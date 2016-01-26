/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.soap.MessageFactory;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.osgp.adapter.protocol.dlms.infra.ws.CorrelationIdProviderService;
import org.osgp.adapter.protocol.dlms.infra.ws.JasperWirelessSMSClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.server.endpoint.adapter.DefaultMethodEndpointAdapter;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;
import org.springframework.ws.server.endpoint.adapter.method.MethodArgumentResolver;
import org.springframework.ws.server.endpoint.adapter.method.MethodReturnValueHandler;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.security.wss4j.Wss4jSecurityInterceptor;

//import org.springframework.ws.transport.http.HttpComponentsMessageSender;

/**
 * An application context Java configuration class for Jasper Wireless settings.
 * The usage of Java configuration requires Spring Framework 3.0
 */
@EnableWs
@Configuration
@PropertySource("file:${osp/osgpAdapterProtocolDlms/config}")
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
    public Jaxb2Marshaller jasperWirelessMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.jasperwireless.api.ws.service.sms");
        return marshaller;
    }

    @Bean
    public SaajSoapMessageFactory messageFactory() throws Exception {
        final SaajSoapMessageFactory saajSoapMessageFactory = new SaajSoapMessageFactory(MessageFactory.newInstance());
        saajSoapMessageFactory.setSoapVersion(SoapVersion.SOAP_11);
        return saajSoapMessageFactory;
    }

    // @Bean
    // public HttpComponentsMessageSender xwsSecurityMessageSender() {
    // return new HttpComponentsMessageSender();
    // }

    @Bean
    public Wss4jSecurityInterceptor wss4jSecurityInterceptorClient() {
        /*
         * Please note If ValidationActions needs to be mentioned then
         * Validdation CallBack Handler must be there
         */
        final Wss4jSecurityInterceptor wss4jSecurityInterceptor = new Wss4jSecurityInterceptor();
        // wss4jSecurityInterceptor.setValidationActions("UsernameToken");
        wss4jSecurityInterceptor.setSecurementActions("UsernameToken");
        wss4jSecurityInterceptor.setSecurementUsername("dummyUser");
        wss4jSecurityInterceptor.setSecurementPassword("dummyPw");
        wss4jSecurityInterceptor.setSecurementPasswordType("PasswordText");
        // wss4jSecurityInterceptor.setValidationCallbackHandler(callbackHandler());
        return wss4jSecurityInterceptor;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate() throws Exception {
        final WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(this.jasperWirelessMarshaller());
        webServiceTemplate.setUnmarshaller(this.jasperWirelessMarshaller());
        webServiceTemplate.setDefaultUri("https://api.jasperwireless.com/ws/service/Sms");
        // webServiceTemplate.setMessageSender(this.xwsSecurityMessageSender());
        // final ClientInterceptor[] clientInterceptors = new
        // ClientInterceptor[] { this.xwsSecurityInterceptor() };
        // webServiceTemplate.setInterceptors(clientInterceptors);

        //
        webServiceTemplate.setInterceptors(new ClientInterceptor[] { this.wss4jSecurityInterceptorClient() });
        webServiceTemplate.setMessageFactory(this.messageFactory());

        return webServiceTemplate;
    }

    @Bean
    public MarshallingPayloadMethodProcessor marshallingPayloadMethodProcessor() {
        final MarshallingPayloadMethodProcessor marshallingPayloadMethodProcessor = new MarshallingPayloadMethodProcessor(
                this.jasperWirelessMarshaller());
        return marshallingPayloadMethodProcessor;
    }

    @Bean
    public DefaultMethodEndpointAdapter defaultMethodEndpointAdapter() {
        final DefaultMethodEndpointAdapter defaultMethodEndpointAdapter = new DefaultMethodEndpointAdapter();

        final List<MethodArgumentResolver> methodArgumentResolvers = new ArrayList<MethodArgumentResolver>();
        methodArgumentResolvers.add(this.marshallingPayloadMethodProcessor());
        defaultMethodEndpointAdapter.setMethodArgumentResolvers(methodArgumentResolvers);

        final List<MethodReturnValueHandler> methodReturnValueHandlers = new ArrayList<MethodReturnValueHandler>();
        methodReturnValueHandlers.add(this.marshallingPayloadMethodProcessor());
        defaultMethodEndpointAdapter.setMethodReturnValueHandlers(methodReturnValueHandlers);

        return defaultMethodEndpointAdapter;
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
        return new JasperWirelessSMSClient();
    }

    @Bean
    public CorrelationIdProviderService correlationIdProviderService() {
        return new CorrelationIdProviderService();
    }
}
