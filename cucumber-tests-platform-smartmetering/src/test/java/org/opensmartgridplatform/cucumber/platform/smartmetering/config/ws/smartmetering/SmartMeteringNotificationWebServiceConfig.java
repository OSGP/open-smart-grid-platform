/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.config.ws.smartmetering;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

import org.opensmartgridplatform.adapter.ws.endpointinterceptors.AnnotationMethodArgumentResolver;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.OrganisationIdentification;
import org.opensmartgridplatform.adapter.ws.endpointinterceptors.SoapHeaderEndpointInterceptor;

@EnableWs
@Configuration
public class SmartMeteringNotificationWebServiceConfig extends WsConfigurerAdapter {

    private static final String ORGANISATION_IDENTIFICATION_HEADER = "OrganisationIdentification";

    @Value("${jaxb2.marshaller.context.path.smartmetering.notification}")
    private String contextPathSmartMeteringNotification;

    @Value("${web.service.notification.context}")
    private String notificationContextPath;

    @Value("${web.service.notification.port}")
    private int notificationPort;

    @Bean
    public DefaultMethodEndpointAdapter defaultMethodEndpointAdapter() {
        final DefaultMethodEndpointAdapter defaultMethodEndpointAdapter = new DefaultMethodEndpointAdapter();

        final List<MethodArgumentResolver> methodArgumentResolvers = new ArrayList<>();
        methodArgumentResolvers.add(this.smartMeteringNotificationMarshallingPayloadMethodProcessor());
        methodArgumentResolvers.add(new AnnotationMethodArgumentResolver(ORGANISATION_IDENTIFICATION_HEADER,
                OrganisationIdentification.class));
        defaultMethodEndpointAdapter.setMethodArgumentResolvers(methodArgumentResolvers);

        final List<MethodReturnValueHandler> methodReturnValueHandlers = new ArrayList<>();
        methodReturnValueHandlers.add(this.smartMeteringNotificationMarshallingPayloadMethodProcessor());
        defaultMethodEndpointAdapter.setMethodReturnValueHandlers(methodReturnValueHandlers);

        return defaultMethodEndpointAdapter;
    }

    @Override
    public void addInterceptors(final List<EndpointInterceptor> interceptors) {
        interceptors.add(new SoapHeaderEndpointInterceptor(ORGANISATION_IDENTIFICATION_HEADER,
                ORGANISATION_IDENTIFICATION_HEADER));
    }

    @Bean
    public SimpleHttpServerFactoryBean httpServer(final SaajSoapMessageFactory messageFactory,
            final DefaultMethodEndpointAdapter defaultMethodEndpointAdapter,
            final PayloadRootAnnotationMethodEndpointMapping mapping) {

        final SoapMessageDispatcher soapMessageDispatcher = new SoapMessageDispatcher();
        soapMessageDispatcher.setEndpointMappings(Arrays.asList(mapping));
        soapMessageDispatcher.setEndpointAdapters(Arrays.asList(defaultMethodEndpointAdapter));

        final WebServiceMessageReceiverHttpHandler httpHandler = new WebServiceMessageReceiverHttpHandler();
        httpHandler.setMessageReceiver(soapMessageDispatcher);
        httpHandler.setMessageFactory(messageFactory);

        final SimpleHttpServerFactoryBean httpServer = new SimpleHttpServerFactoryBean();
        httpServer.setContexts(Collections.singletonMap(this.notificationContextPath, httpHandler));
        httpServer.setPort(this.notificationPort);

        return httpServer;
    }

    @Bean
    public Jaxb2Marshaller smartMeteringNotificationMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(this.contextPathSmartMeteringNotification);
        return marshaller;
    }

    @Bean
    public MarshallingPayloadMethodProcessor smartMeteringNotificationMarshallingPayloadMethodProcessor() {
        return new MarshallingPayloadMethodProcessor(this.smartMeteringNotificationMarshaller(),
                this.smartMeteringNotificationMarshaller());
    }
}
