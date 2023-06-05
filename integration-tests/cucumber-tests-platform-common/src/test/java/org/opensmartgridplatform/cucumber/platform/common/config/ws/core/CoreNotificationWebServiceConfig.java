// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.config.ws.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.AnnotationMethodArgumentResolver;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.SoapHeaderEndpointInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.remoting.support.SimpleHttpServerFactoryBean;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.endpoint.adapter.DefaultMethodEndpointAdapter;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;
import org.springframework.ws.server.endpoint.adapter.method.MethodArgumentResolver;
import org.springframework.ws.server.endpoint.adapter.method.MethodReturnValueHandler;
import org.springframework.ws.server.endpoint.mapping.PayloadRootAnnotationMethodEndpointMapping;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.server.SoapMessageDispatcher;
import org.springframework.ws.transport.http.WebServiceMessageReceiverHttpHandler;

@EnableWs
@Configuration
public class CoreNotificationWebServiceConfig extends WsConfigurerAdapter {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(CoreNotificationWebServiceConfig.class);

  private static final String ORGANISATION_IDENTIFICATION_HEADER = "OrganisationIdentification";

  @Value("${jaxb2.marshaller.context.path.core.notification}")
  private String notificationMarshallerContextPath;

  @Value("${web.service.core.notification.application.name}")
  private String notificationApplicationName;

  @Value("${web.service.core.notification.context}")
  private String notificationContextPath;

  @Value("${web.service.core.notification.port}")
  private int notificationPort;

  @Bean("wsCoreNotificationApplicationName")
  public String notificationApplicationName() {
    return this.notificationApplicationName;
  }

  @Bean("wsCoreNotificationMarshallerContextPath")
  public String notificationMarshallerContextPath() {
    return this.notificationMarshallerContextPath;
  }

  @Bean("wsCoreNotificationTargetUri")
  public String notificationTargetUri() {
    return "http://localhost:" + this.notificationPort + this.notificationContextPath;
  }

  @Override
  public void addInterceptors(final List<EndpointInterceptor> interceptors) {
    interceptors.add(
        new SoapHeaderEndpointInterceptor(
            ORGANISATION_IDENTIFICATION_HEADER, ORGANISATION_IDENTIFICATION_HEADER));
  }

  @Bean("wsCoreNotificationHttpServer")
  public SimpleHttpServerFactoryBean httpServer(
      final SaajSoapMessageFactory messageFactory,
      final PayloadRootAnnotationMethodEndpointMapping mapping) {

    LOGGER.info(
        "Initializing core notifications HTTP server with context path: '{}' and port: '{}'",
        this.notificationContextPath,
        this.notificationPort);

    final SoapMessageDispatcher soapMessageDispatcher = new SoapMessageDispatcher();
    soapMessageDispatcher.setEndpointMappings(Collections.singletonList(mapping));
    soapMessageDispatcher.setEndpointAdapters(
        Collections.singletonList(this.defaultMethodEndpointAdapter()));

    final WebServiceMessageReceiverHttpHandler httpHandler =
        new WebServiceMessageReceiverHttpHandler();
    httpHandler.setMessageReceiver(soapMessageDispatcher);
    httpHandler.setMessageFactory(messageFactory);

    final SimpleHttpServerFactoryBean httpServer = new SimpleHttpServerFactoryBean();
    httpServer.setContexts(Collections.singletonMap(this.notificationContextPath, httpHandler));
    httpServer.setPort(this.notificationPort);

    return httpServer;
  }

  private DefaultMethodEndpointAdapter defaultMethodEndpointAdapter() {
    final DefaultMethodEndpointAdapter defaultMethodEndpointAdapter =
        new DefaultMethodEndpointAdapter();

    final MarshallingPayloadMethodProcessor processor = this.marshallingPayloadMethodProcessor();
    final AnnotationMethodArgumentResolver resolver = this.annotationMethodArgumentResolver();

    final List<MethodArgumentResolver> methodArgumentResolvers = new ArrayList<>();
    methodArgumentResolvers.add(processor);
    methodArgumentResolvers.add(resolver);
    defaultMethodEndpointAdapter.setMethodArgumentResolvers(methodArgumentResolvers);

    final List<MethodReturnValueHandler> methodReturnValueHandlers = new ArrayList<>();
    methodReturnValueHandlers.add(processor);
    defaultMethodEndpointAdapter.setMethodReturnValueHandlers(methodReturnValueHandlers);

    return defaultMethodEndpointAdapter;
  }

  private AnnotationMethodArgumentResolver annotationMethodArgumentResolver() {
    final AnnotationMethodArgumentResolver resolver =
        new AnnotationMethodArgumentResolver(
            ORGANISATION_IDENTIFICATION_HEADER, OrganisationIdentification.class);
    return resolver;
  }

  private MarshallingPayloadMethodProcessor marshallingPayloadMethodProcessor() {
    final Jaxb2Marshaller marshaller = this.notificationMarshaller();
    final MarshallingPayloadMethodProcessor processor =
        new MarshallingPayloadMethodProcessor(marshaller, marshaller);
    return processor;
  }

  private Jaxb2Marshaller notificationMarshaller() {
    LOGGER.info(
        "Initializing core notification marshaller with context path '{}'",
        this.notificationMarshallerContextPath);

    final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setContextPath(this.notificationMarshallerContextPath);
    return marshaller;
  }
}
