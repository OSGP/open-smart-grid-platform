/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.config;

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
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsLogItemRequestMessageSender;
import org.osgp.adapter.protocol.dlms.infra.messaging.OsgpRequestMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alliander.osgp.shared.application.config.AbstractConfig;

/**
 * An application context Java configuration class.
 */
@Configuration
@EnableTransactionManagement()
@PropertySources({
	@PropertySource("classpath:osgp-adapter-protocol-dlms.properties"),
    @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
	@PropertySource(value = "file:${osgp/AdapterProtocolDlms/config}", ignoreResourceNotFound = true),
})
public class MessagingConfig extends AbstractConfig {

    // JMS Settings
    private static final String PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL = "jms.activemq.broker.url";
    private static final String PROPERTY_NAME_JMS_ACTIVEMQ_CONNECTION_POOL_SIZE = "jms.activemq.connection.pool.size";

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.default.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_BACK_OFF_MULTIPLIER = "jms.default.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_DEFAULT_USE_EXPONENTIAL_BACK_OFF = "jms.default.use.exponential.back.off";

    // JMS Settings: Dlms requests
    private static final String PROPERTY_NAME_JMS_DLMS_REQUESTS_QUEUE = "jms.dlms.requests.queue";

    private static final String PROPERTY_NAME_JMS_DLMS_REQUESTS_CONCURRENT_CONSUMERS = "jms.dlms.requests.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_DLMS_REQUESTS_MAX_CONCURRENT_CONSUMERS = "jms.dlms.requests.max.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_DLMS_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.dlms.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DLMS_REQUESTS_MAXIMUM_REDELIVERIES = "jms.dlms.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DLMS_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.dlms.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DLMS_REQUESTS_REDELIVERY_DELAY = "jms.dlms.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DLMS_REQUESTS_BACK_OFF_MULTIPLIER = "jms.dlms.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_DLMS_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.dlms.requests.use.exponential.back.off";

    // JMS Settings: Dlms responses
    private static final String PROPERTY_NAME_JMS_DLMS_RESPONSES_QUEUE = "jms.dlms.responses.queue";
    private static final String PROPERTY_NAME_JMS_DLMS_RESPONSES_EXPLICIT_QOS_ENABLED = "jms.dlms.responses.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_DLMS_RESPONSES_DELIVERY_PERSISTENT = "jms.dlms.responses.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_DLMS_RESPONSES_TIME_TO_LIVE = "jms.dlms.responses.time.to.live";
    private static final String PROPERTY_NAME_JMS_DLMS_RESPONSES_RECEIVE_TIMEOUT = "jms.dlms.responses.receive.timeout";

    private static final String PROPERTY_NAME_JMS_DLMS_RESPONSES_INITIAL_REDELIVERY_DELAY = "jms.dlms.responses.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DLMS_RESPONSES_MAXIMUM_REDELIVERIES = "jms.dlms.responses.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DLMS_RESPONSES_MAXIMUM_REDELIVERY_DELAY = "jms.dlms.responses.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DLMS_RESPONSES_REDELIVERY_DELAY = "jms.dlms.responses.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DLMS_RESPONSES_BACK_OFF_MULTIPLIER = "jms.dlms.responses.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_DLMS_RESPONSES_USE_EXPONENTIAL_BACK_OFF = "jms.dlms.responses.use.exponential.back.off";

    // JMS Settings: Dlms log item requests
    private static final String PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_QUEUE = "jms.dlms.log.item.requests.queue";
    private static final String PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_EXPLICIT_QOS_ENABLED = "jms.dlms.log.item.requests.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_DELIVERY_PERSISTENT = "jms.dlms.log.item.requests.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_TIME_TO_LIVE = "jms.dlms.log.item.requests.time.to.live";
    private static final String PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_RECEIVE_TIMEOUT = "jms.dlms.log.item.requests.receive.timeout";

    private static final String PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.dlms.log.item.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_MAXIMUM_REDELIVERIES = "jms.dlms.log.item.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.dlms.log.item.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_REDELIVERY_DELAY = "jms.dlms.log.item.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_BACK_OFF_MULTIPLIER = "jms.dlms.log.item.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.dlms.log.item.requests.use.exponential.back.off";

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
    @Qualifier("dlmsRequestsMessageListener")
    private MessageListener dlmsRequestsMessageListener;

    public MessagingConfig() {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    // === JMS SETTINGS ===

    @Bean(destroyMethod = "stop")
    public PooledConnectionFactory pooledConnectionFactory() {
        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(this.connectionFactory());
        pooledConnectionFactory.setMaxConnections(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ACTIVEMQ_CONNECTION_POOL_SIZE)));
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
        redeliveryPolicyMap.put(this.dlmsRequestsQueue(), this.dlmsRequestsRedeliveryPolicy());
        redeliveryPolicyMap.put(this.dlmsResponsesQueue(), this.dlmsResponsesRedeliveryPolicy());
        redeliveryPolicyMap.put(this.osgpRequestsQueue(), this.osgpRequestsRedeliveryPolicy());
        redeliveryPolicyMap.put(this.osgpResponsesQueue(), this.osgpResponsesRedeliveryPolicy());
        redeliveryPolicyMap.put(this.dlmsLogItemRequestsQueue(), this.dlmsLogItemRequestsRedeliveryPolicy());
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

    // === JMS SETTINGS DLMS REQUESTS ===

    @Bean
    public ActiveMQDestination dlmsRequestsQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DLMS_REQUESTS_QUEUE));
    }

    @Bean
    public RedeliveryPolicy dlmsRequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.dlmsRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public DefaultMessageListenerContainer dlmsRequestsMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(this.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.dlmsRequestsQueue());
        messageListenerContainer.setConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_REQUESTS_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_REQUESTS_MAX_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMessageListener(this.dlmsRequestsMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    // === JMS SETTINGS: DLMS RESPONSES ===

    @Bean
    public JmsTemplate dlmsResponsesJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.dlmsResponsesQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_RESPONSES_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_RESPONSES_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_RESPONSES_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_RESPONSES_RECEIVE_TIMEOUT)));
        return jmsTemplate;
    }

    @Bean
    public ActiveMQDestination dlmsResponsesQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DLMS_RESPONSES_QUEUE));
    }

    @Bean
    public RedeliveryPolicy dlmsResponsesRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_RESPONSES_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_RESPONSES_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_RESPONSES_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_RESPONSES_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.dlmsResponsesQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_RESPONSES_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_RESPONSES_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public DeviceResponseMessageSender dlmsResponseMessageSender() {
        return new DeviceResponseMessageSender();
    }

    // === JMS SETTINGS: DLMS LOG ITEM REQUESTS ===

    @Bean
    public JmsTemplate dlmsLogItemRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.dlmsLogItemRequestsQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_RECEIVE_TIMEOUT)));
        return jmsTemplate;
    }

    @Bean
    public ActiveMQDestination dlmsLogItemRequestsQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_QUEUE));
    }

    @Bean
    public RedeliveryPolicy dlmsLogItemRequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.dlmsLogItemRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DLMS_LOG_ITEM_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public DlmsLogItemRequestMessageSender dlmsLogItemRequestMessageSender() {
        return new DlmsLogItemRequestMessageSender();
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
        redeliveryPolicy.setDestination(this.dlmsRequestsQueue());
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
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

}
