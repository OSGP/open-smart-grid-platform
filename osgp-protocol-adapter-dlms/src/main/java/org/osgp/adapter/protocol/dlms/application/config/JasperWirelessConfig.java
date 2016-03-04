/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.config;

import javax.annotation.Resource;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.infra.ws.CorrelationIdProviderService;
import org.osgp.adapter.protocol.dlms.infra.ws.JasperWirelessSmsClient;
import org.osgp.adapter.protocol.dlms.infra.ws.JasperWirelessTerminalClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.security.wss4j.Wss4jSecurityInterceptor;

/**
 * An application context Java configuration class for Jasper Wireless settings.
 * The usage of Java configuration requires Spring Framework 3.0
 */
@Configuration
@PropertySource("file:${osp/osgpAdapterProtocolDlms/config}")
public class JasperWirelessConfig {

    // JMS Settings
    private static final String PROPERTY_NAME_CONTROLCENTER_TERMINAL_URI = "jwcc.uri.terminal";
    private static final String PROPERTY_NAME_CONTROLCENTER_SMS_URI = "jwcc.uri.sms";
    private static final String PROPERTY_NAME_CONTROLCENTER_LICENSEKEY = "jwcc.licensekey";
    private static final String PROPERTY_NAME_CONTROLCENTER_USERNAME = "jwcc.username";
    private static final String PROPERTY_NAME_CONTROLCENTER_PASSWORD = "jwcc.password";
    private static final String PROPERTY_NAME_CONTROLCENTER_API_VERSION = "jwcc.api_version";
    private static final String GETSESSION_RETRIES = "jwcc.getsession.retries";
    private static final String GETSESSION_SLEEP_BETWEEN_RETRIES = "jwcc.getsession.sleep.between.retries";

    private static final Logger LOGGER = LoggerFactory.getLogger(JasperWirelessConfig.class);

    @Resource
    private Environment environment;

    public JasperWirelessConfig() {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPaths("com.jasperwireless.api.ws.service");
        return marshaller;
    }

    @Bean
    public SaajSoapMessageFactory messageFactory() throws ProtocolAdapterException {
        SaajSoapMessageFactory saajSoapMessageFactory = null;
        try {
            saajSoapMessageFactory = new SaajSoapMessageFactory(MessageFactory.newInstance());
            saajSoapMessageFactory.setSoapVersion(SoapVersion.SOAP_11);
        } catch (final SOAPException e) {
            final String msg = "Error in creating a webservice message wrapper";
            LOGGER.error(msg, e);
            throw new ProtocolAdapterException(msg, e);
        }
        return saajSoapMessageFactory;
    }

    @Bean
    public Wss4jSecurityInterceptor wss4jSecurityInterceptorClient() {
        final Wss4jSecurityInterceptor wss4jSecurityInterceptor = new Wss4jSecurityInterceptor();
        wss4jSecurityInterceptor.setSecurementActions("UsernameToken");
        return wss4jSecurityInterceptor;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate() throws ProtocolAdapterException {
        final WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(this.marshaller());
        webServiceTemplate.setUnmarshaller(this.marshaller());
        webServiceTemplate.setDefaultUri("https://kpnapi.jasperwireless.com/ws/service/Sms");
        webServiceTemplate.setInterceptors(new ClientInterceptor[] { this.wss4jSecurityInterceptorClient() });
        webServiceTemplate.setMessageFactory(this.messageFactory());
        return webServiceTemplate;
    }

    @Bean
    public JasperWirelessSmsClient jasperWirelessSmsClient() {
        return new JasperWirelessSmsClient();
    }

    @Bean
    public JasperWirelessTerminalClient jasperWirelessTerminalClient() {
        return new JasperWirelessTerminalClient();
    }

    @Bean
    public JasperWirelessAccess jasperWirelessAccess() {
        return new JasperWirelessAccess(this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_SMS_URI),
                this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_LICENSEKEY),
                this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_USERNAME),
                this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_PASSWORD),
                this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_API_VERSION));
    }

    @Bean
    public JasperWirelessAccess jasperWirelessTerminalAccess() {
        return new JasperWirelessAccess(this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_TERMINAL_URI),
                this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_LICENSEKEY),
                this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_USERNAME),
                this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_PASSWORD),
                this.environment.getRequiredProperty(PROPERTY_NAME_CONTROLCENTER_API_VERSION));
    }

    @Bean
    public CorrelationIdProviderService correlationIdProviderService() {
        return new CorrelationIdProviderService();
    }

    @Bean
    public int jasperGetSessionRetries() {
        return Integer.parseInt(this.environment.getProperty(GETSESSION_RETRIES));
    }

    @Bean
    public int jasperGetSessionSleepBetweenRetries() {
        return Integer.parseInt(this.environment.getProperty(GETSESSION_SLEEP_BETWEEN_RETRIES));
    }
}
