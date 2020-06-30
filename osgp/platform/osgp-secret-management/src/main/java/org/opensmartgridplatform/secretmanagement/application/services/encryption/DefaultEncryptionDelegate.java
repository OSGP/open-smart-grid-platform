/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.services.encryption;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.opensmartgridplatform.secretmanagement.application.services.encryption.providers.EncryptionProvider;
import org.springframework.stereotype.Component;

/**
 * This class is used to encrypt/decrypt typed secrets by either the HardwareSecurityModule or by the JRE
 */
@Component
public class DefaultEncryptionDelegate implements EncryptionDelegate {

    private final List<EncryptionProvider> providers;

    public DefaultEncryptionDelegate(final EncryptionProvider[] encryptionProviders) {
        this.providers = Arrays.asList(encryptionProviders);
    }

    @Override
    public EncryptedSecret encrypt(
            final EncryptionProviderType encryptionProviderType, final Secret secret, final String keyReference) {
        final Optional<EncryptionProvider> oep = this.providers.stream().filter(
                ep -> ep.getType().equals(encryptionProviderType)).findFirst();

        //oep.orElseThrow(()->new IllegalStateException("Could not find a provider")).encrypt(secret,keyReference);
        if (oep.isPresent()) {
            return oep.get().encrypt(secret, keyReference);
        } else {
            throw new IllegalStateException("Could not find a provider");
        }
    }

    @Override
    public Secret decrypt(final EncryptedSecret secret, final String keyReference) {
        final EncryptionProviderType encType = secret.getType();
        final Optional<EncryptionProvider> oep = this.providers.stream().filter(ep -> ep.getType().equals(encType)).findFirst();

        if (oep.isPresent()) {
            return oep.get().decrypt(secret, keyReference);
        } else {
            throw new IllegalStateException("Could not find a provider");
        }
    }
}

