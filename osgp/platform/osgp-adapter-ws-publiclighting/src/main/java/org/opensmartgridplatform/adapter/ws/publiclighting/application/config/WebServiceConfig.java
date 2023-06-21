// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.publiclighting.application.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.AnnotationMethodArgumentResolver;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.CertificateAndSoapHeaderAuthorizationEndpointInterceptor;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.MessagePriority;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.SoapHeaderEndpointInterceptor;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.SoapHeaderInterceptor;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.WebServiceMonitorInterceptor;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.WebServiceMonitorInterceptorCapabilities;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.X509CertificateRdnAttributeValueEndpointInterceptor;
import org.opensmartgridplatform.adapter.ws.publiclighting.application.exceptionhandling.DetailSoapFaultMappingExceptionResolver;
import org.opensmartgridplatform.adapter.ws.publiclighting.application.exceptionhandling.SoapFaultMapper;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.server.endpoint.adapter.DefaultMethodEndpointAdapter;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;
import org.springframework.ws.server.endpoint.adapter.method.MethodArgumentResolver;
import org.springframework.ws.server.endpoint.adapter.method.MethodReturnValueHandler;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-publiclighting.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(
    value = "file:${osgp/AdapterWsPublicLighting/config}",
    ignoreResourceNotFound = true)
public class WebServiceConfig extends AbstractConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceConfig.class);

  private static final String
      PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_PUBLIC_LIGHTING_AD_HOC_MANAGEMENT =
          "jaxb2.marshaller.context.path.publiclighting.adhocmanagement";
  private static final String
      PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_PUBLIC_LIGHTING_SCHEDULE_MANAGEMENT =
          "jaxb2.marshaller.context.path.publiclighting.schedulemanagement";

  private static final String PROPERTY_NAME_SOAP_MESSAGE_LOGGING_ENABLED =
      "soap.message.logging.enabled";
  private static final String PROPERTY_NAME_SOAP_MESSAGE_PRINTING_ENABLED =
      "soap.message.printing.enabled";

  private static final String ORGANISATION_IDENTIFICATION_HEADER = "OrganisationIdentification";
  private static final String ORGANISATION_IDENTIFICATION_CONTEXT =
      ORGANISATION_IDENTIFICATION_HEADER;

  private static final String MESSAGE_PRIORITY_HEADER = "MessagePriority";
  private static final String USER_NAME_HEADER = "UserName";
  private static final String APPLICATION_NAME_HEADER = "ApplicationName";

  private static final String X509_RDN_ATTRIBUTE_ID = "cn";
  private static final String X509_RDN_ATTRIBUTE_VALUE_CONTEXT_PROPERTY_NAME = "CommonNameSet";

  private static final String SERVER = "SERVER";

  // === PUBLIC LIGHTING MARSHALLERS ===

  /**
   * Method for creating the Marshaller for schedule management.
   *
   * @return Jaxb2Marshaller
   */
  @Bean
  public Jaxb2Marshaller publicLightingAdHocManagementMarshaller() {
    LOGGER.debug("Creating Public Lighting Ad Hoc Management JAXB 2 Marshaller Bean");

    final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

    marshaller.setContextPath(
        this.environment.getRequiredProperty(
            PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_PUBLIC_LIGHTING_AD_HOC_MANAGEMENT));

    return marshaller;
  }

  /**
   * Method for creating the Marshalling Payload Method Processor for public lighting schedule
   * management.
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor
      publicLightingAdHocManagementMarshallingPayloadMethodProcessor() {
    LOGGER.debug(
        "Creating Public Lighting Ad Hoc Management Marshalling Payload Method Processor Bean");

    return new MarshallingPayloadMethodProcessor(
        this.publicLightingAdHocManagementMarshaller(),
        this.publicLightingAdHocManagementMarshaller());
  }

  /**
   * Method for creating the Marshaller for schedule management.
   *
   * @return Jaxb2Marshaller
   */
  @Bean
  public Jaxb2Marshaller publicLightingScheduleManagementMarshaller() {
    LOGGER.debug("Creating Public Lighting Schedule Management Marshaller Bean");

    final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

    marshaller.setContextPath(
        this.environment.getRequiredProperty(
            PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_PUBLIC_LIGHTING_SCHEDULE_MANAGEMENT));

    return marshaller;
  }

  /**
   * Method for creating the Marshalling Payload Method Processor for Tariff Switching schedule
   * management.
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor
      publicLightingScheduleManagementMarshallingPayloadMethodProcessor() {
    LOGGER.debug(
        "Creating Public Lighting Schedule Management Marshalling Payload Method Processor Bean");

    return new MarshallingPayloadMethodProcessor(
        this.publicLightingScheduleManagementMarshaller(),
        this.publicLightingScheduleManagementMarshaller());
  }

  /**
   * Method for creating the Default Method Endpoint Adapter.
   *
   * @return DefaultMethodEndpointAdapter
   */
  @Bean
  public DefaultMethodEndpointAdapter defaultMethodEndpointAdapter() {
    LOGGER.debug("Creating Default Method Endpoint Adapter Bean");

    final DefaultMethodEndpointAdapter defaultMethodEndpointAdapter =
        new DefaultMethodEndpointAdapter();

    final List<MethodArgumentResolver> methodArgumentResolvers = new ArrayList<>();

    // Add Public Lighting Marshalling Payload Method Processors to Method
    // Argument Resolvers
    methodArgumentResolvers.add(
        this.publicLightingAdHocManagementMarshallingPayloadMethodProcessor());
    methodArgumentResolvers.add(
        this.publicLightingScheduleManagementMarshallingPayloadMethodProcessor());

    // Add Organisation Identification Annotation Method Argument Resolver
    methodArgumentResolvers.add(
        new AnnotationMethodArgumentResolver(
            ORGANISATION_IDENTIFICATION_CONTEXT, OrganisationIdentification.class));
    methodArgumentResolvers.add(
        new AnnotationMethodArgumentResolver(MESSAGE_PRIORITY_HEADER, MessagePriority.class));
    defaultMethodEndpointAdapter.setMethodArgumentResolvers(methodArgumentResolvers);

    final List<MethodReturnValueHandler> methodReturnValueHandlers = new ArrayList<>();

    // Add Public Lighting Marshalling Payload Method Processors to Method
    // Return Value Handlers
    methodReturnValueHandlers.add(
        this.publicLightingAdHocManagementMarshallingPayloadMethodProcessor());
    methodReturnValueHandlers.add(
        this.publicLightingScheduleManagementMarshallingPayloadMethodProcessor());

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

  // === ENDPOINT INTERCEPTORS ===

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
    return new SoapHeaderInterceptor(MESSAGE_PRIORITY_HEADER, MESSAGE_PRIORITY_HEADER);
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
