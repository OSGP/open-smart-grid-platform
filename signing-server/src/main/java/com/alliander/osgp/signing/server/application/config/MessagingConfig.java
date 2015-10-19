/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.signing.server.application.config;

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

import com.alliander.osgp.signing.server.infra.messaging.SigningServerResponseMessageSender;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@Configuration
@EnableTransactionManagement()
@PropertySource("file:${osp/signingServer/config}")
public class MessagingConfig {

    // JMS Settings
    private static final String PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL = "jms.activemq.broker.url";

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.default.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_BACK_OFF_MULTIPLIER = "jms.default.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_DEFAULT_USE_EXPONENTIAL_BACK_OFF = "jms.default.use.exponential.back.off";

    // JMS Settings: singing requests
    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_REQUESTS_QUEUE = "jms.signing.server.requests.queue";

    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_REQUESTS_CONCURRENT_CONSUMERS = "jms.signing.server.requests.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_REQUESTS_MAX_CONCURRENT_CONSUMERS = "jms.signing.server.requests.max.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.signing.server.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_REQUESTS_MAXIMUM_REDELIVERIES = "jms.signing.server.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.signing.server.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_REQUESTS_REDELIVERY_DELAY = "jms.signing.server.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_REQUESTS_BACK_OFF_MULTIPLIER = "jms.signing.server.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.signing.server.requests.use.exponential.back.off";

    // JMS Settings: signing responses
    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_QUEUE = "jms.signing.server.responses.queue";

    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_EXPLICIT_QOS_ENABLED = "jms.signing.server.responses.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_DELIVERY_PERSISTENT = "jms.signing.server.responses.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_TIME_TO_LIVE = "jms.signing.server.responses.time.to.live";
    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_RECEIVE_TIMEOUT = "jms.signing.server.responses.receive.timeout";

    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_INITIAL_REDELIVERY_DELAY = "jms.signing.server.responses.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_MAXIMUM_REDELIVERIES = "jms.signing.server.responses.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_MAXIMUM_REDELIVERY_DELAY = "jms.signing.server.responses.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_REDELIVERY_DELAY = "jms.signing.server.responses.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_BACK_OFF_MULTIPLIER = "jms.signing.server.responses.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_USE_EXPONENTIAL_BACK_OFF = "jms.signing.server.responses.use.exponential.back.off";

    @Resource
    private Environment environment;

    @Autowired
    @Qualifier("signingServerRequestsMessageListener")
    private MessageListener requestsMessageListener;

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
        redeliveryPolicyMap.put(this.requestsQueue(), this.oslpRequestsRedeliveryPolicy());
        redeliveryPolicyMap.put(this.responsesQueue(), this.responsesRedeliveryPolicy());
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

    // === JMS SETTINGS: SIGNING SERVER REQUESTS ===

    @Bean
    public ActiveMQDestination requestsQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_REQUESTS_QUEUE));
    }

    @Bean
    public RedeliveryPolicy oslpRequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.requestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public DefaultMessageListenerContainer requestsMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(this.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.requestsQueue());
        messageListenerContainer.setConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_REQUESTS_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_REQUESTS_MAX_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMessageListener(this.requestsMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    // === JMS SETTINGS: SIGNING SERVER RESPONSES ===

    @Bean
    public JmsTemplate responsesJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.responsesQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_RECEIVE_TIMEOUT)));
        return jmsTemplate;
    }

    @Bean
    public ActiveMQDestination responsesQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_QUEUE));
    }

    @Bean
    public RedeliveryPolicy responsesRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.responsesQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public SigningServerResponseMessageSender responseMessageSender() {
        return new SigningServerResponseMessageSender();
    }
}
