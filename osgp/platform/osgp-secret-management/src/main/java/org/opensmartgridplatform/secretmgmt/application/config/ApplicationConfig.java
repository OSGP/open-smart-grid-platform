package org.opensmartgridplatform.secretmgmt.application.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Value("${component.name}")
    public final static String COMPONENT_NAME = "OSGP-SECRET-MANAGEMENT";
}
