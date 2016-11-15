/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.config;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import com.alliander.osgp.adapter.ws.endpointinterceptors.MessagePriority;
import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.endpointinterceptors.ScheduleTime;
import com.alliander.osgp.adapter.ws.endpointinterceptors.SoapHeaderEndpointInterceptor;
import com.alliander.osgp.adapter.ws.endpointinterceptors.SoapHeaderMessagePriorityEndpointInterceptor;
import com.alliander.osgp.adapter.ws.endpointinterceptors.SoapHeaderScheduleTimeEndpointInterceptor;
import com.alliander.osgp.adapter.ws.endpointinterceptors.X509CertificateRdnAttributeValueEndpointInterceptor;
import com.alliander.osgp.adapter.ws.smartmetering.application.exceptionhandling.DetailSoapFaultMappingExceptionResolver;
import com.alliander.osgp.adapter.ws.smartmetering.application.exceptionhandling.SoapFaultMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.NotificationService;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.NotificationServiceBlackHole;
import com.alliander.osgp.adapter.ws.smartmetering.application.services.NotificationServiceWs;
import com.alliander.osgp.adapter.ws.smartmetering.infra.ws.SendNotificationServiceClient;
import com.alliander.osgp.adapter.ws.smartmetering.infra.ws.WebServiceTemplateFactory;
import com.alliander.osgp.shared.application.config.AbstractConfig;

@Configuration
@PropertySources({
	@PropertySource("classpath:osgp-adapter-ws-smartmetering.properties"),
    @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
	@PropertySource(value = "file:${osgp/AdapterWsSmartMetering/config}", ignoreResourceNotFound = true),
})
public class WebServiceConfig extends AbstractConfig {

    @Value("${jaxb2.marshaller.context.path.smartmetering.adhoc}")
    private String marshallerContextPathAdhoc;
    @Value("${jaxb2.marshaller.context.path.smartmetering.bundle}")
    private String marshallerContextPathBundle;
    @Value("${jaxb2.marshaller.context.path.smartmetering.configuration}")
    private String marshallerContextPathConfiguration;
    @Value("${jaxb2.marshaller.context.path.smartmetering.installation}")
    private String marshallerContextPathInstallation;
    @Value("${jaxb2.marshaller.context.path.smartmetering.management}")
    private String marshallerContextPathManagement;
    @Value("${jaxb2.marshaller.context.path.smartmetering.monitoring}")
    private String marshallerContextPathMonitoring;
    @Value("${jaxb2.marshaller.context.path.smartmetering.notification}")
    private String marshallerContextPathNotification;

    @Value("${web.service.truststore.location}")
    private String webserviceTruststoreLocation;
    @Value("${web.service.truststore.password}")
    private String webserviceTruststorePassword;
    @Value("${web.service.truststore.type}")
    private String webserviceTruststoreType;

    @Value("${web.service.keystore.location}")
    private String webserviceKeystoreLocation;
    @Value("${web.service.keystore.password}")
    private String webserviceKeystorePassword;
    @Value("${web.service.keystore.type}")
    private String webserviceKeystoreType;

    @Value("${web.service.notification.url:#{null}}")
    private String webserviceNotificationUrl;
    @Value("${application.name}")
    private String applicationName;

    private static final String ORGANISATION_IDENTIFICATION_HEADER = "OrganisationIdentification";
    private static final String ORGANISATION_IDENTIFICATION_CONTEXT = ORGANISATION_IDENTIFICATION_HEADER;

    private static final String MESSAGE_PRIORITY_HEADER = "MessagePriority";
    private static final String MESSAGE_PRIORITY_CONTEXT = MESSAGE_PRIORITY_HEADER;
    private static final String MESSAGE_SCHEDULETIME_HEADER = "ScheduleTime";

    private static final String X509_RDN_ATTRIBUTE_ID = "cn";
    private static final String X509_RDN_ATTRIBUTE_VALUE_CONTEXT_PROPERTY_NAME = "CommonNameSet";

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceConfig.class);

    // WS Notification communication

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
        marshaller.setContextPath(this.marshallerContextPathNotification);
        return marshaller;
    }

    @Bean
    public SendNotificationServiceClient sendNotificationServiceClient() throws java.security.GeneralSecurityException {
        return new SendNotificationServiceClient(this.createWebServiceTemplateFactory(this
                .notificationSenderMarshaller()));
    }

    private WebServiceTemplateFactory createWebServiceTemplateFactory(final Jaxb2Marshaller marshaller) {
        return new WebServiceTemplateFactory(marshaller, this.messageFactory(), this.webserviceKeystoreType,
                this.webserviceKeystoreLocation, this.webserviceKeystorePassword, this.webServiceTrustStoreFactory(),
                this.applicationName);
    }

    @Bean
    public String notificationUrl() {
        return this.webserviceNotificationUrl;
    }

    @Bean
    public NotificationService wsSmartMeteringNotificationService() throws GeneralSecurityException {
        if (this.notificationUrl() != null) {
            return new NotificationServiceWs(this.sendNotificationServiceClient(), this.notificationUrl());
        } else {
            return new NotificationServiceBlackHole();
        }
    }

    // Client WS code

    /**
     * Method for creating the Marshaller for smart metering management.
     *
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller smartMeteringManagementMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.marshallerContextPathManagement);

        return marshaller;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for Smart
     * Metering management.
     *
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor smartMeteringManagementMarshallingPayloadMethodProcessor() {
        return new MarshallingPayloadMethodProcessor(this.smartMeteringManagementMarshaller(),
                this.smartMeteringManagementMarshaller());
    }

    /**
     * Method for creating the Marshaller for smart metering bundle.
     *
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller smartMeteringBundleMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.marshallerContextPathBundle);

        return marshaller;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for Smart
     * Metering bundle.
     *
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor smartMeteringBundleMarshallingPayloadMethodProcessor() {
        return new MarshallingPayloadMethodProcessor(this.smartMeteringBundleMarshaller(),
                this.smartMeteringBundleMarshaller());
    }

    /**
     * Method for creating the Marshaller for smart metering installation.
     *
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller smartMeteringInstallationMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.marshallerContextPathInstallation);

        return marshaller;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for Smart
     * Metering installation.
     *
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor smartMeteringInstallationMarshallingPayloadMethodProcessor() {
        return new MarshallingPayloadMethodProcessor(this.smartMeteringInstallationMarshaller(),
                this.smartMeteringInstallationMarshaller());
    }

    /**
     * Method for creating the Marshaller for smart metering monitoring.
     *
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller smartMeteringMonitoringMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.marshallerContextPathMonitoring);

        return marshaller;
    }

    /**
     * Method for creating the Marshaller for smart metering adhoc.
     *
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller smartMeteringAdhocMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.marshallerContextPathAdhoc);

        return marshaller;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for Smart
     * Metering adhoc.
     *
     * @return MarshallingPayloadMethodProcessor
     */

    @Bean
    public MarshallingPayloadMethodProcessor smartMeteringAdhocMarshallingPayloadMethodProcessor() {
        return new MarshallingPayloadMethodProcessor(this.smartMeteringAdhocMarshaller(),
                this.smartMeteringAdhocMarshaller());
    }

    /**
     * Method for creating the Marshaller for smart metering configuration.
     *
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller smartMeteringConfigurationMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.marshallerContextPathConfiguration);

        return marshaller;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for Smart
     * Metering configuration.
     *
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor smartMeteringConfigurationMarshallingPayloadMethodProcessor() {
        return new MarshallingPayloadMethodProcessor(this.smartMeteringConfigurationMarshaller(),
                this.smartMeteringConfigurationMarshaller());
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for Smart
     * Metering monitoring.
     *
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor smartMeteringMonitoringMarshallingPayloadMethodProcessor() {
        return new MarshallingPayloadMethodProcessor(this.smartMeteringMonitoringMarshaller(),
                this.smartMeteringMonitoringMarshaller());
    }

    /**
     * Method for creating the Default Method Endpoint Adapter.
     *
     * @return DefaultMethodEndpointAdapter
     */
    @Bean
    public DefaultMethodEndpointAdapter defaultMethodEndpointAdapter() {
        final DefaultMethodEndpointAdapter defaultMethodEndpointAdapter = new DefaultMethodEndpointAdapter();

        final List<MethodArgumentResolver> methodArgumentResolvers = new ArrayList<MethodArgumentResolver>();

        // SMART METERING
        methodArgumentResolvers.add(this.smartMeteringManagementMarshallingPayloadMethodProcessor());
        methodArgumentResolvers.add(this.smartMeteringBundleMarshallingPayloadMethodProcessor());
        methodArgumentResolvers.add(this.smartMeteringInstallationMarshallingPayloadMethodProcessor());
        methodArgumentResolvers.add(this.smartMeteringMonitoringMarshallingPayloadMethodProcessor());
        methodArgumentResolvers.add(this.smartMeteringAdhocMarshallingPayloadMethodProcessor());
        methodArgumentResolvers.add(this.smartMeteringConfigurationMarshallingPayloadMethodProcessor());

        methodArgumentResolvers.add(new AnnotationMethodArgumentResolver(ORGANISATION_IDENTIFICATION_CONTEXT,
                OrganisationIdentification.class));
        methodArgumentResolvers.add(new AnnotationMethodArgumentResolver(MESSAGE_PRIORITY_CONTEXT,
                MessagePriority.class));
        methodArgumentResolvers.add(new AnnotationMethodArgumentResolver(MESSAGE_SCHEDULETIME_HEADER,
                ScheduleTime.class));
        defaultMethodEndpointAdapter.setMethodArgumentResolvers(methodArgumentResolvers);

        final List<MethodReturnValueHandler> methodReturnValueHandlers = new ArrayList<MethodReturnValueHandler>();

        // SMART METERING
        methodReturnValueHandlers.add(this.smartMeteringManagementMarshallingPayloadMethodProcessor());
        methodReturnValueHandlers.add(this.smartMeteringBundleMarshallingPayloadMethodProcessor());
        methodReturnValueHandlers.add(this.smartMeteringInstallationMarshallingPayloadMethodProcessor());
        methodReturnValueHandlers.add(this.smartMeteringMonitoringMarshallingPayloadMethodProcessor());
        methodReturnValueHandlers.add(this.smartMeteringAdhocMarshallingPayloadMethodProcessor());
        methodReturnValueHandlers.add(this.smartMeteringConfigurationMarshallingPayloadMethodProcessor());

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
        props.put("com.alliander.osgp.shared.exceptionhandling.FunctionalException", "SERVER");
        exceptionResolver.setExceptionMappings(props);
        return exceptionResolver;
    }

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

    @Bean
    public SoapHeaderMessagePriorityEndpointInterceptor messagePriorityInterceptor() {
        return new SoapHeaderMessagePriorityEndpointInterceptor(MESSAGE_PRIORITY_HEADER, MESSAGE_PRIORITY_CONTEXT);
    }

    @Bean
    public SoapHeaderScheduleTimeEndpointInterceptor scheduleTimeInterceptor() {
        return new SoapHeaderScheduleTimeEndpointInterceptor(MESSAGE_SCHEDULETIME_HEADER);
    }

    /**
     * @return
     */
    @Bean
    public CertificateAndSoapHeaderAuthorizationEndpointInterceptor organisationIdentificationInCertificateCnEndpointInterceptor() {
        return new CertificateAndSoapHeaderAuthorizationEndpointInterceptor(
                X509_RDN_ATTRIBUTE_VALUE_CONTEXT_PROPERTY_NAME, ORGANISATION_IDENTIFICATION_CONTEXT);
    }

}
