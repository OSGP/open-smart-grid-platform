/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.opensmartgridplatform.shared.security.DefaultEncryptionDelegate;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.opensmartgridplatform.shared.security.providers.EncryptionProvider;
import org.opensmartgridplatform.shared.security.providers.HsmEncryptionProvider;
import org.opensmartgridplatform.shared.security.providers.JreEncryptionProvider;
import org.opensmartgridplatform.shared.security.providers.RsaEncryptionProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class SecurityConfig {

    @Value("${soap.public.key.resource:#{null}}")
    private Optional<Resource> soapPublicKeyResource;

    @Value("${soap.private.key.resource:#{null}}")
    private Optional<Resource> soapPrivateKeyResource;

    @Value("${database.secret.resource}")
    private Resource databaseSecretResource;

    @Value("${hsm.keystore.resource:#{null}}")
    private Optional<Resource> hsmKeystoreResource;

    @Value("${encryption.provider.type}")
    private String encryptionProviderTypeName;

    @Bean("DefaultEncryptionDelegate")
    public DefaultEncryptionDelegate getEncryptionDelegate() {
        return new DefaultEncryptionDelegate(getDefaultEncryptionProviders());
    }

    private EncryptionProvider[] getDefaultEncryptionProviders() {

        List<EncryptionProvider> encryptionProviderList = new ArrayList<>();

        try {
            JreEncryptionProvider jreEncryptionProvider = new JreEncryptionProvider(
                    this.databaseSecretResource.getFile());

            encryptionProviderList.add(jreEncryptionProvider);

            File privateKeyStoreFile = this.soapPrivateKeyResource.isPresent() ?
                    this.soapPrivateKeyResource.get().getFile() : null;
            File publicKeyStoreFile = this.soapPublicKeyResource.isPresent() ?
                    this.soapPublicKeyResource.get().getFile() : null;

            RsaEncryptionProvider rsaEncryptionProvider = new RsaEncryptionProvider(privateKeyStoreFile,
                    publicKeyStoreFile);

            encryptionProviderList.add(rsaEncryptionProvider);

            if (this.hsmKeystoreResource.isPresent()) {
                HsmEncryptionProvider hsmEncryptionProvider = new HsmEncryptionProvider(
                        this.hsmKeystoreResource.get().getFile());
                encryptionProviderList.add(hsmEncryptionProvider);
            }

            EncryptionProvider[] encryptionProviderArray = new EncryptionProvider[encryptionProviderList.size()];
            return encryptionProviderList.toArray(encryptionProviderArray);
        } catch (IOException e) {
            throw new IllegalStateException("Error creating default encryption providers", e);
        }
    }

    @Bean
    public EncryptionProviderType getEncryptionProviderType() {
        return EncryptionProviderType.valueOf(this.encryptionProviderTypeName);
    }
}