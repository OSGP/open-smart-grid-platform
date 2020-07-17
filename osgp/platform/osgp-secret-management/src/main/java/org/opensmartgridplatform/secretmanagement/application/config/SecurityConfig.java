/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.config;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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

    @Value("${jre.encryption.key.resource}")
    private Resource jreEncryptionKeyResource;

    @Value("${hsm.keystore.resource:#{null}}")
    private Optional<Resource> hsmKeystoreResource;

    @Value("${encryption.provider.type}")
    private String encryptionProviderTypeName;

    @Bean("DefaultEncryptionDelegate")
    public DefaultEncryptionDelegate getEncryptionDelegate() {
        return new DefaultEncryptionDelegate(getDefaultEncryptionProviders());
    }

    @Bean
    public List<EncryptionProvider> getDefaultEncryptionProviders() {

        List<EncryptionProvider> encryptionProviderList = new ArrayList<>();

        try {
            JreEncryptionProvider jreEncryptionProvider = new JreEncryptionProvider(
                    this.jreEncryptionKeyResource.getFile());

            encryptionProviderList.add(jreEncryptionProvider);

            RsaEncryptionProvider rsaEncryptionProvider = new RsaEncryptionProvider();

            this.soapPrivateKeyResource.ifPresent((res)-> {
                try {
                    rsaEncryptionProvider.setPrivateKeyStore(res.getFile());
                }
                catch (IOException e) {
                    throw new IllegalStateException("Could not load private key resource.", e);
                }
            });

            this.soapPublicKeyResource.ifPresent((res)-> {
                try {
                    rsaEncryptionProvider.setPublicKeyStore(res.getFile());
                }
                catch (IOException e) {
                    throw new IllegalStateException("Could not load public key resource.", e);
                }
            });

            encryptionProviderList.add(rsaEncryptionProvider);

            if (this.hsmKeystoreResource.isPresent()) {
                HsmEncryptionProvider hsmEncryptionProvider = new HsmEncryptionProvider(
                        this.hsmKeystoreResource.get().getFile());
                encryptionProviderList.add(hsmEncryptionProvider);
            }

            return encryptionProviderList;

        } catch (IOException e) {
            throw new IllegalStateException("Error creating default encryption providers", e);
        }
    }

    @Bean
    public EncryptionProviderType getEncryptionProviderType() {
        return EncryptionProviderType.valueOf(this.encryptionProviderTypeName);
    }
}