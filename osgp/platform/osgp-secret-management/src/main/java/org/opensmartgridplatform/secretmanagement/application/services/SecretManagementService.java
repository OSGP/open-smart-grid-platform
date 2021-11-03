/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.services;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.tomcat.util.buf.HexUtils;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretStatus;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretType;
import org.opensmartgridplatform.secretmanagement.application.domain.TypedSecret;
import org.opensmartgridplatform.secretmanagement.application.exception.ExceptionWrapper;
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptedSecretRepository;
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptionKeyRepository;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.security.EncryptedSecret;
import org.opensmartgridplatform.shared.security.EncryptionDelegate;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.opensmartgridplatform.shared.security.RsaEncrypter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
@Service
public class SecretManagementService {

  @Value("${max.minutes.for.new.key.to.be.activated:5}")
  private Integer maxMinutesForNewKeyToBeActivated;

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
      byte[] aesEncrypted;
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

  private final EncryptionDelegate encryptionDelegate;
  private final EncryptionProviderType encryptionProviderType;
  private final DbEncryptedSecretRepository secretRepository;
  private final DbEncryptionKeyRepository keyRepository;
  private final RsaEncrypter rsaEncrypter;

  public SecretManagementService(
      @Qualifier("DefaultEncryptionDelegate") final EncryptionDelegate defaultEncryptionDelegate,
      final EncryptionProviderType encryptionProviderType,
      final DbEncryptedSecretRepository secretRepository,
      final DbEncryptionKeyRepository keyRepository,
      final RsaEncrypter rsaEncrypter) {
    this.encryptionDelegate = defaultEncryptionDelegate;
    this.encryptionProviderType = encryptionProviderType;
    this.secretRepository = secretRepository;
    this.keyRepository = keyRepository;
    this.rsaEncrypter = rsaEncrypter;
  }

  private DbEncryptionKeyReference getCurrentKey() {
    final Date now = new Date();
    final List<DbEncryptionKeyReference> keyRefs =
        this.keyRepository.findByTypeAndValid(this.encryptionProviderType, now);
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
    return this.keyRepository.findByTypeAndReference(this.encryptionProviderType, reference);
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
    final Date now = new Date();
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
      return secretTypes.stream()
          .map(secretType -> this.retrieveSecret(deviceIdentification, secretType, status))
          .collect(Collectors.toList());
    } catch (final Exception exc) {
      throw new IllegalStateException(
          String.format(
              "Something went wrong retrieving secrets for device %s: %s",
              deviceIdentification, exc.toString()),
          exc);
    }
  }

  private EncryptedTypedSecret retrieveSecret(
      final String deviceIdentification, final SecretType secretType, final SecretStatus status) {
    final Optional<DbEncryptedSecret> optional =
        this.getSingleDbEncryptedSecret(deviceIdentification, secretType, status);
    if (optional.isPresent()) {
      try {
        return EncryptedTypedSecret.fromDbEncryptedSecret(optional.get());
      } catch (final FunctionalException e) {
        throw new ExceptionWrapper(e);
      }
    } else {
      return EncryptedTypedSecret.getNullInstance(secretType);
    }
  }

  private Optional<DbEncryptedSecret> getSingleDbEncryptedSecret(
      final String deviceIdentification,
      final SecretType secretType,
      final SecretStatus secretStatus) {
    final List<DbEncryptedSecret> secretsList =
        this.secretRepository.findSecrets(deviceIdentification, secretType, secretStatus);
    final boolean onlySingleSecretAllowed =
        SecretStatus.NEW.equals(secretStatus) || SecretStatus.ACTIVE.equals(secretStatus);
    if (secretsList.isEmpty()) {
      return Optional.empty();
    } else if (secretsList.size() > 1 && onlySingleSecretAllowed) {
      final String msgFormat =
          "Only 1 instance allowed with status %s, but found %s for device %s, secret type %s";
      throw new IllegalStateException(
          String.format(
              msgFormat, secretStatus, secretsList.size(), deviceIdentification, secretType));
    }
    return Optional.of(secretsList.iterator().next());
  }

  public synchronized void storeSingleNewSecrets(
      final String deviceIdentification, final List<TypedSecret> secrets) {
    final List<TypedSecret> typedSecretsToStore = new ArrayList<>();
    final List<TypedSecret> typedSecretsToReset = new ArrayList<>();
    for (final TypedSecret typedSecret : secrets) {
      final int numberOfNewSecrets =
          this.secretRepository.getSecretCount(
              deviceIdentification, typedSecret.getSecretType(), SecretStatus.NEW);
      if (numberOfNewSecrets == 0) {
        typedSecretsToStore.add(typedSecret);
      } else if (this.recentNewSecretsPresent(
          deviceIdentification, this.maxMinutesForNewKeyToBeActivated, typedSecret)) {
        final String errorMsg =
            "There is/are secrets of type %s for device %s with status NEW "
                + "created less than %d minutes old. No key with status NEW will be stored. Wait "
                + "at least %d minutes before starting a request requiring NEW keys to be stored.";
        throw new IllegalStateException(
            String.format(
                errorMsg,
                typedSecret.getSecretType().name(),
                deviceIdentification,
                this.maxMinutesForNewKeyToBeActivated,
                this.maxMinutesForNewKeyToBeActivated));
      } else {
        typedSecretsToReset.add(typedSecret);
      }
    }
    this.storeSecrets(deviceIdentification, typedSecretsToStore);
    typedSecretsToReset.forEach(
        ts -> this.resetNewSecrets(deviceIdentification, ts.getSecretType()));
  }

  private void resetNewSecrets(final String deviceIdentification, final SecretType secretType) {
    final List<DbEncryptedSecret> foundSecrets =
        this.secretRepository.findSecrets(deviceIdentification, secretType, SecretStatus.NEW);
    final Date mostRecentCreationTime =
        foundSecrets.stream()
            .max((s1, s2) -> s1.getCreationTime().compareTo(s2.getCreationTime()))
            .get()
            .getCreationTime();
    foundSecrets.forEach(
        s -> {
          if (s.getCreationTime().equals(mostRecentCreationTime)) {
            s.setCreationTime(new Date());
          } else {
            s.setSecretStatus(SecretStatus.EXPIRED);
          }
          this.secretRepository.saveAndFlush(s);
        });
  }

  private boolean recentNewSecretsPresent(
      final String deviceIdentification, final long maxMinutesOld, final TypedSecret typedSecret) {
    final List<DbEncryptedSecret> secrets =
        this.secretRepository.findSecrets(
            deviceIdentification, typedSecret.getSecretType(), SecretStatus.NEW);
    return secrets.stream()
            .filter(
                s ->
                    new Date().getTime() - s.getCreationTime().getTime() < (maxMinutesOld * 60000L))
            .count()
        > 0;
  }

  private synchronized void storeSecrets(
      final String deviceIdentification, final List<TypedSecret> secrets) {
    secrets.forEach(s -> this.checkNrNewSecretsOfType(deviceIdentification, s.getSecretType(), 0));
    final List<EncryptedTypedSecret> aesSecrets =
        secrets.stream()
            .map(ts -> new EncryptedTypedSecret(ts.getSecret(), ts.getSecretType()))
            .map(this::reencryptRsa2Aes)
            .collect(toList());
    this.storeAesSecrets(deviceIdentification, aesSecrets);
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

  public synchronized void activateNewSecrets(
      final String deviceIdentification, final List<SecretType> secretTypes) {
    secretTypes.stream().forEach(t -> this.checkNrNewSecretsOfType(deviceIdentification, t, 1));
    secretTypes.stream()
        .map(t -> this.getUpdatedSecretsForActivation(deviceIdentification, t))
        .flatMap(Collection::stream)
        .collect(collectingAndThen(toList(), this.secretRepository::saveAll));
  }

  public boolean hasNewSecret(final String deviceIdentification, final SecretType secretType) {
    return this.secretRepository.getSecretCount(deviceIdentification, secretType, SecretStatus.NEW)
        > 0;
  }

  private List<DbEncryptedSecret> getUpdatedSecretsForActivation(
      final String deviceIdentification, final SecretType secretType) {
    final List<DbEncryptedSecret> updatedSecrets = new ArrayList<>();
    final Optional<DbEncryptedSecret> activeSecretOptional =
        this.getSingleDbEncryptedSecret(deviceIdentification, secretType, SecretStatus.ACTIVE);
    if (activeSecretOptional.isPresent()) {
      final DbEncryptedSecret currentSecret = activeSecretOptional.get();
      currentSecret.setSecretStatus(SecretStatus.EXPIRED);
      updatedSecrets.add(currentSecret);
    }
    final Optional<DbEncryptedSecret> newSecretOptional =
        this.getSingleDbEncryptedSecret(deviceIdentification, secretType, SecretStatus.NEW);
    if (newSecretOptional.isPresent()) {
      final DbEncryptedSecret newSecret = newSecretOptional.get();
      newSecret.setSecretStatus(SecretStatus.ACTIVE);
      updatedSecrets.add(newSecret);
      return updatedSecrets;
    } else {
      throw new IllegalStateException("Cannot activate new secret: no new secret present");
    }
  }

  private void checkNrNewSecretsOfType(
      final String deviceIdentification, final SecretType t, final int expectedNr) {
    final int nrNewSecretsOfType =
        this.secretRepository.getSecretCount(deviceIdentification, t, SecretStatus.NEW);
    if (nrNewSecretsOfType != expectedNr) {
      final String errorMsg =
          "Expected %s new secrets of type %s for device %s, but %s new secret(s) present";
      throw new IllegalStateException(
          String.format(errorMsg, expectedNr, t, deviceIdentification, nrNewSecretsOfType));
    }
  }

  public synchronized List<TypedSecret> generateAndStoreSecrets(
      final String deviceIdentification, final List<SecretType> secretTypes) {
    secretTypes.forEach(st -> this.checkNrNewSecretsOfType(deviceIdentification, st, 0));
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
          this.encryptionDelegate.generateAes128BitsSecret(
              this.encryptionProviderType, currentKey.getReference());
      return new EncryptedTypedSecret(
          aesEncrypted,
          secretType,
          currentKey.getReference(),
          currentKey.getEncryptionProviderType());
    } catch (final EncrypterException ee) {
      throw new IllegalStateException("Eror generating secret", ee);
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
              secret.encryptedSecret, secret.encryptionKeyReference, secret.encryptionProviderType);
      return new EncryptedTypedSecret(rsaEncrypted, secret.type);
    }
  }

  private byte[] reencryptRsa2Aes(final byte[] rsa) {
    // Incoming new secret, so use current key
    final String keyReference = this.getCurrentKey().getReference();
    byte[] aes;
    try {
      aes =
          this.encryptionDelegate
              .encrypt(this.encryptionProviderType, this.rsaEncrypter.decrypt(rsa), keyReference)
              .getSecret();
    } catch (final EncrypterException ee) {
      throw new IllegalStateException(
          "Could not reencrypt secret from RSA to AES: " + ee.toString(), ee);
    }
    return aes;
  }

  private byte[] reencryptAes2Rsa(
      final byte[] aes,
      final String keyReference,
      final EncryptionProviderType encryptionProviderType) {
    try {
      return this.rsaEncrypter.encrypt(
          this.encryptionDelegate.decrypt(
              new EncryptedSecret(encryptionProviderType, aes), keyReference));
    } catch (final EncrypterException ee) {
      throw new IllegalStateException(
          "Could not reencrypt secret from AES to RSA: " + ee.toString(), ee);
    }
  }
}
