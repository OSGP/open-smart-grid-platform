package org.opensmartgridplatform.secretmgmt.application.services;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SecretManagementService implements SecretManagement {
    private final static int FIRST_PAGE = 0;
    private final EncryptionDelegate encryptionDelegate;
    private final EncryptionProviderType encryptionProviderType;
    private final DbEncryptedSecretRepository secretRepository;
    private final DbEncryptionKeyRepository keyRepository;

    @Autowired
    public SecretManagementService(final EncryptionDelegate osgpEncryptionDelegate,
            final EncryptionProviderType encryptionProviderType, final DbEncryptedSecretRepository secretRepository,
            final DbEncryptionKeyRepository keyRepository) {
        this.encryptionDelegate = osgpEncryptionDelegate;
        this.encryptionProviderType = encryptionProviderType;
        this.secretRepository = secretRepository;
        this.keyRepository = keyRepository;
    }

    @Override
    public void storeSecrets(final String deviceIdentification, final List<TypedSecret> secrets) throws Exception {
        //@formatter:off
        secrets.stream()
                .map(t -> this.validateSecret(deviceIdentification, t))
                .map(s -> this.createEncrypted(deviceIdentification, s, this.getKey(s)))
                .forEach(this.secretRepository::save);
        //@formatter:on
    }

    private DbEncryptionKeyReference getKey(final TypedSecret typedSecret) {
        final Date now = new Date(); //TODO: UTC?
        final Page<DbEncryptionKeyReference> keyRefsPage = this.keyRepository.findByTypeAndValid(now,
                this.encryptionProviderType, Pageable.unpaged());
        if (keyRefsPage.getSize() > 1) {
            throw new IllegalStateException("Multiple encryption keys found that are valid at " + now);
        }
        return keyRefsPage.stream().findFirst().orElseThrow(
                () -> new NoSuchElementException("No encryption key found that is valid at " + now));
    }

    private TypedSecret validateSecret(final String deviceIdentification, final TypedSecret secret) {
        if (secret.getSecret() == null) {
            throw new IllegalArgumentException("No secret string set");
        } else if (secret.getSecretType() == null) {
            throw new IllegalArgumentException("No secret type set");
        } else if (this.isIdenticalToCurrent(deviceIdentification, secret)) {
            throw new IllegalArgumentException(String.format("Secret is identical to current secret (%s, %s)",
                    deviceIdentification, secret.getSecretType().name()));
        }
        return secret;
    }

    private boolean isIdenticalToCurrent(final String deviceIdentification, final TypedSecret secret) {
        try {
            final TypedSecret current = this.retrieveSecret(deviceIdentification, secret.getSecretType());
            return current.getSecret().equals(secret.getSecret());
        } catch(final NoSuchElementException nsee) {
            //there is no current secret
            return false;
        }
    }

    public void storeKey(final DbEncryptionKeyReference keyReference) {
        //Not implemented yet: has to be implemented after MVP
        /*validateKeyReference(keyReference);
        this.keyRepository.save(keyReference);*/
    }

    private void validateKeyReference(final DbEncryptionKeyReference keyReference) {
        //Not implemented yet: implement after MVP; pre-save validation (check on valid range, ...)
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
            dbEncryptedSecret.setCreationTime(new Date());
            return dbEncryptedSecret;
        } catch (final Exception exc) {
            throw new IllegalStateException("Could not create encrypted secret", exc);
        }
    }

    @Override
    public List<TypedSecret> retrieveSecrets(final String deviceIdentification, final List<SecretType> secretTypes)
            throws Exception {
        try {
            //@formatter:off
            return secretTypes.stream()
                    .map(secretType -> this.retrieveSecret(deviceIdentification,secretType))
                    .collect(Collectors.toList());
            //@formatter:on
        } catch (final Exception exc) {
            throw new IllegalStateException(
                    String.format("Something went wrong retrieving secrets for device %s", deviceIdentification), exc);
        }
    }

    public TypedSecret retrieveSecret(final String deviceIdentification, final SecretType secretType) {
        final Date now = new Date();
        final Long secretId = this.secretRepository.findIdOfValidMostRecent(deviceIdentification,
                secretType.name(), this.encryptionProviderType.name(), now);
        if(secretId==null) {
            throw new NoSuchElementException("No secret found with a valid key");
        }
        return this.getTypedSecret(this.secretRepository.findById(secretId).get());
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
            throw new IllegalStateException("Could not create typed secret for NULL secret");
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
            throw new IllegalStateException("Could not decrypt secret (id: " + dbEncryptedSecret.getId() + ")", exc);
        }
    }
}
