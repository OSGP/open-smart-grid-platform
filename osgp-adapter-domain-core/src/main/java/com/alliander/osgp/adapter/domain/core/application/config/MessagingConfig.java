/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.core.application.config;

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

import com.alliander.osgp.adapter.domain.core.infra.jms.OsgpCoreRequestMessageListener;
import com.alliander.osgp.adapter.domain.core.infra.jms.core.OsgpCoreResponseMessageListener;
import com.alliander.osgp.adapter.domain.core.infra.jms.ws.WebServiceRequestMessageListener;
import com.alliander.osgp.adapter.domain.core.infra.jms.ws.WebServiceResponseMessageSender;
import com.alliander.osgp.shared.application.config.AbstractConfig;

@Configuration
@PropertySources({
	@PropertySource("classpath:osgp-adapter-domain-core.properties"),
	@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
    @PropertySource(value = "file:${osgp/AdapterDomainCore/config}", ignoreResourceNotFound = true),
})
public class MessagingConfig extends AbstractConfig {
    // JMS Settings
    private static final String PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL = "jms.activemq.broker.url";

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.default.redelivery.delay";

    // JMS Settings: Common web service requests (receive)
    private static final String PROPERTY_NAME_JMS_COMMON_WS_REQUESTS_QUEUE = "jms.common.ws.requests.queue";

    private static final String PROPERTY_NAME_JMS_COMMON_WS_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.common.ws.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_COMMON_WS_REQUESTS_MAXIMUM_REDELIVERIES = "jms.common.ws.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_COMMON_WS_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.common.ws.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_COMMON_WS_REQUESTS_REDELIVERY_DELAY = "jms.common.ws.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_COMMON_WS_REQUESTS_BACK_OFF_MULTIPLIER = "jms.common.ws.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_COMMON_WS_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.common.ws.requests.use.exponential.back.off";
    private static final String PROPERTY_NAME_JMS_COMMON_WS_REQUESTS_CONCURRENT_CONSUMERS = "jms.common.ws.requests.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_COMMON_WS_REQUESTS_MAX_CONCURRENT_CONSUMERS = "jms.common.ws.requests.max.concurrent.consumers";

    // JMS Settings: Common web service responses (send)
    private static final String PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_QUEUE = "jms.common.ws.responses.queue";
    private static final String PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_EXPLICIT_QOS_ENABLED = "jms.common.ws.responses.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_DELIVERY_PERSISTENT = "jms.common.ws.responses.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_TIME_TO_LIVE = "jms.common.ws.responses.time.to.live";
    private static final String PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_RECEIVE_TIMEOUT = "jms.common.ws.responses.receive.timeout";

    private static final String PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_INITIAL_REDELIVERY_DELAY = "jms.common.ws.responses.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_MAXIMUM_REDELIVERIES = "jms.common.ws.responses.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_MAXIMUM_REDELIVERY_DELAY = "jms.common.ws.responses.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_REDELIVERY_DELAY = "jms.common.ws.responses.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_BACK_OFF_MULTIPLIER = "jms.common.ws.responses.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_USE_EXPONENTIAL_BACK_OFF = "jms.common.ws.responses.use.exponential.back.off";

    // JMS Settings: OSGP domain core requests (send)
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_QUEUE = "jms.osgp.core.requests.queue";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_EXPLICIT_QOS_ENABLED = "jms.osgp.core.requests.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_DELIVERY_PERSISTENT = "jms.osgp.core.requests.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_TIME_TO_LIVE = "jms.osgp.core.requests.time.to.live";

    private static final String PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.osgp.core.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_MAXIMUM_REDELIVERIES = "jms.osgp.core.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.osgp.core.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_REDELIVERY_DELAY = "jms.osgp.core.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_BACK_OFF_MULTIPLIER = "jms.osgp.core.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.osgp.core.requests.use.exponential.back.off";

    // JMS Settings: OSGP domain core responses (receive)
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_QUEUE = "jms.osgp.core.responses.queue";

    private static final String PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INITIAL_REDELIVERY_DELAY = "jms.osgp.core.responses.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_MAXIMUM_REDELIVERIES = "jms.osgp.core.responses.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_MAXIMUM_REDELIVERY_DELAY = "jms.osgp.core.responses.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_REDELIVERY_DELAY = "jms.osgp.core.responses.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_BACK_OFF_MULTIPLIER = "jms.osgp.core.responses.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_USE_EXPONENTIAL_BACK_OFF = "jms.osgp.core.responses.use.exponential.back.off";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_CONCURRENT_CONSUMERS = "jms.osgp.core.responses.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_MAX_CONCURRENT_CONSUMERS = "jms.osgp.core.responses.max.concurrent.consumers";

    // JSM Settings: OSGP domain core incoming requests (receive)
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INCOMING_QUEUE = "jms.osgp.core.requests.incoming.queue";

    private static final String PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INCOMING_INITIAL_REDELIVERY_DELAY = "jms.osgp.core.requests.incoming.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INCOMING_MAXIMUM_REDELIVERIES = "jms.osgp.core.requests.incoming.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INCOMING_MAXIMUM_REDELIVERY_DELAY = "jms.osgp.core.requests.incoming.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INCOMING_REDELIVERY_DELAY = "jms.osgp.core.requests.incoming.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INCOMING_BACK_OFF_MULTIPLIER = "jms.osgp.core.requests.incoming.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INCOMING_USE_EXPONENTIAL_BACK_OFF = "jms.osgp.core.requests.incoming.use.exponential.back.off";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INCOMING_CONCURRENT_CONSUMERS = "jms.osgp.core.requests.incoming.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INCOMING_MAX_CONCURRENT_CONSUMERS = "jms.osgp.core.requests.incoming.max.concurrent.consumers";

    // JMS Settings: OSGP domain core incoming responses (send)
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_QUEUE = "jms.osgp.core.responses.incoming.queue";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_EXPLICIT_QOS_ENABLED = "jms.osgp.core.responses.incoming.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_DELIVERY_PERSISTENT = "jms.osgp.core.responses.incoming.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_TIME_TO_LIVE = "jms.osgp.core.responses.incoming.time.to.live";

    private static final String PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_INITIAL_REDELIVERY_DELAY = "jms.osgp.core.responses.incoming.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_MAXIMUM_REDELIVERIES = "jms.osgp.core.responses.incoming.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_MAXIMUM_REDELIVERY_DELAY = "jms.osgp.core.responses.incoming.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_REDELIVERY_DELAY = "jms.osgp.core.responses.incoming.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_BACK_OFF_MULTIPLIER = "jms.osgp.core.responses.incoming.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_USE_EXPONENTIAL_BACK_OFF = "jms.osgp.core.responses.incoming.use.exponential.back.off";

    @Autowired
    @Qualifier("domainCoreIncomingWebServiceRequestsMessageListener")
    private WebServiceRequestMessageListener webServiceRequestMessageListener;

    @Autowired
    @Qualifier("domainCoreIncomingOsgpCoreResponsesMessageListener")
    private OsgpCoreResponseMessageListener osgpCoreResponseMessageListener;

    @Autowired
    @Qualifier("domainCoreIncomingOsgpCoreRequestsMessageListener")
    private OsgpCoreRequestMessageListener osgpCoreRequestMessageListener;

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
        redeliveryPolicyMap.put(this.commonWsRequestsQueue(), this.commonWsRequestsRedeliveryPolicy());
        redeliveryPolicyMap.put(this.commonWsResponsesQueue(), this.commonWsResponsesRedeliveryPolicy());
        redeliveryPolicyMap.put(this.osgpCoreRequestsQueue(), this.osgpCoreRequestsRedeliveryPolicy());
        redeliveryPolicyMap.put(this.osgpCoreResponsesQueue(), this.osgpCoreResponsesRedeliveryPolicy());
        redeliveryPolicyMap.put(this.osgpCoreRequestsIncomingQueue(), this.osgpCoreRequestsIncomingRedeliveryPolicy());
        redeliveryPolicyMap
        .put(this.osgpCoreResponsesIncomingQueue(), this.osgpCoreResponsesIncomingRedeliveryPolicy());
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

    // === JMS SETTINGS: COMMON WEB SERVICE REQUESTS ===

    @Bean(name = "domainCoreIncomingWebServiceRequestsQueue")
    public ActiveMQDestination commonWsRequestsQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_REQUESTS_QUEUE));
    }

    @Bean(name = "domainCoreIncomingWebServiceRequestsRedeliveryPolicy")
    public RedeliveryPolicy commonWsRequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.commonWsRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean(name = "domainCoreIncomingWebServiceRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer commonWsRequestsMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(this.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.commonWsRequestsQueue());
        messageListenerContainer.setConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_REQUESTS_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_REQUESTS_MAX_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMessageListener(this.webServiceRequestMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    // === JMS SETTINGS: COMMON WEB SERVICE RESPONSES ===

    @Bean(name = "domainCoreOutgoingWebServiceResponsesJmsTemplate")
    public JmsTemplate commonWsResponsesJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.commonWsResponsesQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_RECEIVE_TIMEOUT)));
        return jmsTemplate;
    }

    @Bean(name = "domainCoreOutgoingWebServiceResponsesQueue")
    public ActiveMQDestination commonWsResponsesQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_QUEUE));
    }

    @Bean(name = "domainCoreOutgoingWebServiceResponsesRedeliveryPolicy")
    public RedeliveryPolicy commonWsResponsesRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.commonWsResponsesQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_WS_RESPONSES_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean(name = "domainCoreOutgoingWebServiceResponsesMessageSender")
    public WebServiceResponseMessageSender commonWsResponsesMessageSender() {
        return new WebServiceResponseMessageSender();
    }

    // === JMS SETTINGS: OSGP DOMAIN CORE REQUESTS ===

    @Bean(name = "domainCoreOutgoingOsgpCoreRequestsJmsTemplate")
    public JmsTemplate osgpCoreRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.osgpCoreRequestsQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean(name = "domainCoreOutgoingOsgpCoreRequestsQueue")
    public ActiveMQDestination osgpCoreRequestsQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_QUEUE));
    }

    @Bean(name = "domainCoreOutgoingOsgpCoreRequestsRedeliveryPolicy")
    public RedeliveryPolicy osgpCoreRequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.osgpCoreRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    // === JMS SETTINGS: OSGP DOMAIN CORE RESPONSES ===

    @Bean(name = "domainCoreIncomingOsgpCoreResponsesQueue")
    public ActiveMQDestination osgpCoreResponsesQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_QUEUE));
    }

    @Bean(name = "domainCoreIncomingOsgpCoreResponsesRedeliveryPolicy")
    public RedeliveryPolicy osgpCoreResponsesRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.osgpCoreResponsesQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean(name = "domainCoreIncomingOsgpCoreResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer osgpCoreResponsesMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(this.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.osgpCoreResponsesQueue());
        messageListenerContainer.setConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_MAX_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMessageListener(this.osgpCoreResponseMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    // === JMS SETTINGS: OSGP DOMAIN CORE INCOMING REQUESTS ===

    @Bean(name = "domainCoreIncomingOsgpCoreRequestsQueue")
    public ActiveMQDestination osgpCoreRequestsIncomingQueue() {
        return new ActiveMQQueue(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INCOMING_QUEUE));
    }

    @Bean(name = "domainCoreIncomingOsgpCoreRequestsRedeliveryPolicy")
    public RedeliveryPolicy osgpCoreRequestsIncomingRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INCOMING_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INCOMING_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INCOMING_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INCOMING_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.osgpCoreRequestsIncomingQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INCOMING_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INCOMING_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean(name = "domainCoreIncomingOsgpCoreRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer osgpCoreRequestsIncomingMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(this.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.osgpCoreRequestsIncomingQueue());
        messageListenerContainer.setConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INCOMING_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_REQUESTS_INCOMING_MAX_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMessageListener(this.osgpCoreRequestMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    // === JMS SETTINGS: OSGP DOMAIN CORE INCOMING RESPONSES ===

    @Bean(name = "domainCoreOutgoingOsgpCoreResponsesJmsTemplate")
    public JmsTemplate osgpCoreResponsesIncomingJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.osgpCoreResponsesIncomingQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean(name = "domainCoreOutgoingOsgpCoreResponsesQueue")
    public ActiveMQDestination osgpCoreResponsesIncomingQueue() {
        return new ActiveMQQueue(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_QUEUE));
    }

    @Bean(name = "domainCoreOutgoingOsgpCoreResponsesRedeliveryPolicy")
    public RedeliveryPolicy osgpCoreResponsesIncomingRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.osgpCoreResponsesIncomingQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_CORE_RESPONSES_INCOMING_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }
}
