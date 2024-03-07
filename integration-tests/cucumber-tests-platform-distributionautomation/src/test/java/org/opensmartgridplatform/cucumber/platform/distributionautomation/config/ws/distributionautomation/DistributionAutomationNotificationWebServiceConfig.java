// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.distributionautomation.config.ws.distributionautomation;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.AnnotationMethodArgumentResolver;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.SoapHeaderEndpointInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
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
public class DistributionAutomationNotificationWebServiceConfig extends WsConfigurerAdapter {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DistributionAutomationNotificationWebServiceConfig.class);

  private static final String ORGANISATION_IDENTIFICATION_HEADER = "OrganisationIdentification";

  @Value("${jaxb2.marshaller.context.path.distributionautomation.notification}")
  private String notificationMarshallerContextPath;

  @Value("${web.service.distributionautomation.notification.application.name}")
  private String notificationApplicationName;

  @Value("${web.service.distributionautomation.notification.context}")
  private String notificationContextPath;

  @Value("${web.service.distributionautomation.notification.port}")
  private int notificationPort;

  @Bean("wsDistributionAutomationNotificationApplicationName")
  public String notificationApplicationName() {
    return this.notificationApplicationName;
  }

  @Bean("wsDistributionAutomationNotificationMarshallerContextPath")
  public String notificationMarshallerContextPath() {
    return this.notificationMarshallerContextPath;
  }

  @Bean("wsDistributionAutomationNotificationTargetUri")
  public String notificationTargetUri() {
    return "http://localhost:" + this.notificationPort + this.notificationContextPath;
  }

  @Override
  public void addInterceptors(final List<EndpointInterceptor> interceptors) {
    interceptors.add(
        new SoapHeaderEndpointInterceptor(
            ORGANISATION_IDENTIFICATION_HEADER, ORGANISATION_IDENTIFICATION_HEADER));
  }

  @Bean("wsDistributionAutomationNotificationHttpServer")
  public HttpServer httpServer(
      final SaajSoapMessageFactory messageFactory,
      final PayloadRootAnnotationMethodEndpointMapping mapping)
      throws IOException {

    LOGGER.info(
        "Initializing distribution automation notifications HTTP server with context path: '{}' and port: '{}'",
        this.notificationContextPath,
        this.notificationPort);

    final SoapMessageDispatcher soapMessageDispatcher = new SoapMessageDispatcher();
    soapMessageDispatcher.setEndpointMappings(Arrays.asList(mapping));
    soapMessageDispatcher.setEndpointAdapters(Arrays.asList(this.defaultMethodEndpointAdapter()));

    final WebServiceMessageReceiverHttpHandler httpHandler =
        new WebServiceMessageReceiverHttpHandler();
    httpHandler.setMessageReceiver(soapMessageDispatcher);
    httpHandler.setMessageFactory(messageFactory);

    final HttpServer httpServer =
        HttpServer.create(new InetSocketAddress(this.notificationPort), 1024);
    httpServer.createContext(this.notificationContextPath, httpHandler);
    httpServer.setExecutor(Executors.newCachedThreadPool());
    httpServer.start();

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
    return new AnnotationMethodArgumentResolver(
        ORGANISATION_IDENTIFICATION_HEADER, OrganisationIdentification.class);
  }

  private MarshallingPayloadMethodProcessor marshallingPayloadMethodProcessor() {
    final Jaxb2Marshaller marshaller = this.notificationMarshaller();
    return new MarshallingPayloadMethodProcessor(marshaller, marshaller);
  }

  private Jaxb2Marshaller notificationMarshaller() {
    LOGGER.info(
        "Initializing microgrids notification marshaller with context path: '{}'",
        this.notificationMarshallerContextPath);

    final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setContextPath(this.notificationMarshallerContextPath);
    return marshaller;
  }
}
