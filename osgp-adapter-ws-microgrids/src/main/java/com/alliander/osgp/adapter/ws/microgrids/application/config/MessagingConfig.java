/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.microgrids.application.config;

import javax.annotation.Resource;

import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.alliander.osgp.adapter.ws.infra.jms.LoggingMessageSender;
import com.alliander.osgp.adapter.ws.microgrids.infra.jms.MicrogridsRequestMessageSender;
import com.alliander.osgp.adapter.ws.microgrids.infra.jms.MicrogridsResponseMessageFinder;
import com.alliander.osgp.adapter.ws.microgrids.infra.jms.MicrogridsResponseMessageListener;

@Configuration
@PropertySource("file:${osp/osgpAdapterWsMicrogrids/config}")
public class MessagingConfig {

    @Resource
    private Environment environment;

    // JMS Settings
    private static final String PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL = "jms.activemq.broker.url";

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.default.redelivery.delay";

    // JMS Settings: Microgrids logging
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_QUEUE = "jms.microgrids.logging.queue";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_EXPLICIT_QOS_ENABLED = "jms.microgrids.logging.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_DELIVERY_PERSISTENT = "jms.microgrids.logging.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_TIME_TO_LIVE = "jms.microgrids.logging.time.to.live";

    private static final String PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_INITIAL_REDELIVERY_DELAY = "jms.microgrids.logging.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_MAXIMUM_REDELIVERIES = "jms.microgrids.logging.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_MAXIMUM_REDELIVERY_DELAY = "jms.microgrids.logging.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_REDELIVERY_DELAY = "jms.microgrids.logging.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_BACK_OFF_MULTIPLIER = "jms.microgrids.logging.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_USE_EXPONENTIAL_BACK_OFF = "jms.microgrids.logging.use.exponential.back.off";

    // JMS Settings: Microgrids requests
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_QUEUE = "jms.microgrids.requests.queue";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_EXPLICIT_QOS_ENABLED = "jms.microgrids.requests.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_TIME_TO_LIVE = "jms.microgrids.responses.time.to.live";

    private static final String PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_DELIVERY_PERSISTENT = "jms.microgrids.requests.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_TIME_TO_LIVE = "jms.microgrids.requests.time.to.live";

    private static final String PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.microgrids.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_MAXIMUM_REDELIVERIES = "jms.microgrids.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.microgrids.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_REDELIVERY_DELAY = "jms.microgrids.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_BACK_OFF_MULTIPLIER = "jms.microgrids.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.microgrids.requests.use.exponential.back.off";

    // JMS Settings: Microgrids responses
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_QUEUE = "jms.microgrids.responses.queue";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_EXPLICIT_QOS_ENABLED = "jms.microgrids.responses.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_DELIVERY_PERSISTENT = "jms.microgrids.responses.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_RECEIVE_TIMEOUT = "jms.microgrids.responses.receive.timeout";

    private static final String PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_INITIAL_REDELIVERY_DELAY = "jms.microgrids.responses.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_MAXIMUM_REDELIVERIES = "jms.microgrids.responses.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_MAXIMUM_REDELIVERY_DELAY = "jms.microgrids.responses.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_REDELIVERY_DELAY = "jms.microgrids.responses.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_BACK_OFF_MULTIPLIER = "jms.microgrids.responses.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_USE_EXPONENTIAL_BACK_OFF = "jms.microgrids.responses.use.exponential.back.off";

    @Autowired
    public MicrogridsResponseMessageListener microgridsResponseMessageListener;

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
        activeMQConnectionFactory
                .setBrokerURL(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL));

        activeMQConnectionFactory.setNonBlockingRedelivery(true);

        return activeMQConnectionFactory;
    }

    @Bean
    public RedeliveryPolicyMap redeliveryPolicyMap() {
        final RedeliveryPolicyMap redeliveryPolicyMap = new RedeliveryPolicyMap();
        redeliveryPolicyMap.setDefaultEntry(this.defaultRedeliveryPolicy());
        redeliveryPolicyMap.put(this.microgridsRequestsQueue(), this.microgridsRequestsRedeliveryPolicy());
        redeliveryPolicyMap.put(this.microgridsResponsesQueue(), this.microgridsResponsesRedeliveryPolicy());
        redeliveryPolicyMap.put(this.microgridsLoggingQueue(), this.microgridsLoggingRedeliveryPolicy());
        return redeliveryPolicyMap;
    }

    @Bean
    public RedeliveryPolicy defaultRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long
                .parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(
                Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long
                .parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(
                Long.parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY)));

        return redeliveryPolicy;
    }

    // === JMS SETTINGS: Microgrids REQUESTS ===

    @Bean
    public ActiveMQDestination microgridsRequestsQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_QUEUE));
    }

    /**
     * @return
     */
    @Bean(name = "wsMicrogridsOutgoingRequestsJmsTemplate")
    public JmsTemplate microgridsRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.microgridsRequestsQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long
                .parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean
    public RedeliveryPolicy microgridsRequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.microgridsRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));

        return redeliveryPolicy;
    }

    /**
     * @return
     */
    @Bean(name = "wsMicrogridsOutgoingRequestsMessageSender")
    public MicrogridsRequestMessageSender microgridsRequestMessageSender() {
        return new MicrogridsRequestMessageSender();
    }

    // === JMS SETTINGS: Microgrids RESPONSES ===

    /**
     * @return
     */
    @Bean(name = "wsMicrogridsIncomingResponsesJmsTemplate")
    public JmsTemplate microgridsResponsesJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.microgridsResponsesQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long
                .parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(Long.parseLong(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_RECEIVE_TIMEOUT)));
        return jmsTemplate;
    }

    @Bean
    public ActiveMQDestination microgridsResponsesQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_QUEUE));
    }

    @Bean
    public RedeliveryPolicy microgridsResponsesRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.microgridsRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_RESPONSES_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    /**
     * @return
     */
    @Bean(name = "wsMicrogridsIncomingResponsesMessageFinder")
    public MicrogridsResponseMessageFinder microgridsResponseMessageFinder() {
        return new MicrogridsResponseMessageFinder();
    }

    @Bean(name = "wsMicrogridsResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer microgridsResponseMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(this.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.microgridsResponsesQueue());

        //
        // TODO: add concurrent consumer properties.
        //

        // messageListenerContainer.setConcurrentConsumers(Integer.parseInt(this.environment
        // .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_CONCURRENT_CONSUMERS)));
        // messageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(this.environment
        // .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_MAX_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMessageListener(this.microgridsResponseMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    @Bean
    public MicrogridsResponseMessageListener microgridsResponseMessageListener() {
        return new MicrogridsResponseMessageListener();
    }

    // === JMS SETTINGS: MICROGRIDS LOGGING ===

    @Bean
    public ActiveMQDestination microgridsLoggingQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_QUEUE));
    }

    /**
     * @return
     */
    @Bean
    public JmsTemplate loggingJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.microgridsLoggingQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long
                .parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    /**
     * @return
     */
    @Bean
    public RedeliveryPolicy microgridsLoggingRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.microgridsLoggingQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_MICROGRIDS_LOGGING_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public LoggingMessageSender loggingMessageSender() {
        return new LoggingMessageSender();
    }

}
