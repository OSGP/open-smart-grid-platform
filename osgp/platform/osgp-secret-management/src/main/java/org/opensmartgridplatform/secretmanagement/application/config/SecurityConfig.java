/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.config;

import org.opensmartgridplatform.secretmanagement.application.services.encryption.DefaultEncryptionDelegate;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.EncryptionProviderType;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.providers.EncryptionProvider;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.providers.HsmEncryptionProvider;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.providers.JreEncryptionProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
public class SecurityConfig {

    @Value("${soap.secret.resource}")
    private Resource soapSecretResource;

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
            JreEncryptionProvider jreEncryptionProvider = new JreEncryptionProvider(this.databaseSecretResource.getFile());

            encryptionProviderList.add(jreEncryptionProvider);

            if (this.hsmKeystoreResource.isPresent()) {
                HsmEncryptionProvider hsmEncryptionProvider = new HsmEncryptionProvider(this.hsmKeystoreResource.get().getFile());
                encryptionProviderList.add(hsmEncryptionProvider);
            }

            EncryptionProvider[] encryptionProviderArray = new EncryptionProvider[encryptionProviderList.size()];
            return encryptionProviderList.toArray(encryptionProviderArray);
        }
        catch(IOException e) {
            throw new IllegalStateException("Error creating default encryption providers", e);
        }
    }

    @Bean("SoapSecretEncryptionProvider")
    public EncryptionProvider getSoapSecretEncryptionProvider() {
        try {
            return new JreEncryptionProvider(this.soapSecretResource.getFile());
        } catch (IOException e) {
            throw new IllegalStateException("Could not instantiate JreEncryptionProvider.", e);
        }
    }

    @Bean
    public EncryptionProviderType getEncryptionProviderType() {
        return EncryptionProviderType.valueOf(this.encryptionProviderTypeName);
    }
}