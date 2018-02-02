/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgpfoundation.osgp.adapter.ws.da.application.config;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.osgpfoundation.osgp.adapter.ws.da.application.exceptionhandling.DetailSoapFaultMappingExceptionResolver;
import org.osgpfoundation.osgp.adapter.ws.da.application.exceptionhandling.SoapFaultMapper;
import org.osgpfoundation.osgp.adapter.ws.da.application.services.NotificationService;
import org.osgpfoundation.osgp.adapter.ws.da.application.services.NotificationServiceBlackHole;
import org.osgpfoundation.osgp.adapter.ws.da.application.services.NotificationServiceWs;
import org.osgpfoundation.osgp.adapter.ws.da.presentation.ws.SendNotificationServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.DefaultMethodEndpointAdapter;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;
import org.springframework.ws.server.endpoint.adapter.method.MethodArgumentResolver;
import org.springframework.ws.server.endpoint.adapter.method.MethodReturnValueHandler;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;

import com.alliander.osgp.adapter.ws.endpointinterceptors.AnnotationMethodArgumentResolver;
import com.alliander.osgp.adapter.ws.endpointinterceptors.CertificateAndSoapHeaderAuthorizationEndpointInterceptor;
import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.endpointinterceptors.SoapHeaderEndpointInterceptor;
import com.alliander.osgp.adapter.ws.endpointinterceptors.WebServiceMonitorInterceptor;
import com.alliander.osgp.adapter.ws.endpointinterceptors.X509CertificateRdnAttributeValueEndpointInterceptor;
import com.alliander.osgp.shared.application.config.AbstractConfig;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Configuration
@PropertySources({ @PropertySource("classpath:osgp-adapter-ws-distributionautomation.properties"),
        @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
        @PropertySource(value = "file:${osgp/AdapterWsDistributionAutomation/config}", ignoreResourceNotFound = true), })
public class WebServiceConfig extends AbstractConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceConfig.class);

    private static final String PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_DISTRIBUTION_AUTOMATION_GENERIC = "jaxb2.marshaller.context.path.distributionautomation.generic";
    private static final String PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_DISTRIBUTION_AUTOMATION_NOTIFICATION = "jaxb2.marshaller.context.path.distributionautomation.notification";

    private static final String ORGANISATION_IDENTIFICATION_HEADER = "OrganisationIdentification";
    private static final String ORGANISATION_IDENTIFICATION_CONTEXT = ORGANISATION_IDENTIFICATION_HEADER;

    private static final String USER_NAME_HEADER = "UserName";

    private static final String APPLICATION_NAME_HEADER = "ApplicationName";

    private static final String X509_RDN_ATTRIBUTE_ID = "cn";
    private static final String X509_RDN_ATTRIBUTE_VALUE_CONTEXT_PROPERTY_NAME = "CommonNameSet";
    private static final String SERVER = "SERVER";
    @Value("${web.service.notification.enabled}")
    private boolean webserviceNotificationEnabled;
    @Value("${web.service.notification.url:#{null}}")
    private String webserviceNotificationUrl;
    @Value("${web.service.notification.username:#{null}}")
    private String webserviceNotificationUsername;
    @Value("${web.service.notification.organisation:OSGP}")
    private String webserviceNotificationOrganisation;
    @Value("${web.service.keystore.type}")
    private String webserviceKeystoreType;
    @Value("${web.service.keystore.location}")
    private String webserviceKeystoreLocation;
    @Value("${web.service.keystore.password}")
    private String webserviceKeystorePassword;
    @Value("${web.service.truststore.type}")
    private String webserviceTruststoreType;
    @Value("${web.service.truststore.location}")
    private String webserviceTruststoreLocation;
    @Value("${web.service.truststore.password}")
    private String webserviceTruststorePassword;

    // === DISTRIBUTION AUTOMATION MARSHALLERS ===

    /**
     * Method for creating the Marshaller for Distribution Automation Generic (Ad
     * Hoc Management, Device Management and Monitoring)
     *
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller distributionautomationGenericMarshaller() {
        LOGGER.debug("Creating Distribution Automation Generic JAXB 2 Marshaller Bean");

        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(this.environment
                .getRequiredProperty(PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_DISTRIBUTION_AUTOMATION_GENERIC));
        return marshaller;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for Distribition
     * Automation Generic
     *
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor distributionautomationGenericMarshallingPayloadMethodProcessor() {
        LOGGER.debug("Creating Distribution Automation Generic Marshalling Payload Method Processor Bean");

        return new MarshallingPayloadMethodProcessor(this.distributionautomationGenericMarshaller(),
                this.distributionautomationGenericMarshaller());
    }

    /**
     * Method for creating the Default Method Endpoint Adapter.
     *
     * @return DefaultMethodEndpointAdapter
     */
    @Bean
    public DefaultMethodEndpointAdapter defaultMethodEndpointAdapter() {
        LOGGER.debug("Creating Default Method Endpoint Adapter Bean");

        final DefaultMethodEndpointAdapter defaultMethodEndpointAdapter = new DefaultMethodEndpointAdapter();

        final List<MethodArgumentResolver> methodArgumentResolvers = new ArrayList<>();

        // Add Distribution Automation Marshalling Payload Method Processors to Method
        // Argument Resolvers
        methodArgumentResolvers.add(this.distributionautomationGenericMarshallingPayloadMethodProcessor());

        // Add Organisation Identification Annotation Method Argument Resolver
        methodArgumentResolvers.add(new AnnotationMethodArgumentResolver(ORGANISATION_IDENTIFICATION_CONTEXT,
                OrganisationIdentification.class));
        defaultMethodEndpointAdapter.setMethodArgumentResolvers(methodArgumentResolvers);

        final List<MethodReturnValueHandler> methodReturnValueHandlers = new ArrayList<>();

        // Add Distribution Automation Marshalling Payload Method Processors to Method
        // Return Value Handlers
        methodReturnValueHandlers.add(this.distributionautomationGenericMarshallingPayloadMethodProcessor());

        defaultMethodEndpointAdapter.setMethodReturnValueHandlers(methodReturnValueHandlers);

        return defaultMethodEndpointAdapter;
    }

    @Bean
    public DetailSoapFaultMappingExceptionResolver exceptionResolver() {
        LOGGER.debug("Creating Detail Soap Fault Mapping Exception Resolver Bean");
        final DetailSoapFaultMappingExceptionResolver exceptionResolver = new DetailSoapFaultMappingExceptionResolver(
                new SoapFaultMapper());
        exceptionResolver.setOrder(1);

        final Properties props = new Properties();
        props.put("com.alliander.osgp.shared.exceptionhandling.FunctionalException", SERVER);
        props.put("com.alliander.osgp.shared.exceptionhandling.TechnicalException", SERVER);
        props.put("com.alliander.osgp.shared.exceptionhandling.ConnectionFailureException", SERVER);

        exceptionResolver.setExceptionMappings(props);
        return exceptionResolver;
    }

    // === ENDPOINT INTERCEPTORS ===

    /**
     * @return
     */
    @Bean
    public X509CertificateRdnAttributeValueEndpointInterceptor x509CertificateSubjectCnEndpointInterceptor() {
        return new X509CertificateRdnAttributeValueEndpointInterceptor(X509_RDN_ATTRIBUTE_ID,
                X509_RDN_ATTRIBUTE_VALUE_CONTEXT_PROPERTY_NAME);
    }

    /**
     * @return
     */
    @Bean
    public SoapHeaderEndpointInterceptor organisationIdentificationInterceptor() {
        return new SoapHeaderEndpointInterceptor(ORGANISATION_IDENTIFICATION_HEADER,
                ORGANISATION_IDENTIFICATION_CONTEXT);
    }

    /**
     * @return
     */
    @Bean
    public CertificateAndSoapHeaderAuthorizationEndpointInterceptor organisationIdentificationInCertificateCnEndpointInterceptor() {
        return new CertificateAndSoapHeaderAuthorizationEndpointInterceptor(
                X509_RDN_ATTRIBUTE_VALUE_CONTEXT_PROPERTY_NAME, ORGANISATION_IDENTIFICATION_CONTEXT);
    }

    @Bean
    public WebServiceMonitorInterceptor webServiceMonitorInterceptor() {
        return new WebServiceMonitorInterceptor(ORGANISATION_IDENTIFICATION_HEADER, USER_NAME_HEADER,
                APPLICATION_NAME_HEADER);
    }

    @Bean(value = "notificationServiceDistributionAutomation")
    public NotificationService notificationService() throws GeneralSecurityException {
        if (this.webserviceNotificationEnabled && !StringUtils.isEmpty(this.webserviceNotificationUrl)) {
            return new NotificationServiceWs(this.sendNotificationServiceClient(), this.webserviceNotificationUrl,
                    this.webserviceNotificationUsername, this.webserviceNotificationOrganisation);
        } else {
            return new NotificationServiceBlackHole();
        }
    }

    @Bean
    public SaajSoapMessageFactory messageFactory() {
        return new SaajSoapMessageFactory();
    }

    @Bean
    public KeyStoreFactoryBean webServiceTrustStoreFactory() {
        final KeyStoreFactoryBean factory = new KeyStoreFactoryBean();
        factory.setType(this.webserviceTruststoreType);
        factory.setLocation(new FileSystemResource(this.webserviceTruststoreLocation));
        factory.setPassword(this.webserviceTruststorePassword);

        return factory;
    }

    @Bean
    public Jaxb2Marshaller notificationSenderMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(this.environment
                .getRequiredProperty(PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_DISTRIBUTION_AUTOMATION_NOTIFICATION));
        return marshaller;
    }

    @Bean
    public SendNotificationServiceClient sendNotificationServiceClient() throws java.security.GeneralSecurityException {
        return new SendNotificationServiceClient(
                this.createWebServiceTemplateFactory(this.notificationSenderMarshaller()));
    }

    private DefaultWebServiceTemplateFactory createWebServiceTemplateFactory(final Jaxb2Marshaller marshaller) {
        return new DefaultWebServiceTemplateFactory.Builder().setMarshaller(marshaller)
                .setMessageFactory(this.messageFactory()).setTargetUri(this.webserviceNotificationUrl)
                .setKeyStoreType(this.webserviceKeystoreType).setKeyStoreLocation(this.webserviceKeystoreLocation)
                .setKeyStorePassword(this.webserviceKeystorePassword)
                .setTrustStoreFactory(this.webServiceTrustStoreFactory()).setApplicationName("ZownStream").build();
    }
}
