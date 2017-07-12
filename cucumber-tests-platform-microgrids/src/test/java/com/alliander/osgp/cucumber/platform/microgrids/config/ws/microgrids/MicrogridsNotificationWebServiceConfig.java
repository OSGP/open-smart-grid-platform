/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.microgrids.config.ws.microgrids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.remoting.support.SimpleHttpServerFactoryBean;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.EndpointMapping;
import org.springframework.ws.server.endpoint.adapter.DefaultMethodEndpointAdapter;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;
import org.springframework.ws.server.endpoint.adapter.method.MethodArgumentResolver;
import org.springframework.ws.server.endpoint.adapter.method.MethodReturnValueHandler;
import org.springframework.ws.server.endpoint.mapping.PayloadRootAnnotationMethodEndpointMapping;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.server.SoapMessageDispatcher;
import org.springframework.ws.transport.http.WebServiceMessageReceiverHttpHandler;

import com.alliander.osgp.adapter.ws.endpointinterceptors.AnnotationMethodArgumentResolver;
import com.alliander.osgp.adapter.ws.endpointinterceptors.OrganisationIdentification;
import com.alliander.osgp.adapter.ws.endpointinterceptors.SoapHeaderEndpointInterceptor;
import com.alliander.osgp.cucumber.platform.microgrids.config.PlatformMicrogridsConfiguration;
import com.sun.net.httpserver.HttpHandler;

@EnableWs
@Configuration
public class MicrogridsNotificationWebServiceConfig extends WsConfigurerAdapter {

    private static final String ORGANISATION_IDENTIFICATION_HEADER = "OrganisationIdentification";

    @Autowired
    private PlatformMicrogridsConfiguration configuration;

    @Bean
    public DefaultMethodEndpointAdapter defaultMethodEndpointAdapter() {
        final DefaultMethodEndpointAdapter defaultMethodEndpointAdapter = new DefaultMethodEndpointAdapter();

        final List<MethodArgumentResolver> methodArgumentResolvers = new ArrayList<>();
        methodArgumentResolvers.add(this.microgridsNotificationMarshallingPayloadMethodProcessor());
        methodArgumentResolvers.add(new AnnotationMethodArgumentResolver(ORGANISATION_IDENTIFICATION_HEADER,
                OrganisationIdentification.class));
        defaultMethodEndpointAdapter.setMethodArgumentResolvers(methodArgumentResolvers);

        final List<MethodReturnValueHandler> methodReturnValueHandlers = new ArrayList<>();
        methodReturnValueHandlers.add(this.microgridsNotificationMarshallingPayloadMethodProcessor());
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
        final List<EndpointMapping> mappings = new ArrayList<>();
        mappings.add(mapping);
        soapMessageDispatcher.setEndpointMappings(mappings);

        final List<EndpointAdapter> adapters = new ArrayList<>();
        adapters.add(defaultMethodEndpointAdapter);
        soapMessageDispatcher.setEndpointAdapters(adapters);

        final WebServiceMessageReceiverHttpHandler wsmrhh = new WebServiceMessageReceiverHttpHandler();
        wsmrhh.setMessageReceiver(soapMessageDispatcher);
        wsmrhh.setMessageFactory(messageFactory);

        final Map<String, HttpHandler> contexts = new HashMap<>();
        contexts.put(this.configuration.getNotificationsContextPath(), wsmrhh);

        final SimpleHttpServerFactoryBean httpServer = new SimpleHttpServerFactoryBean();
        httpServer.setContexts(contexts);
        httpServer.setPort(this.configuration.getNotificationsPort());

        return httpServer;
    }

    /**
     * Method for creating the Marshaller for Microgrids notification.
     *
     * @return Jaxb2Marshaller
     */
    @Bean
    public Jaxb2Marshaller microgridsNotificationMarshaller() {
        final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        marshaller.setContextPath(this.configuration.getContextPathMicrogridsNotification());

        return marshaller;
    }

    /**
     * Method for creating the Marshalling Payload Method Processor for
     * Microgrids notification.
     *
     * @return MarshallingPayloadMethodProcessor
     */
    @Bean
    public MarshallingPayloadMethodProcessor microgridsNotificationMarshallingPayloadMethodProcessor() {
        return new MarshallingPayloadMethodProcessor(this.microgridsNotificationMarshaller(),
                this.microgridsNotificationMarshaller());
    }
}
