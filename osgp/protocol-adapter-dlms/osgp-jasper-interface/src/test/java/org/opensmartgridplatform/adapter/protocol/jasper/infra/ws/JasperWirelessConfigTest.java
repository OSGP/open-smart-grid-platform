/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.jasper.infra.ws;

import javax.annotation.Resource;

import org.opensmartgridplatform.adapter.protocol.jasper.config.JasperWirelessAccess;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

/**
 * An application context Java configuration class for Jasper Wireless settings.
 * The usage of Java configuration requires Spring Framework 3.0
 *
 * This configuration class supports setting up the context for junit testing.
 * The difference with the original "life" configuration class is the retrieval
 * of environment properties (like urlendpoint or account values). These are in
 * the junit test class mocked.
 */
@Configuration
public class JasperWirelessConfigTest {

    // JMS Settings
    private static final String PROPERTY_NAME_CONTROLCENTER_SMS_URI = "jwcc.uri.sms";
    private static final String PROPERTY_NAME_CONTROLCENTER_LICENSEKEY = "jwcc.licensekey";
    private static final String PROPERTY_NAME_CONTROLCENTER_USERNAME = "jwcc.username";
    private static final String PROPERTY_NAME_CONTROLCENTER_PASSWORD = "jwcc.password";
    private static final String PROPERTY_NAME_CONTROLCENTER_API_VERSION = "jwcc.api_version";

    @Resource
    private Environment environment;

    @Bean
    public Jaxb2Marshaller jasperWirelessMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.jasperwireless.api.ws.service");
        return marshaller;
    }

    @Bean
    public HttpComponentsMessageSender xwsSecurityMessageSender() {
        return new HttpComponentsMessageSender();
    }

    @Bean
    public Wss4jSecurityInterceptor xwsSecurityInterceptor() {
        final Wss4jSecurityInterceptor interceptor = new Wss4jSecurityInterceptor();
        interceptor.setSecurementActions("UsernameToken");
        return interceptor;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate() {
        final WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(this.jasperWirelessMarshaller());
        webServiceTemplate.setUnmarshaller(this.jasperWirelessMarshaller());
        webServiceTemplate.setDefaultUri("https://api.jasperwireless.com/ws/service/Sms");
        webServiceTemplate.setMessageSender(this.xwsSecurityMessageSender());
        final ClientInterceptor[] clientInterceptors = new ClientInterceptor[] { this.xwsSecurityInterceptor() };
        webServiceTemplate.setInterceptors(clientInterceptors);
        return webServiceTemplate;
    }

    @Bean
    public JasperWirelessAccess jwccWSConfig() {
        return new JasperWirelessAccess(this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_SMS_URI),
                this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_LICENSEKEY),
                this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_USERNAME),
                this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_PASSWORD),
                this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_API_VERSION));
    }

    @Bean
    public JasperWirelessSmsClient jasperWirelessSMSClient() {
        return new JasperWirelessSmsClient();
    }

    @Bean
    public CorrelationIdProviderService correlationIdProviderService() {
        return new CorrelationIdProviderService();
    }
}
