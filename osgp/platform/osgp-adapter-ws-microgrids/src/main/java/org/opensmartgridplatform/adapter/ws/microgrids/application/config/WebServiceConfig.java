/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.microgrids.application.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.opensmartgridplatform.adapter.ws.clients.NotificationWebServiceTemplateFactory;
import org.opensmartgridplatform.adapter.ws.domain.repositories.NotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.AnnotationMethodArgumentResolver;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.CertificateAndSoapHeaderAuthorizationEndpointInterceptor;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.SoapHeaderEndpointInterceptor;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.WebServiceMonitorInterceptor;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.X509CertificateRdnAttributeValueEndpointInterceptor;
import org.opensmartgridplatform.adapter.ws.microgrids.application.exceptionhandling.DetailSoapFaultMappingExceptionResolver;
import org.opensmartgridplatform.adapter.ws.microgrids.application.exceptionhandling.SoapFaultMapper;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.notification.SendNotificationRequest;
import org.opensmartgridplatform.adapter.ws.shared.services.DefaultNotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationService;
import org.opensmartgridplatform.adapter.ws.shared.services.NotificationServiceBlackHole;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.infra.ws.OrganisationIdentificationClientInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.server.endpoint.adapter.DefaultMethodEndpointAdapter;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;
import org.springframework.ws.server.endpoint.adapter.method.MethodArgumentResolver;
import org.springframework.ws.server.endpoint.adapter.method.MethodReturnValueHandler;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-microgrids.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsMicrogrids/config}", ignoreResourceNotFound = true)
public class WebServiceConfig extends AbstractConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceConfig.class);

    private static final String PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_MICROGRIDS_AD_HOC_MANAGEMENT = "jaxb2.marshaller.context.path.microgrids.adhocmanagement";

    private static final String ORGANISATION_IDENTIFICATION_HEADER = "OrganisationIdentification";
    private static final String ORGANISATION_IDENTIFICATION_CONTEXT = ORGANISATION_IDENTIFICATION_HEADER;

    private static final String USER_NAME_HEADER = "UserName";

    private static final String APPLICATION_NAME_HEADER = "ApplicationName";

    private static final String X509_RDN_ATTRIBUTE_ID = "cn";
    private static final String X509_RDN_ATTRIBUTE_VALUE_CONTEXT_PROPERTY_NAME = "CommonNameSet";

    @Value("${web.service.notification.enabled}")
    private boolean webserviceNotificationEnabled;

    @Value("${web.service.notification.username:#{null}}")
    private String webserviceNotificationUsername;

    @Value("${web.service.notification.organisation:OSGP}")
    private String webserviceNotificationOrganisation;

    @Value("${web.service.notification.application.name}")
    private String webserviceNotificationApplicationName;

    private static final String SERVER = "SERVER";

    // === MICROGRIDS MARSHALLERS ===

    /**
     * Method for creating the Marshaller for schedule management.
     *
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller microgridsAdHocManagementMarshaller() {
        LOGGER.debug("Creating Public Lighting Ad Hoc Management JAXB 2 Marshaller Bean");

        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.environment
                .getRequiredProperty(PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_MICROGRIDS_AD_HOC_MANAGEMENT));

        return marshaller;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for public
     * lighting schedule management.
     *
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor microgridsAdHocManagementMarshallingPayloadMethodProcessor() {
        LOGGER.debug("Creating Public Lighting Ad Hoc Management Marshalling Payload Method Processor Bean");

        return new MarshallingPayloadMethodProcessor(this.microgridsAdHocManagementMarshaller(),
                this.microgridsAdHocManagementMarshaller());
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

        // Add Public Lighting Marshalling Payload Method Processors to Method
        // Argument Resolvers
        methodArgumentResolvers.add(this.microgridsAdHocManagementMarshallingPayloadMethodProcessor());

        // Add Organisation Identification Annotation Method Argument Resolver
        methodArgumentResolvers.add(new AnnotationMethodArgumentResolver(ORGANISATION_IDENTIFICATION_CONTEXT,
                OrganisationIdentification.class));
        defaultMethodEndpointAdapter.setMethodArgumentResolvers(methodArgumentResolvers);

        final List<MethodReturnValueHandler> methodReturnValueHandlers = new ArrayList<>();

        // Add Microgrids Marshalling Payload Method Processors to Method
        // Return Value Handlers
        methodReturnValueHandlers.add(this.microgridsAdHocManagementMarshallingPayloadMethodProcessor());

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
        props.put("org.opensmartgridplatform.shared.exceptionhandling.FunctionalException", SERVER);
        props.put("org.opensmartgridplatform.shared.exceptionhandling.TechnicalException", SERVER);
        props.put("org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException", SERVER);

        exceptionResolver.setExceptionMappings(props);
        return exceptionResolver;
    }

    // === ENDPOINT INTERCEPTORS ===

    @Bean
    public X509CertificateRdnAttributeValueEndpointInterceptor x509CertificateSubjectCnEndpointInterceptor() {
        return new X509CertificateRdnAttributeValueEndpointInterceptor(X509_RDN_ATTRIBUTE_ID,
                X509_RDN_ATTRIBUTE_VALUE_CONTEXT_PROPERTY_NAME);
    }

    @Bean
    public SoapHeaderEndpointInterceptor organisationIdentificationInterceptor() {
        return new SoapHeaderEndpointInterceptor(ORGANISATION_IDENTIFICATION_HEADER,
                ORGANISATION_IDENTIFICATION_CONTEXT);
    }

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

    @Bean
    public NotificationService microgridsNotificationService(
            final NotificationWebServiceTemplateFactory templateFactory) {
        if (!this.webserviceNotificationEnabled) {
            return new NotificationServiceBlackHole();
        }
        final Class<SendNotificationRequest> notificationRequestType = SendNotificationRequest.class;
        final MapperFacade mapper = new DefaultMapperFactory.Builder().build().getMapperFacade();
        return new DefaultNotificationService<>(templateFactory, notificationRequestType, mapper);
    }

    @Bean
    public NotificationWebServiceTemplateFactory notificationWebServiceTemplateFactory(
            final NotificationWebServiceConfigurationRepository configRepository) {

        final ClientInterceptor addOsgpHeadersInterceptor = OrganisationIdentificationClientInterceptor.newBuilder()
                .withOrganisationIdentification(this.webserviceNotificationOrganisation)
                .withUserName(this.webserviceNotificationUsername)
                .withApplicationName(this.webserviceNotificationApplicationName).build();

        return new NotificationWebServiceTemplateFactory(configRepository, this.messageFactory(),
                Arrays.asList(addOsgpHeadersInterceptor));
    }

    @Bean
    public SaajSoapMessageFactory messageFactory() {
        return new SaajSoapMessageFactory();
    }

}
