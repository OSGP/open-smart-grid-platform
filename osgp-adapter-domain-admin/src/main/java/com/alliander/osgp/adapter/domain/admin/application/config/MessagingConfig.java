/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.admin.application.config;

import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.alliander.osgp.adapter.domain.admin.infra.jms.OsgpCoreRequestMessageListener;
import com.alliander.osgp.adapter.domain.admin.infra.jms.core.OsgpCoreResponseMessageListener;
import com.alliander.osgp.adapter.domain.admin.infra.jms.ws.WebServiceRequestMessageListener;
import com.alliander.osgp.shared.application.config.AbstractConfig;

/**
 * An application context Java configuration class.
 */
@Configuration
@PropertySources({ 
	@PropertySource("classpath:osgp-adapter-domain-admin.properties"),
	@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
    @PropertySource(value = "file:${osgp/AdapterDomainAdmin/config}", ignoreResourceNotFound = true),
})
public class MessagingConfig extends AbstractConfig {

    // JMS Settings
    private static final String PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL = "jms.activemq.broker.url";

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.default.redelivery.delay";

    // JMS Settings: incoming web service requests
    private static final String PROPERTY_NAME_JMS_INCOMING_WS_REQUESTS_QUEUE = "jms.incoming.ws.requests.queue";

    private static final String PROPERTY_NAME_JMS_INCOMING_WS_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.incoming.ws.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_INCOMING_WS_REQUESTS_MAXIMUM_REDELIVERIES = "jms.incoming.ws.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_INCOMING_WS_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.incoming.ws.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_INCOMING_WS_REQUESTS_REDELIVERY_DELAY = "jms.incoming.ws.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_INCOMING_WS_REQUESTS_BACK_OFF_MULTIPLIER = "jms.incoming.ws.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_INCOMING_WS_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.incoming.ws.requests.use.exponential.back.off";
    private static final String PROPERTY_NAME_JMS_INCOMING_WS_REQUESTS_CONCURRENT_CONSUMERS = "jms.incoming.ws.requests.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_INCOMING_WS_REQUESTS_MAX_CONCURRENT_CONSUMERS = "jms.incoming.ws.requests.max.concurrent.consumers";

    // JMS Settings: outgoing web service responses
    private static final String PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_QUEUE = "jms.outgoing.ws.responses.queue";
    private static final String PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_EXPLICIT_QOS_ENABLED = "jms.outgoing.ws.responses.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_DELIVERY_PERSISTENT = "jms.outgoing.ws.responses.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_TIME_TO_LIVE = "jms.outgoing.ws.responses.time.to.live";
    private static final String PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_RECEIVE_TIMEOUT = "jms.outgoing.ws.responses.receive.timeout";

    private static final String PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_INITIAL_REDELIVERY_DELAY = "jms.outgoing.ws.responses.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_MAXIMUM_REDELIVERIES = "jms.outgoing.ws.responses.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_MAXIMUM_REDELIVERY_DELAY = "jms.outgoing.ws.responses.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_REDELIVERY_DELAY = "jms.outgoing.ws.responses.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_BACK_OFF_MULTIPLIER = "jms.outgoing.ws.responses.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_USE_EXPONENTIAL_BACK_OFF = "jms.outgoing.ws.responses.use.exponential.back.off";

    // JMS Settings: outgoing osgp core requests
    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_QUEUE = "jms.outgoing.osgp.core.requests.queue";
    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_EXPLICIT_QOS_ENABLED = "jms.outgoing.osgp.core.requests.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_DELIVERY_PERSISTENT = "jms.outgoing.osgp.core.requests.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_TIME_TO_LIVE = "jms.outgoing.osgp.core.requests.time.to.live";

    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.outgoing.osgp.core.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_MAXIMUM_REDELIVERIES = "jms.outgoing.osgp.core.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.outgoing.osgp.core.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_REDELIVERY_DELAY = "jms.outgoing.osgp.core.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_BACK_OFF_MULTIPLIER = "jms.outgoing.osgp.core.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.outgoing.osgp.core.requests.use.exponential.back.off";

    // JMS Settings: incoming osgp core responses
    private static final String PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_RESPONSES_QUEUE = "jms.incoming.osgp.core.responses.queue";

    private static final String PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_RESPONSES_INITIAL_REDELIVERY_DELAY = "jms.incoming.osgp.core.responses.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_RESPONSES_MAXIMUM_REDELIVERIES = "jms.incoming.osgp.core.responses.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_RESPONSES_MAXIMUM_REDELIVERY_DELAY = "jms.incoming.osgp.core.responses.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_RESPONSES_REDELIVERY_DELAY = "jms.incoming.osgp.core.responses.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_RESPONSES_BACK_OFF_MULTIPLIER = "jms.incoming.osgp.core.responses.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_RESPONSES_USE_EXPONENTIAL_BACK_OFF = "jms.incoming.osgp.core.responses.use.exponential.back.off";
    private static final String PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_RESPONSES_CONCURRENT_CONSUMERS = "jms.incoming.osgp.core.responses.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_RESPONSES_MAX_CONCURRENT_CONSUMERS = "jms.incoming.osgp.core.responses.max.concurrent.consumers";

    // JSM Settings: incoming osgp core requests
    private static final String PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_REQUESTS_QUEUE = "jms.incoming.osgp.core.requests.queue";

    private static final String PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.incoming.osgp.core.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_REQUESTS_MAXIMUM_REDELIVERIES = "jms.incoming.osgp.core.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.incoming.osgp.core.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_REQUESTS_REDELIVERY_DELAY = "jms.incoming.osgp.core.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_REQUESTS_BACK_OFF_MULTIPLIER = "jms.incoming.osgp.core.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.incoming.osgp.core.requests.use.exponential.back.off";
    private static final String PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_REQUESTS_CONCURRENT_CONSUMERS = "jms.incoming.osgp.core.requests.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_REQUESTS_MAX_CONCURRENT_CONSUMERS = "jms.incoming.osgp.core.requests.max.concurrent.consumers";

    // JMS Settings: outgoing osgp core responses
    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_QUEUE = "jms.outgoing.osgp.core.responses.queue";
    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_EXPLICIT_QOS_ENABLED = "jms.outgoing.osgp.core.responses.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_DELIVERY_PERSISTENT = "jms.outgoing.osgp.core.responses.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_TIME_TO_LIVE = "jms.outgoing.osgp.core.responses.time.to.live";

    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_INITIAL_REDELIVERY_DELAY = "jms.outgoing.osgp.core.responses.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_MAXIMUM_REDELIVERIES = "jms.outgoing.osgp.core.responses.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_MAXIMUM_REDELIVERY_DELAY = "jms.outgoing.osgp.core.responses.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_REDELIVERY_DELAY = "jms.outgoing.osgp.core.responses.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_BACK_OFF_MULTIPLIER = "jms.outgoing.osgp.core.responses.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_USE_EXPONENTIAL_BACK_OFF = "jms.outgoing.osgp.core.responses.use.exponential.back.off";

    @Autowired
    @Qualifier("domainAdminIncomingWebServiceRequestMessageListener")
    private WebServiceRequestMessageListener incomingWebServiceRequestMessageListener;

    @Autowired
    @Qualifier("domainAdminIncomingOsgpCoreResponseMessageListener")
    private OsgpCoreResponseMessageListener incomingOsgpCoreResponseMessageListener;

    @Autowired
    @Qualifier("domainAdminIncomingOsgpCoreRequestMessageListener")
    private OsgpCoreRequestMessageListener incomingOsgpCoreRequestMessageListener;

    // === JMS SETTINGS ===

    @Bean(destroyMethod = "stop")
    public PooledConnectionFactory pooledConnectionFactory() {
        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(this.connectionFactory());
        return pooledConnectionFactory;
    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setRedeliveryPolicyMap(this.redeliveryPolicyMap());
        activeMQConnectionFactory.setBrokerURL(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL));

        activeMQConnectionFactory.setNonBlockingRedelivery(true);

        return activeMQConnectionFactory;
    }

    @Bean
    public RedeliveryPolicyMap redeliveryPolicyMap() {
        final RedeliveryPolicyMap redeliveryPolicyMap = new RedeliveryPolicyMap();
        redeliveryPolicyMap.setDefaultEntry(this.defaultRedeliveryPolicy());
        redeliveryPolicyMap.put(this.incomingWebServiceRequestsQueue(),
                this.incomingWebServiceRequestsRedeliveryPolicy());
        redeliveryPolicyMap.put(this.outgoingWebServiceResponsesQueue(),
                this.outgoingWebServiceResponsesRedeliveryPolicy());
        redeliveryPolicyMap.put(this.outgoingOsgpCoreRequestsQueue(), this.outgoingOsgpCoreRequestsRedeliveryPolicy());
        redeliveryPolicyMap
        .put(this.incomingOsgpCoreResponsesQueue(), this.incomingOsgpCoreResponsesRedeliveryPolicy());
        redeliveryPolicyMap.put(this.incomingOsgpCoreRequestsQueue(), this.incomingOsgpCoreRequestsRedeliveryPolicy());
        redeliveryPolicyMap
        .put(this.outgoingOsgpCoreResponsesQueue(), this.outgoingOsgpCoreResponsesRedeliveryPolicy());
        return redeliveryPolicyMap;
    }

    @Bean
    public RedeliveryPolicy defaultRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY)));

        return redeliveryPolicy;
    }

    // JMS SETTINGS: INCOMING WEB SERVICE REQUESTS ===

    @Bean(name = "domainAdminIncomingWebServiceRequestsQueue")
    public ActiveMQDestination incomingWebServiceRequestsQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_WS_REQUESTS_QUEUE));
    }

    @Bean(name = "domainAdminIncomingWebServiceRequestsRedeliveryPolicy")
    public RedeliveryPolicy incomingWebServiceRequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_WS_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_WS_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_WS_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_WS_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.incomingWebServiceRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_WS_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_WS_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean(name = "domainAdminIncomingWebServiceRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer incomingWebServiceRequestsMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(this.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.incomingWebServiceRequestsQueue());
        messageListenerContainer.setConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_WS_REQUESTS_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_WS_REQUESTS_MAX_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMessageListener(this.incomingWebServiceRequestMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    // JMS SETTINGS: OUTGOING WEB SERVICE RESPONSES

    @Bean(name = "domainAdminOutgoingWebServiceResponsesJmsTemplate")
    public JmsTemplate outgoingWebServiceResponsesJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.outgoingWebServiceResponsesQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_RECEIVE_TIMEOUT)));
        return jmsTemplate;
    }

    @Bean(name = "domainAdminOutgoingWebServiceResponsesQueue")
    public ActiveMQDestination outgoingWebServiceResponsesQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_QUEUE));
    }

    @Bean(name = "domainAdminOutgoingWebServiceResponsesRedeliveryPolicy")
    public RedeliveryPolicy outgoingWebServiceResponsesRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.outgoingWebServiceResponsesQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_WS_RESPONSES_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    // JMS SETTINGS: OUTGOING OSGP CORE REQUESTS (Sending requests to osgp core)

    @Bean(name = "domainAdminOutgoingOsgpCoreRequestsJmsTemplate")
    public JmsTemplate outgoingOsgpCoreRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.outgoingOsgpCoreRequestsQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean(name = "domainAdminOutgoingOsgpCoreRequestsQueue")
    public ActiveMQDestination outgoingOsgpCoreRequestsQueue() {
        return new ActiveMQQueue(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_QUEUE));
    }

    @Bean(name = "domainAdminOutgoingOsgpCoreRequestsRedeliveryPolicy")
    public RedeliveryPolicy outgoingOsgpCoreRequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.outgoingOsgpCoreRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    // JMS SETTINGS: INCOMING OSGP CORE RESPONSES (receiving responses from osgp
    // core)

    @Bean(name = "domainAdminIncomingOsgpCoreResponsesQueue")
    public ActiveMQDestination incomingOsgpCoreResponsesQueue() {
        return new ActiveMQQueue(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_RESPONSES_QUEUE));
    }

    @Bean(name = "domainAdminIncomingOsgpCoreResponsesRedeliveryPolicy")
    public RedeliveryPolicy incomingOsgpCoreResponsesRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_RESPONSES_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_RESPONSES_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_RESPONSES_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_RESPONSES_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.incomingOsgpCoreResponsesQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_RESPONSES_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_RESPONSES_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean(name = "domainAdminIncomingOsgpCoreResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer incomingOsgpCoreResponsesMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(this.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.incomingOsgpCoreResponsesQueue());
        messageListenerContainer.setConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_RESPONSES_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_RESPONSES_MAX_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMessageListener(this.incomingOsgpCoreResponseMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    // JMS SETTINGS: INCOMING OSGP CORE REQUESTS (receiving requests from osgp
    // core)

    @Bean(name = "domainAdminIncomingOsgpCoreRequestsQueue")
    public ActiveMQDestination incomingOsgpCoreRequestsQueue() {
        return new ActiveMQQueue(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_REQUESTS_QUEUE));
    }

    @Bean(name = "domainAdminIncomingOsgpCoreRequestsRedeliveryPolicy")
    public RedeliveryPolicy incomingOsgpCoreRequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.incomingOsgpCoreRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean(name = "domainAdminIncomingOsgpCoreRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer incomingOsgpCoreRequestsMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(this.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.incomingOsgpCoreRequestsQueue());
        messageListenerContainer.setConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_REQUESTS_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_OSGP_CORE_REQUESTS_MAX_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMessageListener(this.incomingOsgpCoreRequestMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    // JMS SETTINGS: OUTGOING OSGP CORE RESPONSES (sending responses to osgp
    // core)

    @Bean(name = "domainAdminOutgoingOsgpCoreResponsesJmsTemplate")
    public JmsTemplate outgoingOsgpCoreResponsesJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.outgoingOsgpCoreResponsesQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean(name = "domainAdminOutgoingOsgpCoreResponsesQueue")
    public ActiveMQDestination outgoingOsgpCoreResponsesQueue() {
        return new ActiveMQQueue(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_QUEUE));
    }

    @Bean(name = "domainAdminOutgoingOsgpCoreResponsesRedeliveryPolicy")
    public RedeliveryPolicy outgoingOsgpCoreResponsesRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.outgoingOsgpCoreResponsesQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_OSGP_CORE_RESPONSES_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }
}
