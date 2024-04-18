// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.secretmanagement.application.services;

import static java.util.Collections.reverseOrder;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretStatus;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretType;
import org.opensmartgridplatform.secretmanagement.application.domain.TypedSecret;
import org.opensmartgridplatform.secretmanagement.application.exception.ExceptionWrapper;
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptedSecretRepository;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.security.EncryptedSecret;
import org.opensmartgridplatform.shared.security.EncryptionDelegate;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.opensmartgridplatform.shared.security.RsaEncrypter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service that manages secrets (store, retrieve, activate, generate). Secrets in this service are
 * always AES-encrypted secrets of type EncryptedTypedSecret. Methods that manipulate the secrets in
 * the database (store/activate) are mostly batch-wise and are implemented transactionally: they do
 * their pre-processing and finally persist any changes in transactional repository saveAll() call,
 * in order to prevent database corruption as a result of partially succeeded batch results.
 *
 * <p>Keep in mind that all public methods assume (externally provided) RSA-encrypted secrets of
 * type TypedSecret, so these secrets are always reencrypted to AES before being processed any
 * further in order to keep unencrypted secret in JVM memory/heap for the shortest possible time.
 * Likewise, any public method will return RSA-encrypted secrets by reencrypting the AES-encrypted
 * secrets to RSA.
 */
@Slf4j
@Service
@Transactional
public class SecretManagementService {

  // Internal datastructure to keep track of (intermediate) secret details
  private static class EncryptedTypedSecret {

    byte[] encryptedSecret;

    SecretType type;

    String encryptionKeyReference; // NULL when RSA

    EncryptionProviderType encryptionProviderType; // NULL when RSA

    private EncryptedTypedSecret(final SecretType type) {
      this.type = type;
    }

    private EncryptedTypedSecret(final byte[] secret, final SecretType type) {
      this(secret, type, null, null);
    }

    private EncryptedTypedSecret(
        final byte[] secret,
        final SecretType type,
        final String encryptionKeyReference,
        final EncryptionProviderType encryptionProviderType) {
      if (secret == null) {
        throw new IllegalArgumentException("No NULL value allowed for parameter 'secret'");
      } else if (type == null) {
        throw new IllegalArgumentException("No NULL value allowed for parameter 'secretType'");
      }
      this.encryptedSecret = secret;
      this.type = type;
      this.encryptionKeyReference = encryptionKeyReference;
      this.encryptionProviderType = encryptionProviderType;
    }

    private boolean hasNullSecret() {
      return this.encryptedSecret == null;
    }

    private TypedSecret toTypedSecret() {
      return new TypedSecret(this.encryptedSecret, this.type);
    }

    private static EncryptedTypedSecret getNullInstance(final SecretType type) {
      return new EncryptedTypedSecret(type);
    }

    private static EncryptedTypedSecret fromDbEncryptedSecret(
        final DbEncryptedSecret dbEncryptedSecret) throws FunctionalException {
      final byte[] aesEncrypted;
      try {
        aesEncrypted = HexUtils.fromHexString(dbEncryptedSecret.getEncodedSecret());
      } catch (final IllegalArgumentException iae) {
        throw new FunctionalException(
            FunctionalExceptionType.INVALID_KEY_FORMAT, ComponentType.SECRET_MANAGEMENT, iae);
      }
      final String keyReference = dbEncryptedSecret.getEncryptionKeyReference().getReference();
      final EncryptionProviderType providerType =
          dbEncryptedSecret.getEncryptionKeyReference().getEncryptionProviderType();
      return new EncryptedTypedSecret(
          aesEncrypted, dbEncryptedSecret.getSecretType(), keyReference, providerType);
    }
  }

  private final EncryptionDelegate encryptionDelegateForKeyStorage;

  private final EncryptionProviderType encryptionProviderType;

  private final DbEncryptedSecretRepository secretRepository;

  private final EncryptionKeyReferenceCacheService encryptionKeyReferenceCacheService;

  private final RsaEncrypter encrypterForSecretManagementClient;

  private final RsaEncrypter decrypterForSecretManagement;

  private final SecretManagementMetrics secretManagementMetrics;

  public SecretManagementService(
      @Qualifier("DefaultEncryptionDelegateForKeyStorage") final EncryptionDelegate defaultEncryptionDelegateForKeyStorage,
      final EncryptionProviderType encryptionProviderType,
      final DbEncryptedSecretRepository secretRepository,
      final EncryptionKeyReferenceCacheService encryptionKeyReferenceCacheService,
      @Qualifier(value = "encrypterForSecretManagementClient") final RsaEncrypter encrypterForSecretManagementClient,
      @Qualifier(value = "decrypterForSecretManagement") final RsaEncrypter decrypterForSecretManagement,
      final SecretManagementMetrics secretManagementMetrics) {
    this.encryptionDelegateForKeyStorage = defaultEncryptionDelegateForKeyStorage;
    this.encryptionProviderType = encryptionProviderType;
    this.secretRepository = secretRepository;
    this.encryptionKeyReferenceCacheService = encryptionKeyReferenceCacheService;
    this.encrypterForSecretManagementClient = encrypterForSecretManagementClient;
    this.decrypterForSecretManagement = decrypterForSecretManagement;
    this.secretManagementMetrics = secretManagementMetrics;
  }

  private DbEncryptionKeyReference getCurrentKey() {
    final Instant now = Instant.now();
    final List<DbEncryptionKeyReference> keyRefs =
        this.encryptionKeyReferenceCacheService.findAllByTypeAndValid(
            this.encryptionProviderType, now);
    if (keyRefs.size() > 1) {
      final String messageFormat = "Multiple encryption keys found of type %s that are valid at %s";
      throw new IllegalStateException(
          String.format(messageFormat, this.encryptionProviderType, now));
    } else if (keyRefs.isEmpty()) {
      final String messageFormat = "No encryption key of type %s found that is valid at %s";
      throw new NoSuchElementException(
          String.format(messageFormat, this.encryptionProviderType, now));
    }
    return keyRefs.get(0);
  }

  private DbEncryptionKeyReference getKeyByReference(final String reference) {
    return this.encryptionKeyReferenceCacheService.getKeyByReference(
        this.encryptionProviderType, reference);
  }

  private EncryptedTypedSecret validateAndReturnNewSecret(final EncryptedTypedSecret secret) {
    if (secret.hasNullSecret()) {
      final FunctionalExceptionType excType = FunctionalExceptionType.KEY_NOT_PRESENT;
      throw new ExceptionWrapper(new FunctionalException(excType, ComponentType.SECRET_MANAGEMENT));
    }
    return secret;
  }

  private DbEncryptedSecret createDbEncrypted(
      final String deviceIdentification,
      final EncryptedTypedSecret secret,
      final DbEncryptionKeyReference keyReference) {
    final Instant now = Instant.now();
    final DbEncryptedSecret dbEncryptedSecret = new DbEncryptedSecret();
    dbEncryptedSecret.setDeviceIdentification(deviceIdentification);
    dbEncryptedSecret.setEncodedSecret(HexUtils.toHexString(secret.encryptedSecret));
    dbEncryptedSecret.setSecretType(secret.type);
    dbEncryptedSecret.setSecretStatus(SecretStatus.NEW);
    dbEncryptedSecret.setEncryptionKeyReference(keyReference);
    dbEncryptedSecret.setCreationTime(now);
    return dbEncryptedSecret;
  }

  public List<TypedSecret> retrieveSecrets(
      final String deviceIdentification, final List<SecretType> secretTypes) {
    return this.doRetrieveSecrets(deviceIdentification, secretTypes, SecretStatus.ACTIVE);
  }

  public List<TypedSecret> retrieveNewSecrets(
      final String deviceIdentification, final List<SecretType> secretTypes) {
    return this.doRetrieveSecrets(deviceIdentification, secretTypes, SecretStatus.NEW);
  }

  private List<TypedSecret> doRetrieveSecrets(
      final String deviceIdentification,
      final List<SecretType> secretTypes,
      final SecretStatus status) {
    return this.retrieveAesSecrets(deviceIdentification, secretTypes, status).stream()
        .map(this::reencryptAes2Rsa)
        .map(EncryptedTypedSecret::toTypedSecret)
        .collect(Collectors.toList());
  }

  private List<EncryptedTypedSecret> retrieveAesSecrets(
      final String deviceIdentification,
      final List<SecretType> secretTypes,
      final SecretStatus status) {
    try {
      return this.retrieveSecrets(deviceIdentification, secretTypes, status);
    } catch (final Exception exc) {
      throw new IllegalStateException(
          String.format(
              "Something went wrong retrieving secrets for device %s: %s",
              deviceIdentification, exc.toString()),
          exc);
    }
  }

  private List<EncryptedTypedSecret> retrieveSecrets(
      final String deviceIdentification,
      final List<SecretType> secretTypes,
      final SecretStatus status) {

    final Map<SecretType, Optional<DbEncryptedSecret>> dbEncryptedSecretByType =
        this.getValidatedDbEncryptedSecretByType(deviceIdentification, secretTypes, status);

    return secretTypes.stream()
        .map(
            secretType ->
                this.mapAsEncryptedTypedSecret(secretType, dbEncryptedSecretByType.get(secretType)))
        .toList();
  }

  private EncryptedTypedSecret mapAsEncryptedTypedSecret(
      final SecretType secretType, final Optional<DbEncryptedSecret> optionalDbEncryptedSecret) {
    if (optionalDbEncryptedSecret.isPresent()) {
      try {
        return EncryptedTypedSecret.fromDbEncryptedSecret(optionalDbEncryptedSecret.get());
      } catch (final FunctionalException e) {
        throw new ExceptionWrapper(e);
      }
    } else {
      return EncryptedTypedSecret.getNullInstance(secretType);
    }
  }

  private EnumMap<SecretType, Optional<DbEncryptedSecret>> getValidatedDbEncryptedSecretByType(
      final String deviceIdentification,
      final List<SecretType> secretTypes,
      final SecretStatus secretStatus) {

    if (secretTypes.isEmpty()) {
      return new EnumMap<>(SecretType.class);
    }

    final Map<SecretType, List<DbEncryptedSecret>> dbEncryptedSecretByType =
        this.secretRepository.findSecrets(deviceIdentification, secretTypes, secretStatus).stream()
            .collect(groupingBy(DbEncryptedSecret::getSecretType));

    validateSecrets(dbEncryptedSecretByType, deviceIdentification, secretTypes, secretStatus);

    final EnumMap<SecretType, Optional<DbEncryptedSecret>> validatedDbEncryptedSecretByType =
        new EnumMap<>(SecretType.class);
    secretTypes.forEach(
        secretType -> {
          final Optional<DbEncryptedSecret> optionalDbEncryptedSecret =
              dbEncryptedSecretByType.getOrDefault(secretType, new ArrayList<>()).stream()
                  .min(
                      Comparator.comparing(DbEncryptedSecret::getCreationTime, reverseOrder())
                          .thenComparing(DbEncryptedSecret::getId, reverseOrder()));

          validatedDbEncryptedSecretByType.put(secretType, optionalDbEncryptedSecret);
        });
    return validatedDbEncryptedSecretByType;
  }

  private static void validateSecrets(
      final Map<SecretType, List<DbEncryptedSecret>> dbEncryptedSecretByType,
      final String deviceIdentification,
      final List<SecretType> secretTypes,
      final SecretStatus secretStatus) {

    final boolean onlySingleSecretByTypeAllowed =
        SecretStatus.NEW.equals(secretStatus) || SecretStatus.ACTIVE.equals(secretStatus);

    secretTypes.forEach(
        secretType -> {
          final List<DbEncryptedSecret> secretsListForType =
              dbEncryptedSecretByType.getOrDefault(secretType, new ArrayList<>());
          if (secretsListForType.size() > 1 && onlySingleSecretByTypeAllowed) {
            final String msgFormat =
                "Only 1 instance allowed with status %s, but found %s for device %s, secret type %s";
            throw new IllegalStateException(
                String.format(
                    msgFormat,
                    secretStatus,
                    secretsListForType.size(),
                    deviceIdentification,
                    secretType));
          }
        });
  }

  public void storeSecrets(
      final String deviceIdentification, final List<TypedSecret> typedSecrets) {

    final List<SecretType> secretTypeList =
        typedSecrets.stream().map(TypedSecret::getSecretType).toList();
    this.withdrawExistingKeysWithStatusNew(deviceIdentification, secretTypeList);

    final List<EncryptedTypedSecret> aesSecrets =
        typedSecrets.stream()
            .map(ts -> new EncryptedTypedSecret(ts.getSecret(), ts.getSecretType()))
            .map(this::reencryptRsa2Aes)
            .collect(toList());
    this.storeAesSecrets(deviceIdentification, aesSecrets);
  }

  private int withdrawExistingKeysWithStatusNew(
      final String deviceIdentification, final List<SecretType> secretTypes) {
    if (secretTypes.isEmpty()) {
      return 0;
    }
    // All NEW keys of the device and type are set to status WITHDRAWN.
    return this.secretRepository.withdrawSecretsWithStatusNew(deviceIdentification, secretTypes);
  }

  private void storeAesSecrets(
      final String deviceIdentification, final List<EncryptedTypedSecret> secrets) {
    secrets.stream()
        .map(this::validateAndReturnNewSecret)
        .map(
            ets ->
                this.createDbEncrypted(
                    deviceIdentification, ets, this.getKeyByReference(ets.encryptionKeyReference)))
        .collect(collectingAndThen(toList(), this.secretRepository::saveAll));
  }

  public boolean hasNewSecret(final String deviceIdentification, final SecretType secretType) {
    return this.secretRepository.getSecretCount(deviceIdentification, secretType, SecretStatus.NEW)
        > 0;
  }

  public void activateNewSecrets(
      final String deviceIdentification, final List<SecretType> secretTypes) {
    final Map<SecretType, Optional<DbEncryptedSecret>> dbEncryptedSecretsNew =
        this.getValidatedDbEncryptedSecretByType(
            deviceIdentification, secretTypes, SecretStatus.NEW);
    final Map<SecretType, Optional<DbEncryptedSecret>> dbEncryptedSecretsActive =
        this.getValidatedDbEncryptedSecretByType(
            deviceIdentification, secretTypes, SecretStatus.ACTIVE);

    secretTypes.stream()
        .map(
            t ->
                this.getUpdatedSecretsForActivation(
                    dbEncryptedSecretsNew.get(t), dbEncryptedSecretsActive.get(t), t))
        .flatMap(Collection::stream)
        .collect(collectingAndThen(toList(), this.secretRepository::saveAll));
  }

  private List<DbEncryptedSecret> getUpdatedSecretsForActivation(
      final Optional<DbEncryptedSecret> newSecretOptional,
      final Optional<DbEncryptedSecret> activeSecretOptional,
      final SecretType secretType) {
    final List<DbEncryptedSecret> updatedSecrets = new ArrayList<>();

    if (newSecretOptional.isPresent()) {
      if (activeSecretOptional.isPresent()) {
        final DbEncryptedSecret currentSecret = activeSecretOptional.get();
        currentSecret.setSecretStatus(SecretStatus.EXPIRED);
        updatedSecrets.add(currentSecret);
      }
      final DbEncryptedSecret newSecret = newSecretOptional.get();
      newSecret.setSecretStatus(SecretStatus.ACTIVE);
      updatedSecrets.add(newSecret);
    } else {
      log.info("No new secret of secret type {} present for activation.", secretType);
    }
    return updatedSecrets;
  }

  public List<TypedSecret> generateAndStoreSecrets(
      final String deviceIdentification, final List<SecretType> secretTypes) {

    this.withdrawExistingKeysWithStatusNew(deviceIdentification, secretTypes);
    final List<EncryptedTypedSecret> encryptedTypedSecrets =
        secretTypes.stream().map(this::generateAes128BitsSecret).collect(Collectors.toList());

    this.storeAesSecrets(deviceIdentification, encryptedTypedSecrets);

    return encryptedTypedSecrets.stream()
        .map(this::reencryptAes2Rsa)
        .map(EncryptedTypedSecret::toTypedSecret)
        .collect(Collectors.toList());
  }

  private EncryptedTypedSecret generateAes128BitsSecret(final SecretType secretType) {
    try {
      final DbEncryptionKeyReference currentKey = this.getCurrentKey();
      final byte[] aesEncrypted =
          this.encryptionDelegateForKeyStorage.generateAes128BitsSecret(
              this.encryptionProviderType, currentKey.getReference());
      return new EncryptedTypedSecret(
          aesEncrypted,
          secretType,
          currentKey.getReference(),
          currentKey.getEncryptionProviderType());
    } catch (final EncrypterException ee) {
      throw this.handleEncrypterException("Eror generating secret", ee);
    }
  }

  private EncryptedTypedSecret reencryptRsa2Aes(final EncryptedTypedSecret secret) {
    final byte[] aesEncrypted = this.reencryptRsa2Aes(secret.encryptedSecret);
    final DbEncryptionKeyReference currentKey = this.getCurrentKey();
    return new EncryptedTypedSecret(
        aesEncrypted,
        secret.type,
        currentKey.getReference(),
        currentKey.getEncryptionProviderType());
  }

  private EncryptedTypedSecret reencryptAes2Rsa(final EncryptedTypedSecret secret) {
    if (secret.hasNullSecret()) {
      return secret; // No need to encrypt NULL value
    } else {
      final byte[] rsaEncrypted =
          this.reencryptAes2Rsa(
              secret.encryptedSecret, secret.encryptionKeyReference, secret.encryptionProviderType,
              secret.type);
      return new EncryptedTypedSecret(rsaEncrypted, secret.type);
    }
  }

  private byte[] reencryptRsa2Aes(final byte[] rsa) {
    // Incoming new secret, so use current key
    final String keyReference = this.getCurrentKey().getReference();
    final byte[] aes;
    try {
      aes =
          this.encryptionDelegateForKeyStorage
              .encrypt(
                  this.encryptionProviderType,
                  this.decrypterForSecretManagement.decrypt(rsa),
                  keyReference)
              .getSecret();
    } catch (final EncrypterException ee) {
      throw this.handleEncrypterException(
          "Could not reencrypt secret from RSA to AES: " + ee.toString(), ee);
    }
    return aes;
  }

  private byte[] reencryptAes2Rsa(
      final byte[] aes,
      final String keyReference,
      final EncryptionProviderType encryptionProviderType,
      final SecretType secretType) {
    try {
      final byte[] decrypted = this.encryptionDelegateForKeyStorage.decrypt(
          new EncryptedSecret(encryptionProviderType, aes), keyReference);
      final byte[] encrypted = this.encrypterForSecretManagementClient.encrypt(decrypted);

      log.debug(
          "\nSECRETS LOGGING -> secret type: {}, provider type: {}, key reference: {} "
              + "\nAES encrypted (DB)  : {} "
              + "\ndecrypted           : {} "
              + "\nRSA encrypted (SOAP): {}",
          secretType.name(), encryptionProviderType.name(), keyReference, HexUtils.toHexString(aes),
          HexUtils.toHexString(decrypted), HexUtils.toHexString(encrypted));

      return encrypted;
    } catch (final EncrypterException ee) {
      throw this.handleEncrypterException(
          "Could not reencrypt secret from AES to RSA: " + ee.toString(), ee);
    }
  }

  /*
   * com.ncipher.provider.nCCommunicationException is loaded into JVM by HSM.
   * JAR is not standard available in maven repo
   * Exception checking on class name is possible without importing jar
   */
  private IllegalStateException handleEncrypterException(
      final String message, final EncrypterException encrypterException) {
    this.secretManagementMetrics.incrementEncrypterException(encrypterException);

    return new IllegalStateException(message, encrypterException);
  }
}
