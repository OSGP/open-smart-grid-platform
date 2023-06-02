//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.da.application.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.opensmartgridplatform.adapter.ws.clients.NotificationWebServiceTemplateFactory;
import org.opensmartgridplatform.adapter.ws.da.application.exceptionhandling.DetailSoapFaultMappingExceptionResolver;
import org.opensmartgridplatform.adapter.ws.da.application.exceptionhandling.SoapFaultMapper;
import org.opensmartgridplatform.adapter.ws.da.application.mapping.DistributionAutomationMapper;
import org.opensmartgridplatform.adapter.ws.domain.repositories.NotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.AnnotationMethodArgumentResolver;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.CertificateAndSoapHeaderAuthorizationEndpointInterceptor;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.SoapHeaderEndpointInterceptor;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.WebServiceMonitorInterceptor;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.WebServiceMonitorInterceptorCapabilities;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.X509CertificateRdnAttributeValueEndpointInterceptor;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.notification.SendNotificationRequest;
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

@Configuration
@PropertySource("classpath:osgp-adapter-ws-distributionautomation.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(
    value = "file:${osgp/AdapterWsDistributionAutomation/config}",
    ignoreResourceNotFound = true)
public class WebServiceConfig extends AbstractConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceConfig.class);

  private static final String
      PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_DISTRIBUTION_AUTOMATION_GENERIC =
          "jaxb2.marshaller.context.path.distributionautomation.generic";

  private static final String PROPERTY_NAME_SOAP_MESSAGE_LOGGING_ENABLED =
      "soap.message.logging.enabled";
  private static final String PROPERTY_NAME_SOAP_MESSAGE_PRINTING_ENABLED =
      "soap.message.printing.enabled";

  private static final String ORGANISATION_IDENTIFICATION_HEADER = "OrganisationIdentification";
  private static final String ORGANISATION_IDENTIFICATION_CONTEXT =
      ORGANISATION_IDENTIFICATION_HEADER;

  private static final String USER_NAME_HEADER = "UserName";

  private static final String APPLICATION_NAME_HEADER = "ApplicationName";

  private static final String X509_RDN_ATTRIBUTE_ID = "cn";
  private static final String X509_RDN_ATTRIBUTE_VALUE_CONTEXT_PROPERTY_NAME = "CommonNameSet";
  private static final String SERVER = "SERVER";

  @Value("${web.service.notification.enabled}")
  private boolean webserviceNotificationEnabled;

  @Value("${web.service.notification.username:#{null}}")
  private String webserviceNotificationUsername;

  @Value("${web.service.notification.organisation:OSGP}")
  private String webserviceNotificationOrganisation;

  @Value("${web.service.notification.application.name:DISTRIBUTION_AUTOMATION}")
  private String webserviceNotificationApplicationName;

  // === DISTRIBUTION AUTOMATION MARSHALLERS ===

  /**
   * Method for creating the Marshaller for Distribution Automation Generic (Ad Hoc Management,
   * Device Management and Monitoring)
   *
   * @return Jaxb2Marshaller
   */
  @Bean
  public Jaxb2Marshaller distributionautomationGenericMarshaller() {
    LOGGER.debug("Creating Distribution Automation Generic JAXB 2 Marshaller Bean");

    final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setContextPath(
        this.environment.getRequiredProperty(
            PROPERTY_NAME_MARSHALLER_CONTEXT_PATH_DISTRIBUTION_AUTOMATION_GENERIC));
    return marshaller;
  }

  /**
   * Method for creating the Marshalling Payload Method Processor for Distribution Automation
   * Generic
   *
   * @return MarshallingPayloadMethodProcessor
   */
  @Bean
  public MarshallingPayloadMethodProcessor
      distributionautomationGenericMarshallingPayloadMethodProcessor() {
    LOGGER.debug(
        "Creating Distribution Automation Generic Marshalling Payload Method Processor Bean");

    return new MarshallingPayloadMethodProcessor(
        this.distributionautomationGenericMarshaller(),
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

    final DefaultMethodEndpointAdapter defaultMethodEndpointAdapter =
        new DefaultMethodEndpointAdapter();

    final List<MethodArgumentResolver> methodArgumentResolvers = new ArrayList<>();

    // Add Distribution Automation Marshalling Payload Method Processors to
    // Method
    // Argument Resolvers
    methodArgumentResolvers.add(
        this.distributionautomationGenericMarshallingPayloadMethodProcessor());

    // Add Organisation Identification Annotation Method Argument Resolver
    methodArgumentResolvers.add(
        new AnnotationMethodArgumentResolver(
            ORGANISATION_IDENTIFICATION_CONTEXT, OrganisationIdentification.class));
    defaultMethodEndpointAdapter.setMethodArgumentResolvers(methodArgumentResolvers);

    final List<MethodReturnValueHandler> methodReturnValueHandlers = new ArrayList<>();

    // Add Distribution Automation Marshalling Payload Method Processors to
    // Method
    // Return Value Handlers
    methodReturnValueHandlers.add(
        this.distributionautomationGenericMarshallingPayloadMethodProcessor());

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

  @Bean
  public String webserviceNotificationApplicationName() {
    return this.webserviceNotificationApplicationName;
  }

  @Bean
  public String webserviceNotificationOrganisation() {
    return this.webserviceNotificationOrganisation;
  }

  @Bean
  public NotificationService distributionAutomationNotificationService(
      final NotificationWebServiceTemplateFactory templateFactory,
      final DistributionAutomationMapper mapper) {

    if (!this.webserviceNotificationEnabled) {
      return new NotificationServiceBlackHole();
    }
    final Class<SendNotificationRequest> notificationRequestType = SendNotificationRequest.class;
    return new DefaultNotificationService<>(
        templateFactory,
        notificationRequestType,
        mapper,
        this.webserviceNotificationApplicationName);
  }

  @Bean
  public NotificationWebServiceTemplateFactory notificationWebServiceTemplateFactory(
      final NotificationWebServiceConfigurationRepository configRepository) {

    final ClientInterceptor addOsgpHeadersInterceptor =
        OrganisationIdentificationClientInterceptor.newBuilder()
            .withOrganisationIdentification(this.webserviceNotificationOrganisation)
            .withUserName(this.webserviceNotificationUsername)
            .withApplicationName(this.webserviceNotificationApplicationName)
            .build();

    return new NotificationWebServiceTemplateFactory(
        configRepository, this.messageFactory(), Arrays.asList(addOsgpHeadersInterceptor));
  }

  @Bean
  public SaajSoapMessageFactory messageFactory() {
    return new SaajSoapMessageFactory();
  }
}
