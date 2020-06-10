package org.opensmartgridplatform.secretmgmt.application.config;

import java.io.IOException;

import org.opensmartgridplatform.secretmgmt.application.services.encryption.DefaultEncryptionDelegate;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.EncryptionDelegate;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProvider;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.JreEncryptionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

@Configuration
public class SecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class.getName());

    @Value("${soap.secret.resource}")
    private Resource soapSecretResource;

    @Value("${database.secret.resource}")
    private Resource databaseSecretResource;

    @Value("${encryption.provider.type}")
    private String encryptionProviderTypeName;

    @Bean(name = "MyEncryptionDelegate")
    public EncryptionDelegate getDefaultEncryptionDelegate() throws IllegalStateException {

        try {
            final EncryptionDelegate encryptionDelegate = new DefaultEncryptionDelegate(this.databaseSecretResource.getFile());
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
            final EncryptionProvider encryptionProvider = new JreEncryptionProvider();
            encryptionProvider.setKeystore(this.soapSecretResource.getFile());
            return encryptionProvider;

        } catch (final IOException e) {
            final String errorMessage = String.format("Unexpected exception when reading key for soap protocol. Key Path: %s",
                    this.soapSecretResource.getFilename());
            LOGGER.error(errorMessage);

            throw new IllegalStateException(errorMessage, e);
        }
    }

    @Bean
    public EncryptionProviderType getEncryptionProviderType() {
        try {
            return EncryptionProviderType.valueOf(this.encryptionProviderTypeName);
        } catch(final IllegalArgumentException iae) {
            throw new IllegalStateException(String.format("Unknown encryption provider type '%s'",
                    this.encryptionProviderTypeName), iae);
        }
    }
}