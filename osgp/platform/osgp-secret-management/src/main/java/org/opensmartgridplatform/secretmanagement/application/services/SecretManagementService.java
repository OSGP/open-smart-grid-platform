/**
 * Copyright 2020 Smart Society Services B.V.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.secretmanagement.application.services;

import java.io.IOException;
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
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptedSecretRepository;
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptionKeyRepository;
import org.opensmartgridplatform.shared.security.EncryptedSecret;
import org.opensmartgridplatform.shared.security.EncryptionDelegate;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.opensmartgridplatform.shared.security.Secret;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SecretManagementService {
    private final EncryptionDelegate encryptionDelegate;
    private final EncryptionProviderType encryptionProviderType;
    private final DbEncryptedSecretRepository secretRepository;
    private final DbEncryptionKeyRepository keyRepository;

    public SecretManagementService(
            @Qualifier("DefaultEncryptionDelegate") final EncryptionDelegate defaultEncryptionDelegate,
            final EncryptionProviderType encryptionProviderType, final DbEncryptedSecretRepository secretRepository,
            final DbEncryptionKeyRepository keyRepository) {
        this.encryptionDelegate = defaultEncryptionDelegate;
        this.encryptionProviderType = encryptionProviderType;
        this.secretRepository = secretRepository;
        this.keyRepository = keyRepository;
    }

    private DbEncryptionKeyReference getKey() {
        final Date now = new Date();
        final Page<DbEncryptionKeyReference> keyRefsPage = this.keyRepository.findByTypeAndValid(
                this.encryptionProviderType, now, Pageable.unpaged());
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
            throw new IllegalArgumentException(
                    String.format("Secret is identical to current secret (%s, %s)", deviceIdentification,
                            secret.getSecretType().name()));
        }
        return secret;
    }

    private boolean isIdenticalToCurrent(final String deviceIdentification, final TypedSecret secret) {
        final Optional<TypedSecret> current = this.retrieveCurrentSecret(deviceIdentification, secret.getSecretType());
        return current.isPresent() && current.get().getSecret().equals(secret.getSecret());
    }

    private DbEncryptedSecret createEncrypted(final String deviceIdentification, final TypedSecret typedSecret,
            final DbEncryptionKeyReference keyReference) {
        final String secretString = typedSecret.getSecret();
        final byte[] secretBytes = HexUtils.fromHexString(secretString);
        final Secret secret = new Secret(secretBytes);
        final Date now = new Date(); //TODO check creation & time zone
        try {
            final EncryptedSecret encryptedSecret = this.encryptionDelegate.encrypt(
                    keyReference.getEncryptionProviderType(), secret, keyReference.getReference());
            final DbEncryptedSecret dbEncryptedSecret = new DbEncryptedSecret();
            dbEncryptedSecret.setDeviceIdentification(deviceIdentification);
            dbEncryptedSecret.setEncodedSecret(HexUtils.toHexString(encryptedSecret.getSecret()));
            dbEncryptedSecret.setSecretType(typedSecret.getSecretType());
            dbEncryptedSecret.setSecretStatus(SecretStatus.NEW);
            dbEncryptedSecret.setEncryptionKeyReference(keyReference);
            dbEncryptedSecret.setCreationTime(now);
            return dbEncryptedSecret;
        } catch (final Exception exc) {
            throw new IllegalStateException("Could not create encrypted secret", exc);
        }
    }

    public List<TypedSecret> retrieveSecrets(final String deviceIdentification, final List<SecretType> secretTypes) {
        try {
            //@formatter:off
            return secretTypes.stream()
                    .map(secretType -> this.retrieveCurrentSecret(deviceIdentification, secretType))
                    .map(Optional::get)
                    .collect(Collectors.toList());
            //@formatter:on
        } catch (final Exception exc) {
            throw new IllegalStateException(
                    String.format("Something went wrong retrieving secrets for device %s", deviceIdentification), exc);
        }
    }

    public Optional<TypedSecret> retrieveCurrentSecret(final String deviceIdentification, final SecretType secretType) {
        final Optional<DbEncryptedSecret> encryptedSecret = this.getSingleDbEncryptedSecret(deviceIdentification,
                secretType, SecretStatus.ACTIVE);
        return encryptedSecret.isPresent() ? Optional.of(this.getTypedSecret(encryptedSecret.get())) : Optional.empty();

    }

    private Optional<DbEncryptedSecret> getSingleDbEncryptedSecret(final String deviceIdentification,
            final SecretType secretType, final SecretStatus secretStatus) {
        final Page<DbEncryptedSecret> secretsPage = this.secretRepository.findSecrets(deviceIdentification, secretType,
                secretStatus, Pageable.unpaged());
        if (secretsPage.getTotalElements() == 0) {
            return Optional.empty();
        } else if (secretsPage.getTotalElements() > 1) {
            throw new IllegalStateException(); //TODO set proper msg
        }
        return Optional.of(secretsPage.iterator().next());
    }

    private TypedSecret getTypedSecret(final DbEncryptedSecret dbEncryptedSecret) {
        final DbEncryptionKeyReference keyReference = dbEncryptedSecret.getEncryptionKeyReference();
        if (keyReference == null) {
            throw new IllegalStateException("Could not create encrypted secret: secret has no key reference");
        }
        final byte[] secretBytes = HexUtils.fromHexString(dbEncryptedSecret.getEncodedSecret());
        final EncryptedSecret encryptedSecret = new EncryptedSecret(keyReference.getEncryptionProviderType(),
                secretBytes);
        return this.createTypedSecret(dbEncryptedSecret, keyReference, encryptedSecret);
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

    public synchronized Long storeSecret(final String deviceIdentification, final TypedSecret typedSecret)
            throws IOException {
        final Date now = new Date(); //TODO check creation & time zone
        final SecretType secretType = typedSecret.getSecretType();
        if (this.secretRepository.getSecretCount(deviceIdentification, secretType, SecretStatus.NEW) > 0) {
            final String errorMsg = "Cannot store new secret: a new secret of type %s is already present";
            throw new IllegalStateException(String.format(errorMsg, secretType.name()));
        }
        this.validateSecret(deviceIdentification, typedSecret);
        DbEncryptedSecret encryptedSecret = this.createEncrypted(deviceIdentification, typedSecret, this.getKey());
        try {
            encryptedSecret = this.secretRepository.save(encryptedSecret);
            return encryptedSecret.getId();
        } catch (final DataAccessException dae) {
            throw new IOException("Could not store new key in database", dae);
        }
    }

    public synchronized void activateNewSecret(final String deviceIdentification, final SecretType secretType) {
        final Date now = new Date(); //TODO check creation & time zone
        final int nrNewSecrets = this.secretRepository.getSecretCount(deviceIdentification, secretType,
                SecretStatus.NEW);
        if (nrNewSecrets == 0) {
            throw new IllegalStateException("Cannot activate new secret: no new secret is present");
        } else if (nrNewSecrets > 1) { //should not be possible
            throw new IllegalStateException("Cannot activate new secret: multiple new secrets are present");
        }
        final Optional<DbEncryptedSecret> currentSecretOptional = this.getSingleDbEncryptedSecret(deviceIdentification,
                secretType, SecretStatus.ACTIVE);
        if (currentSecretOptional.isPresent()) {
            final DbEncryptedSecret currentSecret = currentSecretOptional.get();
            currentSecret.setSecretStatus(SecretStatus.EXPIRED);
            this.secretRepository.save(currentSecret);
        }
        final DbEncryptedSecret newSecret = this.getSingleDbEncryptedSecret(deviceIdentification, secretType,
                SecretStatus.NEW).get(); //We have checked nr of new secrets already
        newSecret.setSecretStatus(SecretStatus.ACTIVE);
        this.secretRepository.save(newSecret);
    }
}
