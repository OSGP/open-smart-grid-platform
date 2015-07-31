/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * @author OSGP
 *
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    };

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.web.socket.config.annotation.
     * WebSocketMessageBrokerConfigurer
     * #registerStompEndpoints(org.springframework
     * .web.socket.config.annotation.StompEndpointRegistry)
     */
    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint("/hello").withSockJS();
    }

    // @Bean
    // public SockJsClient sockJsClient() {
    // final List<Transport> transports = new ArrayList<>();
    // final RestTemplateXhrTransport restTemplateXhrTransport = new
    // RestTemplateXhrTransport();
    // transports.add(restTemplateXhrTransport);
    //
    // final SockJsClient sockJsClient = new SockJsClient(transports);
    // sockJsClient.doHandshake(?????, "ws://localhost:8080/app", "");
    //
    // sockJsClient.start();
    //
    // return sockJsClient;
    // }

}
