/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.config.messaging;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.alliander.osgp.adapter.domain.admin.infra.jms.OsgpCoreRequestMessageListener;
import com.alliander.osgp.adapter.domain.admin.infra.jms.core.OsgpCoreResponseMessageListener;
import com.alliander.osgp.adapter.domain.admin.infra.jms.ws.WebServiceRequestMessageListener;

public class DomainAdminMessagingConfig {

    // JMS SETTINGS: INCOMING WEB SERVICE REQUESTS ===

    @Bean(name = "domainAdminIncomingWebServiceRequestsQueue")
    public ActiveMQDestination incomingWebServiceRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.DOMAIN_ADMIN_1_0__WS_ADMIN_1_0__REQUESTS_QUEUE);
    }

    @Bean(name = "domainAdminIncomingWebServiceRequestsMessageListenerContainer")
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

    @Autowired
    @Qualifier("domainAdminIncomingWebServiceRequestMessageListener")
    private WebServiceRequestMessageListener incomingWebServiceRequestMessageListener;

    // JMS SETTINGS: OUTGOING WEB SERVICE RESPONSES

    @Bean(name = "domainAdminOutgoingWebServiceResponsesJmsTemplate")
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

    @Bean(name = "domainAdminOutgoingWebServiceResponsesQueue")
    public ActiveMQDestination outgoingWebServiceResponsesQueue() {
        return new ActiveMQQueue(MessagingConfig.WS_ADMIN_1_0__DOMAIN_ADMIN_1_0__RESPONSES_QUEUE);
    }

    // JMS SETTINGS: OUTGOING OSGP CORE REQUESTS (Sending requests to osgp core)

    @Bean(name = "domainAdminOutgoingOsgpCoreRequestsJmsTemplate")
    public JmsTemplate outgoingOsgpCoreRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.outgoingOsgpCoreRequestsQueue());
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean(name = "domainAdminOutgoingOsgpCoreRequestsQueue")
    public ActiveMQDestination outgoingOsgpCoreRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.OSGP_CORE_1_0__DOMAIN_ADMIN_1_0__REQUESTS_QUEUE);
    }

    // JMS SETTINGS: INCOMING OSGP CORE RESPONSES (receiving responses from osgp core)

    @Bean(name = "domainAdminIncomingOsgpCoreResponsesQueue")
    public ActiveMQDestination incomingOsgpCoreResponsesQueue() {
        return new ActiveMQQueue(MessagingConfig.DOMAIN_ADMIN_1_0__OSGP_CORE_1_0__RESPONSES_QUEUE);
    }

    @Bean(name = "domainAdminIncomingOsgpCoreResponsesMessageListenerContainer")
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

    @Autowired
    @Qualifier("domainAdminIncomingOsgpCoreResponseMessageListener")
    private OsgpCoreResponseMessageListener incomingOsgpCoreResponseMessageListener;

    // JMS SETTINGS: INCOMING OSGP CORE REQUESTS (receiving requests from osgp core) 

    @Bean(name = "domainAdminIncomingOsgpCoreRequestsQueue")
    public ActiveMQDestination incomingOsgpCoreRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.DOMAIN_ADMIN_1_0__OSGP_CORE_1_0__REQUESTS_QUEUE);
    }

    @Bean(name = "domainAdminIncomingOsgpCoreRequestsMessageListenerContainer")
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

    @Autowired
    @Qualifier("domainAdminIncomingOsgpCoreRequestMessageListener")
    private OsgpCoreRequestMessageListener incomingOsgpCoreRequestMessageListener;

    // JMS SETTINGS: OUTGOING OSGP CORE RESPONSES (sending responses to osgp core)

    @Bean(name = "domainAdminOutgoingOsgpCoreResponsesJmsTemplate")
    public JmsTemplate outgoingOsgpCoreResponsesJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.outgoingOsgpCoreResponsesQueue());
        jmsTemplate.setExplicitQosEnabled(MessagingConfig.EXPLICIT_QOS_ENABLED);
        jmsTemplate.setTimeToLive(MessagingConfig.TIME_TO_LIVE);
        jmsTemplate.setDeliveryPersistent(MessagingConfig.DELIVERY_PERSISTENT);
        jmsTemplate.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean(name = "domainAdminOutgoingOsgpCoreResponsesQueue")
    public ActiveMQDestination outgoingOsgpCoreResponsesQueue() {
        return new ActiveMQQueue(MessagingConfig.OSGP_CORE_1_0__DOMAIN_ADMIN_1_0__RESPONSES_QUEUE);
    }
}
