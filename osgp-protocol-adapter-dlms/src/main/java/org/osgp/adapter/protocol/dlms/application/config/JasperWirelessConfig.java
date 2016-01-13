/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.config;

import javax.annotation.Resource;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.osgp.adapter.protocol.dlms.infra.ws.JasperWirelessSMSClient;
import org.osgp.adapter.protocol.dlms.infra.ws.JasperWirelessSMSClientImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * An application context Java configuration class for Jasper Wireless settings.
 * The usage of Java configuration requires Spring Framework 3.0
 */
@Configuration
@PropertySource("file:${osp/osgpAdapterProtocolDlms/config}")
@ImportResource("classpath:applicationContext.xml")
public class JasperWirelessConfig {

    // JMS Settings
    private static final String PROPERTY_NAME_CONTROLCENTER_SMS_URI = "jwcc.uri.sms";
    private static final String PROPERTY_NAME_CONTROLCENTER_LICENSEKEY = "jwcc.licensekey";
    private static final String PROPERTY_NAME_CONTROLCENTER_USERNAME = "jwcc.username";
    private static final String PROPERTY_NAME_CONTROLCENTER_PASSWORD = "jwcc.password";
    private static final String PROPERTY_NAME_CONTROLCENTER_API_VERSION = "jwcc.api_version";

    @Resource
    private Environment environment;

    public JasperWirelessConfig() {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    @Bean
    public JwccWSConfig jwccWSConfig() {
        return new JwccWSConfig(this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_SMS_URI),
                this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_LICENSEKEY),
                this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_USERNAME),
                this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_PASSWORD),
                this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_API_VERSION));
    }

    @Bean
    public JasperWirelessSMSClient jasperWirelessSMSClient() {
        return new JasperWirelessSMSClientImpl();
    }
}
