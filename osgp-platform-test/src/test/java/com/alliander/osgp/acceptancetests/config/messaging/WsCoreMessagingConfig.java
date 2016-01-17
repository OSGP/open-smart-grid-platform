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

import com.alliander.osgp.adapter.ws.core.infra.jms.CommonRequestMessageSender;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonResponseMessageFinder;

public class WsCoreMessagingConfig {
    // === JMS SETTINGS: COMMON REQUESTS ===

    @Bean(name = "wsCoreOutgoingRequestsJmsTemplate")
    public JmsTemplate commonRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.commonRequestsQueue());
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean(name = "wsCoreOutgoingRequestsQueue")
    public ActiveMQDestination commonRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.DOMAIN_CORE_1_0__WS_CORE_1_0__REQUESTS_QUEUE);
    }

    @Bean(name = "wsCoreOutgoingRequestsMessageSender")
    public CommonRequestMessageSender commonRequestMessageSender() {
        return new CommonRequestMessageSender();
    }

    // === JMS SETTINGS: COMMON RESPONSES ===
    @Bean(name = "wsCoreIncomingResponsesJmsTemplate")
    public JmsTemplate commonResponsesJmsTemplate() {
        return mock(JmsTemplate.class);
    }

    @Bean(name = "wsCoreIncomingResponsesQueue")
    public ActiveMQDestination commonResponsesQueue() {
        return new ActiveMQQueue(MessagingConfig.WS_CORE_1_0__DOMAIN_CORE_1_0__RESPONSES_QUEUE);
    }

    @Bean(name = "wsCoreIncomingResponsesMessageFinder")
    public CommonResponseMessageFinder commonResponseMessageFinder() {
        return new CommonResponseMessageFinder();
    }
}
