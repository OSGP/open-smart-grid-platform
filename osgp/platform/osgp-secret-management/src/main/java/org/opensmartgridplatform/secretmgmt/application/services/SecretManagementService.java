package org.opensmartgridplatform.secretmgmt.application.services;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmgmt.application.domain.SecretType;
import org.opensmartgridplatform.secretmgmt.application.domain.TypedSecret;
import org.opensmartgridplatform.secretmgmt.application.repository.DbEncryptedSecretRepository;
import org.opensmartgridplatform.secretmgmt.application.repository.DbEncryptionKeyRepository;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.EncryptedSecret;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.EncryptionDelegate;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.Secret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SecretManagementService implements SecretManagement {

    private final EncryptionDelegate encryptionDelegate;
    private final DbEncryptedSecretRepository secretRepository;
    private final DbEncryptionKeyRepository keyRepository;

    @Autowired
    public SecretManagementService(final EncryptionDelegate encryptionDelegate,
            final DbEncryptedSecretRepository secretRepository, final DbEncryptionKeyRepository keyRepository) {
        this.encryptionDelegate = encryptionDelegate;
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
        final Page<DbEncryptionKeyReference> keyRefsPage = this.keyRepository.findValidOrderedByValidFrom(now,
                Pageable.unpaged());
        if (keyRefsPage.getSize() > 1) {
            throw new IllegalStateException("Multiple encryption keys found that are valid at " + now);
        }
        return keyRefsPage.stream().findFirst().orElseThrow(
                () -> new IllegalStateException("No encryption key found that are valid at "+now));
    }

    private DbEncryptedSecret createEncrypted(final String deviceIdentification, final TypedSecret typedSecret,
            final DbEncryptionKeyReference keyReference) {
        final String secretString = typedSecret.getSecret();
        final Secret secret = new Secret(secretString.getBytes()); //TODO check with Erik
        try {
            final EncryptedSecret encryptedSecret = this.encryptionDelegate.encrypt(keyReference.getEncryptionProviderType(),
                    secret);
            final DbEncryptedSecret dbEncryptedSecret = new DbEncryptedSecret();
            dbEncryptedSecret.setDeviceIdentification(deviceIdentification);
            dbEncryptedSecret.setEncodedSecret(encryptedSecret.toString());   //TODO check with Erik
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
        //@formatter:off
        final Map<SecretType,List<DbEncryptedSecret>> secretsByTypeMap = secretTypes.stream()
                .collect(
                    Collectors.toMap(
                        Function.identity(),
                        secretType -> this.secretRepository.findValidOrderedByKeyValidFrom(deviceIdentification
                                , secretType, now, pageable).toList()));
        return secretsByTypeMap.entrySet().stream()
                .map(mapEntry -> this.getTypedSecretFromMapEntry(deviceIdentification, mapEntry))
                .collect(Collectors.toList());
        //@formatter:on
    }

    private TypedSecret getTypedSecretFromMapEntry(final String deviceIdentification,
            final Map.Entry<SecretType, List<DbEncryptedSecret>> mapEntry) {
        final int nrSecretsForType = mapEntry.getValue().size();
        if(nrSecretsForType!=1) {
            throw new IllegalStateException(
                String.format("Illegal number of secrets found for device %s with type %s: %s",
                       deviceIdentification,
                       mapEntry.getKey(),
                       nrSecretsForType));
        }
        return this.getTypedSecret(mapEntry.getValue().get(0));
    }

    private TypedSecret getTypedSecret(final DbEncryptedSecret dbEncryptedSecret) {
        if (dbEncryptedSecret != null) {
            final byte[] secretBytes = dbEncryptedSecret.getEncodedSecret().getBytes(); //TODO check with Erik
            final EncryptedSecret encryptedSecret = new EncryptedSecret(
                    dbEncryptedSecret.getEncryptionKeyReference().getEncryptionProviderType(), secretBytes);
            try {
                final Secret decryptedSecret = this.encryptionDelegate.decrypt(encryptedSecret);
                final TypedSecret typedSecret = new TypedSecret();
                typedSecret.setSecret(decryptedSecret.getSecret().toString()); //TODO check with Erik
                typedSecret.setSecretType(dbEncryptedSecret.getSecretType());
                return typedSecret;
            } catch (final Exception exc) {
                throw new IllegalStateException(
                        "Could not create encrypted secret (id: " + dbEncryptedSecret.getId() + ")", exc);
            }
        } else {    //Should never happen because of stream mapping in retrieveSecret()
            throw new IllegalStateException("Could not create encrypted secret for NULL secret");
        }
    }
}
