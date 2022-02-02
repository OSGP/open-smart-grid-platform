/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
  private String contextPathCoreNotification;

  @Value("${web.service.core.notification.context}")
  private String notificationContextPath;

  @Value("${web.service.core.notification.port}")
  private int notificationPort;

  @Override
  public void addInterceptors(final List<EndpointInterceptor> interceptors) {
    interceptors.add(
        new SoapHeaderEndpointInterceptor(
            ORGANISATION_IDENTIFICATION_HEADER, ORGANISATION_IDENTIFICATION_HEADER));
  }

  @Bean
  public SimpleHttpServerFactoryBean coreNotificationsHttpServer(
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

    final List<MethodArgumentResolver> methodArgumentResolvers = new ArrayList<>();
    methodArgumentResolvers.add(this.coreNotificationMarshallingPayloadMethodProcessor());
    methodArgumentResolvers.add(
        new AnnotationMethodArgumentResolver(
            ORGANISATION_IDENTIFICATION_HEADER, OrganisationIdentification.class));
    defaultMethodEndpointAdapter.setMethodArgumentResolvers(methodArgumentResolvers);

    final List<MethodReturnValueHandler> methodReturnValueHandlers = new ArrayList<>();
    methodReturnValueHandlers.add(this.coreNotificationMarshallingPayloadMethodProcessor());
    defaultMethodEndpointAdapter.setMethodReturnValueHandlers(methodReturnValueHandlers);

    return defaultMethodEndpointAdapter;
  }

  private Jaxb2Marshaller coreNotificationMarshaller() {

    LOGGER.info(
        "Initializing core notification marshaller with context path '{}'",
        this.contextPathCoreNotification);

    final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setContextPath(this.contextPathCoreNotification);
    return marshaller;
  }

  private MarshallingPayloadMethodProcessor coreNotificationMarshallingPayloadMethodProcessor() {
    return new MarshallingPayloadMethodProcessor(
        this.coreNotificationMarshaller(), this.coreNotificationMarshaller());
  }
}
