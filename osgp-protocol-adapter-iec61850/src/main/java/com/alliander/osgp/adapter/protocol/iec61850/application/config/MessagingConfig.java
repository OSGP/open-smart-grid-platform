/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.application.config;

import javax.annotation.Resource;
import javax.jms.MessageListener;

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
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.DeviceResponseMessageSender;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.Iec61850LogItemRequestMessageSender;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.OsgpRequestMessageSender;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.OsgpResponseMessageListener;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@Configuration
@EnableTransactionManagement()
@PropertySource("file:${osp/osgpAdapterProtocolIec61850/config}")
public class MessagingConfig {

    // JMS Settings
    private static final String PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL = "jms.activemq.broker.url";

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.default.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_BACK_OFF_MULTIPLIER = "jms.default.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_DEFAULT_USE_EXPONENTIAL_BACK_OFF = "jms.default.use.exponential.back.off";

    // JMS Settings: IEC61850 requests
    private static final String PROPERTY_NAME_JMS_IEC61850_REQUESTS_QUEUE = "jms.iec61850.requests.queue";

    private static final String PROPERTY_NAME_JMS_IEC61850_REQUESTS_CONCURRENT_CONSUMERS = "jms.iec61850.requests.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_IEC61850_REQUESTS_MAX_CONCURRENT_CONSUMERS = "jms.iec61850.requests.max.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_IEC61850_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.iec61850.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_IEC61850_REQUESTS_MAXIMUM_REDELIVERIES = "jms.iec61850.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_IEC61850_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.iec61850.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_IEC61850_REQUESTS_REDELIVERY_DELAY = "jms.iec61850.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_IEC61850_REQUESTS_BACK_OFF_MULTIPLIER = "jms.iec61850.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_IEC61850_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.iec61850.requests.use.exponential.back.off";

    // JMS Settings: IEC61850 responses
    private static final String PROPERTY_NAME_JMS_IEC61850_RESPONSES_QUEUE = "jms.iec61850.responses.queue";

    private static final String PROPERTY_NAME_JMS_IEC61850_RESPONSES_EXPLICIT_QOS_ENABLED = "jms.iec61850.responses.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_IEC61850_RESPONSES_DELIVERY_PERSISTENT = "jms.iec61850.responses.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_IEC61850_RESPONSES_TIME_TO_LIVE = "jms.iec61850.responses.time.to.live";
    private static final String PROPERTY_NAME_JMS_IEC61850_RESPONSES_RECEIVE_TIMEOUT = "jms.iec61850.responses.receive.timeout";

    private static final String PROPERTY_NAME_JMS_IEC61850_RESPONSES_INITIAL_REDELIVERY_DELAY = "jms.iec61850.responses.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_IEC61850_RESPONSES_MAXIMUM_REDELIVERIES = "jms.iec61850.responses.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_IEC61850_RESPONSES_MAXIMUM_REDELIVERY_DELAY = "jms.iec61850.responses.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_IEC61850_RESPONSES_REDELIVERY_DELAY = "jms.iec61850.responses.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_IEC61850_RESPONSES_BACK_OFF_MULTIPLIER = "jms.iec61850.responses.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_IEC61850_RESPONSES_USE_EXPONENTIAL_BACK_OFF = "jms.iec61850.responses.use.exponential.back.off";

    // JMS Settings: log item requests
    private static final String PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_QUEUE = "jms.iec61850.log.item.requests.queue";

    private static final String PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_EXPLICIT_QOS_ENABLED = "jms.iec61850.log.item.requests.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_DELIVERY_PERSISTENT = "jms.iec61850.log.item.requests.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_TIME_TO_LIVE = "jms.iec61850.log.item.requests.time.to.live";
    private static final String PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_RECEIVE_TIMEOUT = "jms.iec61850.log.item.requests.receive.timeout";

    private static final String PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.iec61850.log.item.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_MAXIMUM_REDELIVERIES = "jms.iec61850.log.item.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.iec61850.log.item.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_REDELIVERY_DELAY = "jms.iec61850.log.item.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_BACK_OFF_MULTIPLIER = "jms.iec61850.log.item.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.iec61850.log.item.requests.use.exponential.back.off";

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

    @Resource
    private Environment environment;

    @Autowired
    @Qualifier("iec61850RequestsMessageListener")
    private MessageListener iec61850RequestsMessageListener;

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
        redeliveryPolicyMap.put(this.iec61850RequestsQueue(), this.iec61850RequestsRedeliveryPolicy());
        redeliveryPolicyMap.put(this.iec61850ResponsesQueue(), this.iec61850ResponsesRedeliveryPolicy());
        redeliveryPolicyMap.put(this.osgpRequestsQueue(), this.osgpRequestsRedeliveryPolicy());
        redeliveryPolicyMap.put(this.osgpResponsesQueue(), this.osgpResponsesRedeliveryPolicy());
        redeliveryPolicyMap.put(this.iec61850LogItemRequestsQueue(), this.iec61850LogItemRequestsRedeliveryPolicy());
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

    // === JMS SETTINGS IEC61850 REQUESTS ===

    @Bean
    public ActiveMQDestination iec61850RequestsQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_REQUESTS_QUEUE));
    }

    @Bean
    public RedeliveryPolicy iec61850RequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.iec61850RequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public DefaultMessageListenerContainer iec61850RequestsMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(this.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.iec61850RequestsQueue());
        messageListenerContainer.setConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_REQUESTS_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_REQUESTS_MAX_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMessageListener(this.iec61850RequestsMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    // === JMS SETTINGS: IEC61850 RESPONSES ===

    @Bean
    public JmsTemplate iec61850ResponsesJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.iec61850ResponsesQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_RESPONSES_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_RESPONSES_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_RESPONSES_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_RESPONSES_RECEIVE_TIMEOUT)));
        return jmsTemplate;
    }

    @Bean
    public ActiveMQDestination iec61850ResponsesQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_RESPONSES_QUEUE));
    }

    @Bean
    public RedeliveryPolicy iec61850ResponsesRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_RESPONSES_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_RESPONSES_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_RESPONSES_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_RESPONSES_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.iec61850ResponsesQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_RESPONSES_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_RESPONSES_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public DeviceResponseMessageSender iec61850ResponseMessageSender() {
        return new DeviceResponseMessageSender();
    }

    // === JMS SETTINGS: IEC61850 LOG ITEM REQUESTS ===

    @Bean
    public JmsTemplate iec61850LogItemRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.iec61850LogItemRequestsQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_RECEIVE_TIMEOUT)));
        return jmsTemplate;
    }

    @Bean
    public ActiveMQDestination iec61850LogItemRequestsQueue() {
        return new ActiveMQQueue(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_QUEUE));
    }

    @Bean
    public RedeliveryPolicy iec61850LogItemRequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.iec61850LogItemRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_IEC61850_LOG_ITEM_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public Iec61850LogItemRequestMessageSender iec61850LogItemRequestMessageSender() {
        return new Iec61850LogItemRequestMessageSender();
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
        redeliveryPolicy.setDestination(this.iec61850RequestsQueue());
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
