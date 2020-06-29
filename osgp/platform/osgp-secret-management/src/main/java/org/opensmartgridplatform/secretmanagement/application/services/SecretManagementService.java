/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.services;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.apache.tomcat.util.buf.HexUtils;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretType;
import org.opensmartgridplatform.secretmanagement.application.domain.TypedSecret;
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptedSecretRepository;
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptionKeyRepository;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.EncryptedSecret;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.EncryptionDelegate;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.EncryptionProviderType;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.Secret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SecretManagementService implements SecretManagement {
    private final EncryptionDelegate encryptionDelegate;
    private final EncryptionProviderType encryptionProviderType;
    private final DbEncryptedSecretRepository secretRepository;
    private final DbEncryptionKeyRepository keyRepository;

    @Autowired
    public SecretManagementService(@Qualifier("DefaultEncryptionDelegate") final EncryptionDelegate defaultEncryptionDelegate,
            final EncryptionProviderType encryptionProviderType, final DbEncryptedSecretRepository secretRepository,
            final DbEncryptionKeyRepository keyRepository) {
        this.encryptionDelegate = defaultEncryptionDelegate;
        this.encryptionProviderType = encryptionProviderType;
        this.secretRepository = secretRepository;
        this.keyRepository = keyRepository;
    }

    @Override
    public void storeSecrets(final String deviceIdentification, final List<TypedSecret> secrets) throws Exception {
        //@formatter:off
        secrets.stream()
                .map(t -> this.validateSecret(deviceIdentification, t))
                .map(s -> this.createEncrypted(deviceIdentification, s, this.getKey()))
                .forEach(this.secretRepository::save);
        //@formatter:on
    }

    private DbEncryptionKeyReference getKey() {
        final Date now = new Date();
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

    @java.lang.SuppressWarnings("squid:S3655")
    public TypedSecret retrieveSecret(final String deviceIdentification, final SecretType secretType) {
        final Date now = new Date();
        final Long secretId = this.secretRepository.findIdOfValidMostRecent(deviceIdentification,
                secretType.name(), now);
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
