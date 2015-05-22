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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.alliander.osgp.adapter.domain.publiclighting.infra.jms.OsgpCoreRequestMessageListener;
//import com.alliander.osgp.adapter.domain.publiclighting.infra.jms.OsgpCoreRequestMessageProcessor;
//import com.alliander.osgp.adapter.domain.publiclighting.infra.jms.OsgpCoreRequestMessageSender;
import com.alliander.osgp.adapter.domain.publiclighting.infra.jms.core.OsgpCoreResponseMessageListener;
//import com.alliander.osgp.adapter.domain.publiclighting.infra.jms.OsgpCoreResponseMessageProcessor;
//import com.alliander.osgp.adapter.domain.publiclighting.infra.jms.OsgpCoreResponseMessageSender;
import com.alliander.osgp.adapter.domain.publiclighting.infra.jms.ws.WebServiceRequestMessageListener;
import com.alliander.osgp.adapter.domain.publiclighting.infra.jms.ws.WebServiceResponseMessageSender;

//import com.alliander.osgp.adapter.domain.publiclighting.infra.jms.WebServiceRequestMessageProcessor;

public class DomainPublicLightingMessagingConfig {

    // JMS SETTINGS: INCOMING WEB SERVICE REQUESTS ===

    @Bean(name = "domainPublicLightingIncomingWebServiceRequestsQueue")
    public ActiveMQDestination incomingWebServiceRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.DOMAIN_PUBLICLIGHTING_1_0__WS_PUBLICLIGHTING_1_0__REQUESTS_QUEUE);
    }

    @Bean(name = "domainPublicLightingIncomingWebServiceRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer incomingWebServiceRequestsMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.incomingWebServiceRequestsQueue());
        messageListenerContainer.setConcurrentConsumers(MessagingConfig.CONCURRENT_CONSUMERS);
        messageListenerContainer.setMaxConcurrentConsumers(MessagingConfig.MAX_CONCURRENT_CONSUMERS);
        messageListenerContainer.setMessageListener(this.incomingWebServiceRequestMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    // @Bean(name =
    // "domainPublicLightingIncomingWebServiceRequestMessageListener")
    // public WebServiceRequestMessageListener
    // incomingWebServiceRequestMessageListener() {
    // // return new
    // WebServiceRequestMessageListener(this.incomingWebServiceRequestMessageProcessor());
    // return new WebServiceRequestMessageListener();
    // }

    @Autowired
    @Qualifier("domainPublicLightingIncomingWebServiceRequestMessageListener")
    private WebServiceRequestMessageListener incomingWebServiceRequestMessageListener;

    // @Bean(name =
    // "domainPublicLightingIncomingWebServiceRequestMessageProcessor")
    // public WebServiceRequestMessageProcessor
    // incomingWebServiceRequestMessageProcessor() {
    // return new WebServiceRequestMessageProcessor();
    // }

    // @Autowired
    // private WebServiceRequestMessageProcessor
    // incomingWebServiceRequestMessageProcessor;

    // JMS SETTINGS: OUTGOING WEB SERVICE RESPONSES

    @Bean(name = "domainPublicLightingOutgoingWebServiceResponsesJmsTemplate")
    public JmsTemplate outgoingWebServiceResponsesJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.outgoingWebServiceResponsesQueue());
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(MessagingConfig.RECEIVE_TIMEOUT);
        return jmsTemplate;
    }

    @Bean(name = "domainPublicLightingOutgoingWebServiceResponsesQueue")
    public ActiveMQDestination outgoingWebServiceResponsesQueue() {
        return new ActiveMQQueue(MessagingConfig.WS_PUBLICLIGHTING_1_0__DOMAIN_PUBLICLIGHTING_1_0__RESPONSES_QUEUE);
    }

    @Bean(name = "domainPublicLightingOutgoingWebServiceResponseMessageSender")
    public WebServiceResponseMessageSender webServiceResponseMessageSenderMock() {
        return mock(WebServiceResponseMessageSender.class);
    }

    // JMS SETTINGS: OUTGOING OSGP CORE REQUESTS (Sending requests to osgp core)

    @Bean(name = "domainPublicLightingOutgoingOsgpCoreRequestsJmsTemplate")
    public JmsTemplate outgoingOsgpCoreRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.outgoingOsgpCoreRequestsQueue());
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean(name = "domainPublicLightingOutgoingOsgpCoreRequestsQueue")
    public ActiveMQDestination outgoingOsgpCoreRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.OSGP_CORE_1_0__DOMAIN_PUBLICLIGHTING_1_0__REQUESTS_QUEUE);
    }

    // @Bean
    // public OsgpCoreRequestMessageSender
    // outgoingOsgpCoreRequestMessageSender() {
    // return new OsgpCoreRequestMessageSender();
    // }

    // JMS SETTINGS: INCOMING OSGP CORE RESPONSES (receiving responses from osgp
    // core)

    @Bean(name = "domainPublicLightingIncomingOsgpCoreResponsesQueue")
    public ActiveMQDestination incomingOsgpCoreResponsesQueue() {
        return new ActiveMQQueue(MessagingConfig.DOMAIN_PUBLICLIGHTING_1_0__OSGP_CORE_1_0__RESPONSES_QUEUE);
    }

    @Bean(name = "domainPublicLightingIncomingOsgpCoreResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer incomingOsgpCoreResponsesMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.incomingOsgpCoreResponsesQueue());
        messageListenerContainer.setConcurrentConsumers(MessagingConfig.CONCURRENT_CONSUMERS);
        messageListenerContainer.setMaxConcurrentConsumers(MessagingConfig.MAX_CONCURRENT_CONSUMERS);
        messageListenerContainer.setMessageListener(this.incomingOsgpCoreResponseMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    // @Bean(name =
    // "domainPublicLightingIncomingOsgpCoreResponseMessageListener")
    // public OsgpCoreResponseMessageListener
    // incomingOsgpCoreResponseMessageListener() {
    // return new OsgpCoreResponseMessageListener();
    // }

    @Autowired
    @Qualifier("domainPublicLightingIncomingOsgpCoreResponseMessageListener")
    private OsgpCoreResponseMessageListener incomingOsgpCoreResponseMessageListener;

    // @Bean
    // public OsgpCoreResponseMessageProcessor
    // incomingOsgpCoreResponseMessageProcessor() {
    // return new OsgpCoreResponseMessageProcessor();
    // }

    // JMS SETTINGS: INCOMING OSGP CORE REQUESTS (receiving requests from osgp
    // core)

    @Bean(name = "domainPublicLightingIncomingOsgpCoreRequestsQueue")
    public ActiveMQDestination incomingOsgpCoreRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.DOMAIN_PUBLICLIGHTING_1_0__OSGP_CORE_1_0__REQUESTS_QUEUE);
    }

    @Bean(name = "domainPublicLightingIncomingOsgpCoreRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer incomingOsgpCoreRequestsMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.incomingOsgpCoreRequestsQueue());
        messageListenerContainer.setConcurrentConsumers(MessagingConfig.CONCURRENT_CONSUMERS);
        messageListenerContainer.setMaxConcurrentConsumers(MessagingConfig.MAX_CONCURRENT_CONSUMERS);
        messageListenerContainer.setMessageListener(this.incomingOsgpCoreRequestMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    // @Bean(name =
    // "domainPublicLightingIncomingOsgpCoreRequestMessageListener")
    // public OsgpCoreRequestMessageListener
    // incomingOsgpCoreRequestMessageListener() {
    // return new OsgpCoreRequestMessageListener();
    // }

    @Autowired
    @Qualifier("domainPublicLightingIncomingOsgpCoreRequestMessageListener")
    private OsgpCoreRequestMessageListener incomingOsgpCoreRequestMessageListener;

    // @Bean
    // public OsgpCoreRequestMessageProcessor
    // incomingOsgpCoreRequestMessageProcessor() {
    // return new OsgpCoreRequestMessageProcessor();
    // }

    // JMS SETTINGS: OUTGOING OSGP CORE RESPONSES (sending responses to osgp
    // core)

    @Bean(name = "domainPublicLightingOutgoingOsgpCoreResponsesJmsTemplate")
    public JmsTemplate outgoingOsgpCoreResponsesJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.outgoingOsgpCoreResponsesQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean(name = "domainPublicLightingOutgoingOsgpCoreResponsesQueue")
    public ActiveMQDestination outgoingOsgpCoreResponsesQueue() {
        return new ActiveMQQueue(MessagingConfig.OSGP_CORE_1_0__DOMAIN_PUBLICLIGHTING_1_0__RESPONSES_QUEUE);
    }

    // @Bean
    // public OsgpCoreResponseMessageSender
    // outgoingOsgpCoreResponseMessageSender() {
    // return new OsgpCoreResponseMessageSender();
    // }

    @Bean
    public Long getPowerUsageHistoryResponseTimeToLive() {
        return 3600000L;
    }
}
