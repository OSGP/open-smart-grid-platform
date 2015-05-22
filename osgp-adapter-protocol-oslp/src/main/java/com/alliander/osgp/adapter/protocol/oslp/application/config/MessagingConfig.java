/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.application.config;

import javax.annotation.Resource;
import javax.jms.MessageListener;

import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceResponseMessageSender;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.OsgpRequestMessageSender;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.OsgpResponseMessageListener;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.OslpLogItemRequestMessageSender;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@Configuration
@EnableTransactionManagement()
@PropertySource("file:${osp/osgpAdapterProtocolOslp/config}")
public class MessagingConfig {

    // JMS Settings
    private static final String PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL = "jms.activemq.broker.url";

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.default.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_BACK_OFF_MULTIPLIER = "jms.default.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_DEFAULT_USE_EXPONENTIAL_BACK_OFF = "jms.default.use.exponential.back.off";

    // JMS Settings: Oslp requests
    private static final String PROPERTY_NAME_JMS_OSLP_REQUESTS_QUEUE = "jms.oslp.requests.queue";

    private static final String PROPERTY_NAME_JMS_OSLP_REQUESTS_CONCURRENT_CONSUMERS = "jms.oslp.requests.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_OSLP_REQUESTS_MAX_CONCURRENT_CONSUMERS = "jms.oslp.requests.max.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_OSLP_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.oslp.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSLP_REQUESTS_MAXIMUM_REDELIVERIES = "jms.oslp.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_OSLP_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.oslp.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSLP_REQUESTS_REDELIVERY_DELAY = "jms.oslp.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSLP_REQUESTS_BACK_OFF_MULTIPLIER = "jms.oslp.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_OSLP_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.oslp.requests.use.exponential.back.off";

    // JMS Settings: Oslp responses
    private static final String PROPERTY_NAME_JMS_OSLP_RESPONSES_QUEUE = "jms.oslp.responses.queue";
    private static final String PROPERTY_NAME_JMS_OSLP_RESPONSES_EXPLICIT_QOS_ENABLED = "jms.oslp.responses.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_OSLP_RESPONSES_DELIVERY_PERSISTENT = "jms.oslp.responses.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_OSLP_RESPONSES_TIME_TO_LIVE = "jms.oslp.responses.time.to.live";
    private static final String PROPERTY_NAME_JMS_OSLP_RESPONSES_RECEIVE_TIMEOUT = "jms.oslp.responses.receive.timeout";

    private static final String PROPERTY_NAME_JMS_OSLP_RESPONSES_INITIAL_REDELIVERY_DELAY = "jms.oslp.responses.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSLP_RESPONSES_MAXIMUM_REDELIVERIES = "jms.oslp.responses.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_OSLP_RESPONSES_MAXIMUM_REDELIVERY_DELAY = "jms.oslp.responses.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSLP_RESPONSES_REDELIVERY_DELAY = "jms.oslp.responses.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSLP_RESPONSES_BACK_OFF_MULTIPLIER = "jms.oslp.responses.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_OSLP_RESPONSES_USE_EXPONENTIAL_BACK_OFF = "jms.oslp.responses.use.exponential.back.off";

    // JMS Settings: Oslp log item requests
    private static final String PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_QUEUE = "jms.oslp.log.item.requests.queue";
    private static final String PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_EXPLICIT_QOS_ENABLED = "jms.oslp.log.item.requests.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_DELIVERY_PERSISTENT = "jms.oslp.log.item.requests.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_TIME_TO_LIVE = "jms.oslp.log.item.requests.time.to.live";
    private static final String PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_RECEIVE_TIMEOUT = "jms.oslp.log.item.requests.receive.timeout";

    private static final String PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.oslp.log.item.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_MAXIMUM_REDELIVERIES = "jms.oslp.log.item.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.oslp.log.item.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_REDELIVERY_DELAY = "jms.oslp.log.item.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_BACK_OFF_MULTIPLIER = "jms.oslp.log.item.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.oslp.log.item.requests.use.exponential.back.off";

    // JMS Settings: OSGP requests
    private static final String PROPERTY_NAME_JMS_OSGP_REQUESTS_QUEUE = "jms.osgp.requests.queue";
    private static final String PROPERTY_NAME_JMS_OSGP_REQUESTS_EXPLICIT_QOS_ENABLED = "jms.osgp.requests.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_OSGP_REQUESTS_DELIVERY_PERSISTENT = "jms.osgp.requests.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_OSGP_REQUESTS_TIME_TO_LIVE = "jms.osgp.requests.time.to.live";
    private static final String PROPERTY_NAME_JMS_OSGP_REQUESTS_RECEIVE_TIMEOUT = "jms.osgp.requests.receive.timeout";

    private static final String PROPERTY_NAME_JMS_OSGP_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.osgp.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSGP_REQUESTS_MAXIMUM_REDELIVERIES = "jms.osgp.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_OSGP_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.osgp.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSGP_REQUESTS_REDELIVERY_DELAY = "jms.osgp.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSGP_REQUESTS_BACK_OFF_MULTIPLIER = "jms.osgp.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_OSGP_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.osgp.requests.use.exponential.back.off";

    // JSM Settings: OSGP responses
    private static final String PROPERTY_NAME_JMS_OSGP_RESPONSES_QUEUE = "jms.osgp.responses.queue";

    private static final String PROPERTY_NAME_JMS_OSGP_RESPONSES_CONCURRENT_CONSUMERS = "jms.osgp.responses.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_OSGP_RESPONSES_MAX_CONCURRENT_CONSUMERS = "jms.osgp.responses.max.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_OSGP_RESPONSES_INITIAL_REDELIVERY_DELAY = "jms.osgp.responses.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSGP_RESPONSES_MAXIMUM_REDELIVERIES = "jms.osgp.responses.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_OSGP_RESPONSES_MAXIMUM_REDELIVERY_DELAY = "jms.osgp.responses.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSGP_RESPONSES_REDELIVERY_DELAY = "jms.osgp.responses.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_OSGP_RESPONSES_BACK_OFF_MULTIPLIER = "jms.osgp.responses.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_OSGP_RESPONSES_USE_EXPONENTIAL_BACK_OFF = "jms.osgp.responses.use.exponential.back.off";

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationContext.class);

    @Resource
    private Environment environment;

    @Autowired
    @Qualifier("oslpRequestsMessageListener")
    private MessageListener oslpRequestsMessageListener;

    public MessagingConfig() {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

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
        redeliveryPolicyMap.put(this.oslpRequestsQueue(), this.oslpRequestsRedeliveryPolicy());
        redeliveryPolicyMap.put(this.oslpResponsesQueue(), this.oslpResponsesRedeliveryPolicy());
        redeliveryPolicyMap.put(this.osgpRequestsQueue(), this.osgpRequestsRedeliveryPolicy());
        redeliveryPolicyMap.put(this.osgpResponsesQueue(), this.osgpResponsesRedeliveryPolicy());
        redeliveryPolicyMap.put(this.oslpLogItemRequestsQueue(), this.oslpLogItemRequestsRedeliveryPolicy());
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
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_USE_EXPONENTIAL_BACK_OFF)));

        return redeliveryPolicy;
    }

    // === JMS SETTINGS OSLP REQUESTS ===

    @Bean
    public ActiveMQDestination oslpRequestsQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_OSLP_REQUESTS_QUEUE));
    }

    @Bean
    public RedeliveryPolicy oslpRequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.oslpRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public DefaultMessageListenerContainer oslpRequestsMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(this.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.oslpRequestsQueue());
        messageListenerContainer.setConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_REQUESTS_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_REQUESTS_MAX_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMessageListener(this.oslpRequestsMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    // === JMS SETTINGS: OSLP RESPONSES ===

    @Bean
    public JmsTemplate oslpResponsesJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.oslpResponsesQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_RESPONSES_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_RESPONSES_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_RESPONSES_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_RESPONSES_RECEIVE_TIMEOUT)));
        return jmsTemplate;
    }

    @Bean
    public ActiveMQDestination oslpResponsesQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_OSLP_RESPONSES_QUEUE));
    }

    @Bean
    public RedeliveryPolicy oslpResponsesRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_RESPONSES_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_RESPONSES_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_RESPONSES_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_RESPONSES_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.oslpResponsesQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_RESPONSES_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_RESPONSES_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public DeviceResponseMessageSender oslpResponseMessageSender() {
        return new DeviceResponseMessageSender();
    }

    // === JMS SETTINGS: OSLP LOG ITEM REQUESTS ===

    @Bean
    public JmsTemplate oslpLogItemRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.oslpLogItemRequestsQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_RECEIVE_TIMEOUT)));
        return jmsTemplate;
    }

    @Bean
    public ActiveMQDestination oslpLogItemRequestsQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_QUEUE));
    }

    @Bean
    public RedeliveryPolicy oslpLogItemRequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.oslpLogItemRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSLP_LOG_ITEM_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public OslpLogItemRequestMessageSender oslpLogItemRequestMessageSender() {
        return new OslpLogItemRequestMessageSender();
    }

    // === OSGP REQUESTS ===

    @Bean
    public JmsTemplate osgpRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.osgpRequestsQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_REQUESTS_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_REQUESTS_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_REQUESTS_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_REQUESTS_RECEIVE_TIMEOUT)));
        return jmsTemplate;
    }

    @Bean
    public ActiveMQDestination osgpRequestsQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_OSGP_REQUESTS_QUEUE));
    }

    @Bean
    public RedeliveryPolicy osgpRequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.osgpRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public OsgpRequestMessageSender osgpRequestMessageSender() {
        return new OsgpRequestMessageSender();
    }

    // === OSGP RESPONSES ===

    @Bean
    public ActiveMQDestination osgpResponsesQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_OSGP_RESPONSES_QUEUE));
    }

    @Bean
    public RedeliveryPolicy osgpResponsesRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_RESPONSES_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_RESPONSES_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_RESPONSES_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_RESPONSES_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.oslpRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_RESPONSES_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_RESPONSES_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public DefaultMessageListenerContainer osgpResponsesMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(this.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.osgpResponsesQueue());
        messageListenerContainer.setConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_RESPONSES_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_OSGP_RESPONSES_MAX_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMessageListener(this.osgpResponseMessageListener());
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    @Bean
    public OsgpResponseMessageListener osgpResponseMessageListener() {
        return new OsgpResponseMessageListener();
    }

}
