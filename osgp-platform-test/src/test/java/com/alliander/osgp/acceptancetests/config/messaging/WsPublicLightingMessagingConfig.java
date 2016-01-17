/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.config.messaging;

import static org.mockito.Mockito.mock;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;

import com.alliander.osgp.adapter.ws.publiclighting.infra.jms.PublicLightingRequestMessageSender;
import com.alliander.osgp.adapter.ws.publiclighting.infra.jms.PublicLightingResponseMessageFinder;

public class WsPublicLightingMessagingConfig {

    // === JMS SETTINGS: PUBLIC LIGHTING REQUESTS ===

    @Bean(name = "wsPublicLightingOutgoingRequestsQueue")
    public ActiveMQDestination publicLightingRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.DOMAIN_PUBLICLIGHTING_1_0__WS_PUBLICLIGHTING_1_0__REQUESTS_QUEUE);
    }

    @Bean(name = "wsPublicLightingOutgoingRequestsJmsTemplate")
    public JmsTemplate publicLightingRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.publicLightingRequestsQueue());
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean(name = "wsPublicLightingOutgoingRequestsMessageSender")
    public PublicLightingRequestMessageSender publicLightingRequestMessageSender() {
        return new PublicLightingRequestMessageSender();
    }

    // === JMS SETTINGS: PUBLIC LIGHTING RESPONSES ===

    @Bean(name = "wsPublicLightingIncomingResponsesJmsTemplate")
    public JmsTemplate publicLightingResponsesJmsTemplate() {
        return mock(JmsTemplate.class);
    }

    @Bean(name = "wsPublicLightingIncomingResponsesQueue")
    public ActiveMQDestination publicLightingResponsesQueue() {
        return new ActiveMQQueue(MessagingConfig.WS_PUBLICLIGHTING_1_0__DOMAIN_PUBLICLIGHTING_1_0__RESPONSES_QUEUE);
    }

    @Bean(name = "wsPublicLightingIncomingResponsesMessageFinder")
    public PublicLightingResponseMessageFinder publicLightingResponseMessageFinder() {
        return new PublicLightingResponseMessageFinder();
    }
}
