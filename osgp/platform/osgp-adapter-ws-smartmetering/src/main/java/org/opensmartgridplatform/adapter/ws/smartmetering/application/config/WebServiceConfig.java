/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.AnnotationMethodArgumentResolver;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.BypassRetry;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.CertificateAndSoapHeaderAuthorizationEndpointInterceptor;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MaxScheduleTime;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MessagePriority;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.ResponseUrl;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.ScheduleTime;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.SoapHeaderEndpointInterceptor;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.SoapHeaderInterceptor;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.WebServiceMonitorInterceptor;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.WebServiceMonitorInterceptorCapabilities;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.X509CertificateRdnAttributeValueEndpointInterceptor;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.exceptionhandling.DetailSoapFaultMappingExceptionResolver;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.exceptionhandling.SoapFaultMapper;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.DefaultMethodEndpointAdapter;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;
import org.springframework.ws.server.endpoint.adapter.method.MethodArgumentResolver;
import org.springframework.ws.server.endpoint.adapter.method.MethodReturnValueHandler;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-smartmetering.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsSmartMetering/config}", ignoreResourceNotFound = true)
public class WebServiceConfig extends AbstractConfig {

  private static final String SERVER = "SERVER";

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

  private static final String PROPERTY_NAME_SOAP_MESSAGE_LOGGING_ENABLED =
      "soap.message.logging.enabled";
  private static final String PROPERTY_NAME_SOAP_MESSAGE_PRINTING_ENABLED =
      "soap.message.printing.enabled";

  private static final String ORGANISATION_IDENTIFICATION_HEADER = "OrganisationIdentification";
  private static final String ORGANISATION_IDENTIFICATION_CONTEXT =
      ORGANISATION_IDENTIFICATION_HEADER;

  private static final String USER_NAME_HEADER = "UserName";
  private static final String APPLICATION_NAME_HEADER = "ApplicationName";

  private static final String MESSAGE_PRIORITY_HEADER = "MessagePriority";
  private static final String MESSAGE_SCHEDULETIME_HEADER = "ScheduleTime";
  public static final String MESSAGE_MAXSCHEDULETIME_HEADER = "MaxScheduleTime";
  private static final String MESSAGE_RESPONSE_URL_HEADER = "ResponseUrl";
  private static final String BYPASS_RETRY_HEADER = "BypassRetry";

  private static final String X509_RDN_ATTRIBUTE_ID = "cn";
  private static final String X509_RDN_ATTRIBUTE_VALUE_CONTEXT_PROPERTY_NAME = "CommonNameSet";

  private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceConfig.class);

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
   * Method for creating the Marshalling Payload Method Processor for Smart Metering management.
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor
      smartMeteringManagementMarshallingPayloadMethodProcessor() {
    return new MarshallingPayloadMethodProcessor(
        this.smartMeteringManagementMarshaller(), this.smartMeteringManagementMarshaller());
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
   * Method for creating the Marshalling Payload Method Processor for Smart Metering bundle.
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor smartMeteringBundleMarshallingPayloadMethodProcessor() {
    return new MarshallingPayloadMethodProcessor(
        this.smartMeteringBundleMarshaller(), this.smartMeteringBundleMarshaller());
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
   * Method for creating the Marshalling Payload Method Processor for Smart Metering installation.
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor
      smartMeteringInstallationMarshallingPayloadMethodProcessor() {
    return new MarshallingPayloadMethodProcessor(
        this.smartMeteringInstallationMarshaller(), this.smartMeteringInstallationMarshaller());
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
   * Method for creating the Marshalling Payload Method Processor for Smart Metering adhoc.
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor smartMeteringAdhocMarshallingPayloadMethodProcessor() {
    return new MarshallingPayloadMethodProcessor(
        this.smartMeteringAdhocMarshaller(), this.smartMeteringAdhocMarshaller());
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
   * Method for creating the Marshalling Payload Method Processor for Smart Metering configuration.
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor
      smartMeteringConfigurationMarshallingPayloadMethodProcessor() {
    return new MarshallingPayloadMethodProcessor(
        this.smartMeteringConfigurationMarshaller(), this.smartMeteringConfigurationMarshaller());
  }

  /**
   * Method for creating the Marshalling Payload Method Processor for Smart Metering monitoring.
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor
      smartMeteringMonitoringMarshallingPayloadMethodProcessor() {
    return new MarshallingPayloadMethodProcessor(
        this.smartMeteringMonitoringMarshaller(), this.smartMeteringMonitoringMarshaller());
  }

  /**
   * Method for creating the Default Method Endpoint Adapter.
   *
   * @return DefaultMethodEndpointAdapter
   */
  @Bean
  public DefaultMethodEndpointAdapter defaultMethodEndpointAdapter() {
    final DefaultMethodEndpointAdapter defaultMethodEndpointAdapter =
        new DefaultMethodEndpointAdapter();

    final List<MethodArgumentResolver> methodArgumentResolvers = new ArrayList<>();

    // SMART METERING
    methodArgumentResolvers.add(this.smartMeteringManagementMarshallingPayloadMethodProcessor());
    methodArgumentResolvers.add(this.smartMeteringBundleMarshallingPayloadMethodProcessor());
    methodArgumentResolvers.add(this.smartMeteringInstallationMarshallingPayloadMethodProcessor());
    methodArgumentResolvers.add(this.smartMeteringMonitoringMarshallingPayloadMethodProcessor());
    methodArgumentResolvers.add(this.smartMeteringAdhocMarshallingPayloadMethodProcessor());
    methodArgumentResolvers.add(this.smartMeteringConfigurationMarshallingPayloadMethodProcessor());

    methodArgumentResolvers.add(
        new AnnotationMethodArgumentResolver(
            ORGANISATION_IDENTIFICATION_CONTEXT, OrganisationIdentification.class));
    methodArgumentResolvers.add(
        new AnnotationMethodArgumentResolver(MESSAGE_PRIORITY_HEADER, MessagePriority.class));
    methodArgumentResolvers.add(
        new AnnotationMethodArgumentResolver(MESSAGE_SCHEDULETIME_HEADER, ScheduleTime.class));
    methodArgumentResolvers.add(
        new AnnotationMethodArgumentResolver(
            MESSAGE_MAXSCHEDULETIME_HEADER, MaxScheduleTime.class, true));
    methodArgumentResolvers.add(
        new AnnotationMethodArgumentResolver(MESSAGE_RESPONSE_URL_HEADER, ResponseUrl.class));
    methodArgumentResolvers.add(
        new AnnotationMethodArgumentResolver(BYPASS_RETRY_HEADER, BypassRetry.class));
    defaultMethodEndpointAdapter.setMethodArgumentResolvers(methodArgumentResolvers);

    final List<MethodReturnValueHandler> methodReturnValueHandlers = new ArrayList<>();

    // SMART METERING
    methodReturnValueHandlers.add(this.smartMeteringManagementMarshallingPayloadMethodProcessor());
    methodReturnValueHandlers.add(this.smartMeteringBundleMarshallingPayloadMethodProcessor());
    methodReturnValueHandlers.add(
        this.smartMeteringInstallationMarshallingPayloadMethodProcessor());
    methodReturnValueHandlers.add(this.smartMeteringMonitoringMarshallingPayloadMethodProcessor());
    methodReturnValueHandlers.add(this.smartMeteringAdhocMarshallingPayloadMethodProcessor());
    methodReturnValueHandlers.add(
        this.smartMeteringConfigurationMarshallingPayloadMethodProcessor());

    defaultMethodEndpointAdapter.setMethodReturnValueHandlers(methodReturnValueHandlers);

    return defaultMethodEndpointAdapter;
  }

  @Bean
  public DetailSoapFaultMappingExceptionResolver exceptionResolver() {

    LOGGER.debug("Creating Detail Soap Fault Mapping Exception Resolver Bean");

    final DetailSoapFaultMappingExceptionResolver exceptionResolver =
        new DetailSoapFaultMappingExceptionResolver(new SoapFaultMapper());
    exceptionResolver.setOrder(1);

    final Properties props = new Properties();
    props.put("org.opensmartgridplatform.shared.exceptionhandling.OsgpException", SERVER);
    props.put("org.opensmartgridplatform.shared.exceptionhandling.FunctionalException", SERVER);
    props.put("org.opensmartgridplatform.shared.exceptionhandling.TechnicalException", SERVER);
    props.put(
        "org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException", SERVER);
    exceptionResolver.setExceptionMappings(props);
    return exceptionResolver;
  }

  @Bean
  public X509CertificateRdnAttributeValueEndpointInterceptor
      x509CertificateSubjectCnEndpointInterceptor() {
    return new X509CertificateRdnAttributeValueEndpointInterceptor(
        X509_RDN_ATTRIBUTE_ID, X509_RDN_ATTRIBUTE_VALUE_CONTEXT_PROPERTY_NAME);
  }

  @Bean
  public SoapHeaderEndpointInterceptor organisationIdentificationInterceptor() {
    return new SoapHeaderEndpointInterceptor(
        ORGANISATION_IDENTIFICATION_HEADER, ORGANISATION_IDENTIFICATION_CONTEXT);
  }

  @Bean
  public SoapHeaderInterceptor messagePriorityInterceptor() {
    LOGGER.debug("Creating Message Priority Interceptor Bean");

    return new SoapHeaderInterceptor(MESSAGE_PRIORITY_HEADER, MESSAGE_PRIORITY_HEADER);
  }

  @Bean
  public SoapHeaderInterceptor scheduleTimeInterceptor() {
    return new SoapHeaderInterceptor(MESSAGE_SCHEDULETIME_HEADER, MESSAGE_SCHEDULETIME_HEADER);
  }

  @Bean
  public SoapHeaderInterceptor maxScheduleTimeInterceptor() {
    return new SoapHeaderInterceptor(
        MESSAGE_MAXSCHEDULETIME_HEADER, MESSAGE_MAXSCHEDULETIME_HEADER);
  }

  @Bean
  public SoapHeaderInterceptor responseUrlInterceptor() {
    return new SoapHeaderInterceptor(MESSAGE_RESPONSE_URL_HEADER, MESSAGE_RESPONSE_URL_HEADER);
  }

  @Bean
  public SoapHeaderInterceptor bypassRetryInterceptor() {
    return new SoapHeaderInterceptor(BYPASS_RETRY_HEADER, BYPASS_RETRY_HEADER);
  }

  @Bean
  public CertificateAndSoapHeaderAuthorizationEndpointInterceptor
      organisationIdentificationInCertificateCnEndpointInterceptor() {
    return new CertificateAndSoapHeaderAuthorizationEndpointInterceptor(
        X509_RDN_ATTRIBUTE_VALUE_CONTEXT_PROPERTY_NAME, ORGANISATION_IDENTIFICATION_CONTEXT);
  }

  @Bean
  public WebServiceMonitorInterceptor webServiceMonitorInterceptor() {
    final boolean soapMessageLoggingEnabled =
        this.environment.getProperty(
            PROPERTY_NAME_SOAP_MESSAGE_LOGGING_ENABLED, boolean.class, false);
    final boolean soapMessagePrintingEnabled =
        this.environment.getProperty(
            PROPERTY_NAME_SOAP_MESSAGE_PRINTING_ENABLED, boolean.class, true);

    final WebServiceMonitorInterceptorCapabilities capabilities =
        new WebServiceMonitorInterceptorCapabilities(
            soapMessageLoggingEnabled, soapMessagePrintingEnabled);

    return new WebServiceMonitorInterceptor(
        ORGANISATION_IDENTIFICATION_HEADER,
        USER_NAME_HEADER,
        APPLICATION_NAME_HEADER,
        capabilities);
  }
}
