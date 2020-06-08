package org.opensmartgridplatform.secretmgmt.application.config;

import org.opensmartgridplatform.secretmgmt.application.services.encryption.DefaultEncryptionDelegate;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.EncryptionDelegate;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProvider;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.JreEncryptionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class.getName());

    @Value("${soap.secret.resource}")
    private Resource soapSecretResource;

    @Value("${database.secret.resource}")
    private Resource databaseSecretResource;

    @Bean(name = "MyEncryptionDelegate")
    public EncryptionDelegate getDefaultEncryptionDelegate() throws IllegalStateException {

        try {
            EncryptionDelegate encryptionDelegate = new DefaultEncryptionDelegate(databaseSecretResource.getFile());
            return encryptionDelegate;
        } catch (final IOException e) {
            final String errorMessage = String.format("Unexpected exception when reading keys. Key Path: %s",
                    this.databaseSecretResource.getFilename());
            LOGGER.error(errorMessage);

            throw new IllegalStateException(errorMessage, e);
        }
    }

    @Bean(name = "SoapSecretEncryptionProvider")
    public EncryptionProvider getSoapSecretEncryptionProvider() throws IllegalStateException {

        try {
            EncryptionProvider encryptionProvider = new JreEncryptionProvider();
            encryptionProvider.setKeystore(this.soapSecretResource.getFile());
            return encryptionProvider;

        } catch (final IOException e) {
            final String errorMessage = String.format("Unexpected exception when reading key for soap protocol. Key Path: %s",
                    this.soapSecretResource.getFilename());
            LOGGER.error(errorMessage);

            throw new IllegalStateException(errorMessage, e);
        }
    }
}