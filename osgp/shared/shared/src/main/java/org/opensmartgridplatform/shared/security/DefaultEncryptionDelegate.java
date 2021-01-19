/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.security;

import java.util.EnumMap;
import java.util.List;

import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.security.providers.EncryptionProvider;
import org.springframework.stereotype.Component;

/**
 * This class is used to encrypt/decrypt typed secrets by either the HardwareSecurityModule or by the JRE
 */
@Component
public class DefaultEncryptionDelegate implements EncryptionDelegate {

    private static final String ERROR_NO_PROVIDER = "Could not find a provider of type %s; available providers are %s";
    private final EnumMap<EncryptionProviderType, EncryptionProvider> encryptionProviders;

    public DefaultEncryptionDelegate(final List<EncryptionProvider> encryptionProviders) {
        this.encryptionProviders = new EnumMap<>(EncryptionProviderType.class);
        encryptionProviders.stream().forEach(p -> this.encryptionProviders.put(p.getType(), p));
    }

    private EncryptionProvider getEncryptionProvider(final EncryptionProviderType type) {
        if (!this.encryptionProviders.containsKey(type)) {
            throw new EncrypterException(String.format(ERROR_NO_PROVIDER, type, this.encryptionProviders));
        }
        return this.encryptionProviders.get(type);
    }

    @Override
    public EncryptedSecret encrypt(final EncryptionProviderType encryptionProviderType, final byte[] secret,
            final String keyReference) {
        return this.getEncryptionProvider(encryptionProviderType).encrypt(secret, keyReference);
    }

    @Override
    public byte[] decrypt(final EncryptedSecret secret, final String keyReference) {
        return this.getEncryptionProvider(secret.getType()).decrypt(secret, keyReference);
    }

    @Override
    public byte[] generateAes128BitsSecret(final EncryptionProviderType encryptionProviderType,
            final String keyReference) {
        return this.getEncryptionProvider(encryptionProviderType).generateAes128BitsSecret(keyReference);
    }

    @Override
    public int getSecretByteLength(final EncryptionProviderType encryptionProviderType) {
        return this.getEncryptionProvider(encryptionProviderType).getSecretByteLength();
    }
}

