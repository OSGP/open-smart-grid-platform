package org.opensmartgridplatform.secretmgmt.application.config;

import org.apache.tomcat.util.buf.HexUtils;
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
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class SecurityConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class.getName());

    @Value("${soap.secret.resource}")
    private Resource soapSecretResource;

    @Value("${database.secret.resource}")
    private Resource databaseSecretResource;

    @Bean(name = "DefaultEncryptionDelegate")
    public EncryptionDelegate getDefaultEncryptionDelegate() throws IllegalStateException {

        try {
            byte[] key = Files.readAllBytes(Paths.get(this.databaseSecretResource.getFile().getAbsolutePath()));
            String encodedKey = HexUtils.toHexString(key);

            EncryptionDelegate encryptionDelegate = new DefaultEncryptionDelegate(encodedKey);

            return encryptionDelegate;
        } catch (final IOException e) {
            final String errorMessage = String.format("Unexpected exception when reading keys. Key Path: %s",
                    this.databaseSecretResource.getFilename());
            LOGGER.error(errorMessage);

            throw new IllegalStateException(errorMessage, e);
        }
    }

    @Bean(name = "SoapSecretEncryptionProvider")
    public EncryptionProvider getSoapEncryptionProvider() throws IllegalStateException {

        try {
            byte[] key = Files.readAllBytes(Paths.get(this.soapSecretResource.getFile().getAbsolutePath()));
            String encodedKey = HexUtils.toHexString(key);
            EncryptionProvider encryptionProvider = new JreEncryptionProvider(encodedKey);
            return encryptionProvider;

        } catch (final IOException e) {
            final String errorMessage = String.format("Unexpected exception when reading key for soap protocol. Key Path: %s",
                    this.soapSecretResource.getFilename());
            LOGGER.error(errorMessage);

            throw new IllegalStateException(errorMessage, e);
        }
    }
}