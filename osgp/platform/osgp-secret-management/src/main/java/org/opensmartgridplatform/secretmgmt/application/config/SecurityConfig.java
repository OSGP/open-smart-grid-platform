package org.opensmartgridplatform.secretmgmt.application.config;

import org.opensmartgridplatform.secretmgmt.application.services.encryption.DefaultEncryptionDelegate;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.EncryptionDelegate;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProvider;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.JreEncryptionProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

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

    @Bean(name = "osgpEncryptionDelegate")
    public EncryptionDelegate getDefaultEncryptionDelegate() throws Exception {
        EncryptionDelegate encryptionDelegate;

        if (this.hsmKeystoreResource.isPresent()) {
            encryptionDelegate = new DefaultEncryptionDelegate(this.databaseSecretResource.getFile(), this.hsmKeystoreResource.get().getFile());
        } else {
            encryptionDelegate = new DefaultEncryptionDelegate(this.databaseSecretResource.getFile());
        }
        return encryptionDelegate;

    }

    @Bean(name = "SoapSecretEncryptionProvider")
    public EncryptionProvider getSoapSecretEncryptionProvider() throws Exception {
        final EncryptionProvider encryptionProvider = new JreEncryptionProvider();
        encryptionProvider.setKeyFile(this.soapSecretResource.getFile());
        return encryptionProvider;
    }

    @Bean
    public EncryptionProviderType getEncryptionProviderType() {
        return EncryptionProviderType.valueOf(this.encryptionProviderTypeName);
    }
}