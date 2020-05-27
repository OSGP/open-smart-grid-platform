package org.opensmartgridplatform.secretmgmt.application.config;

import org.opensmartgridplatform.secretmgmt.application.services.encryption.DefaultEncryptionDelegate;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.EncryptionDelegate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {

    @Bean(name = "DefaultEncryptionDelegate")
    public EncryptionDelegate getDefaultEncryptionDelegate() {
        return new DefaultEncryptionDelegate();
    }
}