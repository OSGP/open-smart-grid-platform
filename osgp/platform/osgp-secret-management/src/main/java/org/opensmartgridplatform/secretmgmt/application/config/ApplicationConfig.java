package org.opensmartgridplatform.secretmgmt.application.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-secret-management.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/SecretManagement/config}", ignoreResourceNotFound = true)
public class ApplicationConfig {

    private ApplicationConfig() {
    }

    @Value("${component.name}")
    public static final String COMPONENT_NAME = "OSGP-SECRET-MANAGEMENT";
}
