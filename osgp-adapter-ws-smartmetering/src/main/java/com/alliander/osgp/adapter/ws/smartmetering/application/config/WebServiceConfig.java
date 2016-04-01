/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
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
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.NotificationMapper;
import com.alliander.osgp.adapter.ws.smartmetering.infra.ws.SendNotificationServiceClient;
import com.alliander.osgp.adapter.ws.smartmetering.infra.ws.WebServiceTemplateFactory;

@Configuration
@PropertySource("file:${osp/osgpAdapterWsSmartMetering/config}")
public class WebServiceConfig {

    private static final String PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_SMART_METERING_MANAGEMENT = "jaxb2.marshaller.context.path.smartmetering.management";
    private static final String PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_SMART_METERING_INSTALLATION = "jaxb2.marshaller.context.path.smartmetering.installation";
    private static final String PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_SMART_METERING_MONITORING = "jaxb2.marshaller.context.path.smartmetering.monitoring";
    private static final String PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_SMART_METERING_ADHOC = "jaxb2.marshaller.context.path.smartmetering.adhoc";
    private static final String PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_SMART_METERING_CONFIGURATION = "jaxb2.marshaller.context.path.smartmetering.configuration";

    private static final String ORGANISATION_IDENTIFICATION_HEADER = "OrganisationIdentification";
    private static final String ORGANISATION_IDENTIFICATION_CONTEXT = ORGANISATION_IDENTIFICATION_HEADER;

    private static final String MESSAGE_PRIORITY_HEADER = "MessagePriority";
    private static final String MESSAGE_PRIORITY_CONTEXT = MESSAGE_PRIORITY_HEADER;
    private static final String MESSAGE_SCHEDULETIME_HEADER = "ScheduleTime";

    private static final String X509_RDN_ATTRIBUTE_ID = "cn";
    private static final String X509_RDN_ATTRIBUTE_VALUE_CONTEXT_PROPERTY_NAME = "CommonNameSet";

    private static final String PROPERTY_NAME_APPLICATION_NAME = "application.name";

    private static final String PROPERTY_NAME_WEBSERVICE_TRUSTSTORE_LOCATION = "web.service.truststore.location";
    private static final String PROPERTY_NAME_WEBSERVICE_TRUSTSTORE_PASSWORD = "web.service.truststore.password";
    private static final String PROPERTY_NAME_WEBSERVICE_TRUSTSTORE_TYPE = "web.service.truststore.type";
    private static final String PROPERTY_NAME_WEBSERVICE_KEYSTORE_LOCATION = "web.service.keystore.location";
    private static final String PROPERTY_NAME_WEBSERVICE_KEYSTORE_PASSWORD = "web.service.keystore.password";
    private static final String PROPERTY_NAME_WEBSERVICE_KEYSTORE_TYPE = "web.service.keystore.type";

    private static final String PROPERTY_NAME_WEBSERVICE_NOTIFICATION_URL = "web.service.notification.url";

    private static final String PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_SMARTMETERING_NOTIFICATION = "jaxb2.marshaller.context.path.smartmetering.notification";

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceConfig.class);

    @Resource
    private Environment environment;

    // WS Notification communication

    @Bean
    public SaajSoapMessageFactory messageFactory() {
        return new SaajSoapMessageFactory();
    }

    @Bean
    public KeyStoreFactoryBean webServiceTrustStoreFactory() {
        final KeyStoreFactoryBean factory = new KeyStoreFactoryBean();
        factory.setType(this.environment.getProperty(PROPERTY_NAME_WEBSERVICE_TRUSTSTORE_TYPE));
        factory.setLocation(new FileSystemResource(this.environment
                .getProperty(PROPERTY_NAME_WEBSERVICE_TRUSTSTORE_LOCATION)));
        factory.setPassword(this.environment.getProperty(PROPERTY_NAME_WEBSERVICE_TRUSTSTORE_PASSWORD));

        return factory;
    }

    @Bean
    public Jaxb2Marshaller notificationSenderMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(this.environment
                .getRequiredProperty(PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_SMARTMETERING_NOTIFICATION));
        return marshaller;
    }

    @Bean
    public SendNotificationServiceClient sendNotificationServiceClient() throws java.security.GeneralSecurityException {
        return new SendNotificationServiceClient(this.createWebServiceTemplateFactory(this
                .notificationSenderMarshaller()), this.notificationMapper());
    }

    private WebServiceTemplateFactory createWebServiceTemplateFactory(final Jaxb2Marshaller marshaller) {
        return new WebServiceTemplateFactory(marshaller, this.messageFactory(),
                this.environment.getProperty(PROPERTY_NAME_WEBSERVICE_KEYSTORE_TYPE),
                this.environment.getProperty(PROPERTY_NAME_WEBSERVICE_KEYSTORE_LOCATION),
                this.environment.getProperty(PROPERTY_NAME_WEBSERVICE_KEYSTORE_PASSWORD),
                this.webServiceTrustStoreFactory(),
                this.environment.getRequiredProperty(PROPERTY_NAME_APPLICATION_NAME));
    }

    @Bean
    public NotificationMapper notificationMapper() {
        return new NotificationMapper();
    }

    @Bean
    public String notificationURL() {
        return this.environment.getRequiredProperty(PROPERTY_NAME_WEBSERVICE_NOTIFICATION_URL);
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

        marshaller.setContextPath(this.environment
                .getRequiredProperty(PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_SMART_METERING_MANAGEMENT));

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
     * Method for creating the Marshaller for smart metering installation.
     *
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller smartMeteringInstallationMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.environment
                .getRequiredProperty(PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_SMART_METERING_INSTALLATION));

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

        marshaller.setContextPath(this.environment
                .getRequiredProperty(PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_SMART_METERING_MONITORING));

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

        marshaller.setContextPath(this.environment
                .getRequiredProperty(PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_SMART_METERING_ADHOC));

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

        marshaller.setContextPath(this.environment
                .getRequiredProperty(PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_SMART_METERING_CONFIGURATION));

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
