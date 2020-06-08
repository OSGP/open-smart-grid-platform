package org.opensmartgridplatform.secretmgmt.application.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.buf.HexUtils;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmgmt.application.domain.SecretType;
import org.opensmartgridplatform.secretmgmt.application.domain.TypedSecret;
import org.opensmartgridplatform.secretmgmt.application.repository.DbEncryptedSecretRepository;
import org.opensmartgridplatform.secretmgmt.application.repository.DbEncryptionKeyRepository;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.EncryptedSecret;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.EncryptionDelegate;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.Secret;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SecretManagementService implements SecretManagement {

    private final EncryptionDelegate encryptionDelegate;
    private final DbEncryptedSecretRepository secretRepository;
    private final DbEncryptionKeyRepository keyRepository;

    @Value("${encryption.provider.type}")
    String encryptionProviderTypeName;

    @Autowired
    public SecretManagementService(final EncryptionDelegate myEncryptionDelegate,
            final DbEncryptedSecretRepository secretRepository, final DbEncryptionKeyRepository keyRepository) {
        this.encryptionDelegate = myEncryptionDelegate;
        this.secretRepository = secretRepository;
        this.keyRepository = keyRepository;
    }

    @Override
    public void storeSecrets(final String deviceIdentification, final List<TypedSecret> secrets) throws Exception {
        //@formatter:off
        secrets.stream()
                .map(s -> this.createEncrypted(deviceIdentification, s, this.getKey(s)))
                .forEach(e -> this.secretRepository.save(e));
        //@formatter:on
    }

    private DbEncryptionKeyReference getKey(final TypedSecret typedSecret) {
        final Date now = new Date(); //TODO: UTC?
        final EncryptionProviderType ept = this.getConfiguredEncryptionProviderType();
        final Page<DbEncryptionKeyReference> keyRefsPage = this.keyRepository.findByTypeAndValid(now, ept,
                Pageable.unpaged());
        if (keyRefsPage.getSize() > 1) {
            throw new IllegalStateException("Multiple encryption keys found that are valid at " + now);
        }
        return keyRefsPage.stream().findFirst().orElseThrow(
                () -> new IllegalStateException("No encryption key found that are valid at " + now));
    }

    private EncryptionProviderType getConfiguredEncryptionProviderType() {
        try {
            return EncryptionProviderType.valueOf(this.encryptionProviderTypeName);
        } catch (final Exception exc) {
            throw new IllegalStateException(String.format("Could not determine encryption type; configured is '%s'",
                    this.encryptionProviderTypeName), exc);
        }
    }

    private DbEncryptedSecret createEncrypted(final String deviceIdentification, final TypedSecret typedSecret,
            final DbEncryptionKeyReference keyReference) {
        final String secretString = typedSecret.getSecret();
        final byte[] secretBytes = HexUtils.fromHexString(secretString);
        final Secret secret = new Secret(secretBytes);
        try {
            final EncryptedSecret encryptedSecret = this.encryptionDelegate.encrypt(
                    keyReference.getEncryptionProviderType(), secret, keyReference.getReference());
            final DbEncryptedSecret dbEncryptedSecret = new DbEncryptedSecret();
            dbEncryptedSecret.setDeviceIdentification(deviceIdentification);
            dbEncryptedSecret.setEncodedSecret(HexUtils.toHexString(encryptedSecret.getSecret()));
            dbEncryptedSecret.setSecretType(typedSecret.getSecretType());
            dbEncryptedSecret.setEncryptionKeyReference(keyReference);
            return dbEncryptedSecret;
        } catch (final Exception exc) {
            throw new IllegalStateException("Could not create encrypted secret", exc);
        }
    }

    @Override
    public List<TypedSecret> retrieveSecrets(final String deviceIdentification, final List<SecretType> secretTypes)
            throws Exception {
        final Pageable pageable = Pageable.unpaged();
        final Date now = new Date(); //TODO: UTC?
        final Map<SecretType, List<DbEncryptedSecret>> secretsByTypeMap = new HashMap<>();
        try {
            for (final SecretType secretType : secretTypes) {
                secretsByTypeMap.put(secretType,
                        this.secretRepository.findValidOrderedByKeyValidFrom(deviceIdentification, secretType,
                                this.getConfiguredEncryptionProviderType(), now, pageable).toList());
            }
            return this.getTypedSecretsFromDbEncryptedSecrets(secretsByTypeMap);
        } catch (final Exception exc) {
            throw new IllegalStateException(
                    String.format("Something went wrong retrieving secrets for device %s", deviceIdentification), exc);
        }
    }

    private List<TypedSecret> getTypedSecretsFromDbEncryptedSecrets(
            final Map<SecretType, List<DbEncryptedSecret>> secretsByTypeMap) {
        final List<TypedSecret> typedSecrets = new ArrayList<>();
        for (final SecretType secretType : secretsByTypeMap.keySet()) {
            final List<DbEncryptedSecret> dbEncryptedSecrets = secretsByTypeMap.get(secretType);
            final int nrSecretsForType = dbEncryptedSecrets.size();
            if (nrSecretsForType != 1) {
                throw new IllegalStateException(
                        String.format("Illegal number of secrets found with type %s: %s", secretType,
                                nrSecretsForType));
            }
            typedSecrets.add(this.getTypedSecret(dbEncryptedSecrets.get(0)));
        }
        return typedSecrets;
    }

    private TypedSecret getTypedSecret(final DbEncryptedSecret dbEncryptedSecret) {
        if (dbEncryptedSecret != null) {
            final DbEncryptionKeyReference keyReference = dbEncryptedSecret.getEncryptionKeyReference();
            if (keyReference == null) {
                throw new IllegalStateException("Could not create encrypted secret: secret has no key reference");
            }
            final byte[] secretBytes = HexUtils.fromHexString(dbEncryptedSecret.getEncodedSecret());
            final EncryptedSecret encryptedSecret = new EncryptedSecret(keyReference.getEncryptionProviderType(),
                    secretBytes);
            return this.createTypedSecret(dbEncryptedSecret, keyReference, encryptedSecret);
        } else {    //Should never happen because of stream mapping in retrieveSecrets()
            throw new IllegalStateException("Could not create encrypted secret for NULL secret");
        }
    }

    private TypedSecret createTypedSecret(final DbEncryptedSecret dbEncryptedSecret,
            final DbEncryptionKeyReference keyReference, final EncryptedSecret encryptedSecret) {
        try {
            final Secret decryptedSecret = this.encryptionDelegate.decrypt(encryptedSecret,
                    keyReference.getReference());
            final TypedSecret typedSecret = new TypedSecret();
            typedSecret.setSecret(HexUtils.toHexString(decryptedSecret.getSecret()));
            typedSecret.setSecretType(dbEncryptedSecret.getSecretType());
            return typedSecret;
        } catch (final Exception exc) {
            throw new IllegalStateException("Could not create encrypted secret (id: " + dbEncryptedSecret.getId() + ")",
                    exc);
        }
    }
}
