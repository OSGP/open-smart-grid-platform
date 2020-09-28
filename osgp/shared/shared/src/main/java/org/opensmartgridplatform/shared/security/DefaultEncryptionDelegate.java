/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.security;

import java.util.List;

import org.opensmartgridplatform.shared.exceptionhandling.EncrypterException;
import org.opensmartgridplatform.shared.security.providers.EncryptionProvider;
import org.springframework.stereotype.Component;

/**
 * This class is used to encrypt/decrypt typed secrets by either the HardwareSecurityModule or by the JRE
 */
@Component
public class DefaultEncryptionDelegate implements EncryptionDelegate {

    private static final String ERROR_NO_PROVIDER = "Could not find a provider";
    private final List<EncryptionProvider> providers;

    public DefaultEncryptionDelegate(final List<EncryptionProvider> encryptionProviders) {
        this.providers = encryptionProviders;
    }

    @Override
    public EncryptedSecret encrypt(final EncryptionProviderType encryptionProviderType, final byte[] secret,
            final String keyReference) {
        return this.providers.stream().filter(ep -> ep.getType().equals(encryptionProviderType)).findFirst().orElseThrow(
                () -> new EncrypterException(ERROR_NO_PROVIDER)).encrypt(secret, keyReference);
    }

    @Override
    public byte[] decrypt(final EncryptedSecret secret, final String keyReference) {
        return this.providers.stream().filter(ep -> ep.getType().equals(secret.getType())).findFirst().orElseThrow(
                () -> new EncrypterException(ERROR_NO_PROVIDER)).decrypt(secret, keyReference);
    }

    /*@Override
    public Secret generate128BitsSecret(final EncryptionProviderType encryptionProviderType) {
        return this.providers.stream().filter(ep -> ep.getType().equals(encryptionProviderType)).findFirst().orElseThrow(
                () -> new EncrypterException(ERROR_NO_PROVIDER)).;
    }*/
}

