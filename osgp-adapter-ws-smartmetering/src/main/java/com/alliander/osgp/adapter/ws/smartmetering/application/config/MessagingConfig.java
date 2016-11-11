/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.config;

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
import org.springframework.context.annotation.PropertySources;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.alliander.osgp.adapter.ws.infra.jms.LoggingMessageSender;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringResponseMessageListener;
import com.alliander.osgp.shared.application.config.AbstractConfig;

@Configuration
@PropertySources({
	@PropertySource("classpath:osgp-adapter-ws-smartmetering.properties"),
	@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
    @PropertySource(value = "file:${osgp/AdapterWsSmartMetering/config}", ignoreResourceNotFound = true),
})
public class MessagingConfig extends AbstractConfig {
    // JMS Settings
    private static final String PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL = "jms.activemq.broker.url";

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.default.redelivery.delay";

    // JMS Settings: Smart Metering logging
    private static final String PROPERTY_NAME_JMS_SMART_METERING_LOGGING_QUEUE = "jms.smartmetering.logging.queue";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_LOGGING_EXPLICIT_QOS_ENABLED = "jms.smartmetering.logging.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_LOGGING_DELIVERY_PERSISTENT = "jms.smartmetering.logging.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_LOGGING_TIME_TO_LIVE = "jms.smartmetering.logging.time.to.live";

    private static final String PROPERTY_NAME_JMS_SMART_METERING_LOGGING_INITIAL_REDELIVERY_DELAY = "jms.smartmetering.logging.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_LOGGING_MAXIMUM_REDELIVERIES = "jms.smartmetering.logging.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_LOGGING_MAXIMUM_REDELIVERY_DELAY = "jms.smartmetering.logging.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_LOGGING_REDELIVERY_DELAY = "jms.smartmetering.logging.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_LOGGING_BACK_OFF_MULTIPLIER = "jms.smartmetering.logging.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_LOGGING_USE_EXPONENTIAL_BACK_OFF = "jms.smartmetering.logging.use.exponential.back.off";

    // JMS Settings: Smart Metering requests
    private static final String PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_QUEUE = "jms.smartmetering.requests.queue";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_EXPLICIT_QOS_ENABLED = "jms.smartmetering.requests.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_DELIVERY_PERSISTENT = "jms.smartmetering.requests.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_TIME_TO_LIVE = "jms.smartmetering.requests.time.to.live";

    private static final String PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.smartmetering.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_MAXIMUM_REDELIVERIES = "jms.smartmetering.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.smartmetering.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_REDELIVERY_DELAY = "jms.smartmetering.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_BACK_OFF_MULTIPLIER = "jms.smartmetering.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.smartmetering.requests.use.exponential.back.off";

    // JMS Settings: Smart Metering responses
    private static final String PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_QUEUE = "jms.smartmetering.responses.queue";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_EXPLICIT_QOS_ENABLED = "jms.smartmetering.responses.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_DELIVERY_PERSISTENT = "jms.smartmetering.responses.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_TIME_TO_LIVE = "jms.smartmetering.responses.time.to.live";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_RECEIVE_TIMEOUT = "jms.smartmetering.responses.receive.timeout";

    private static final String PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_CONCURRENT_CONSUMERS = "jms.smartmetering.responses.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_MAX_CONCURRENT_CONSUMERS = "jms.smartmetering.responses.max.concurrent.consumers";

    private static final String PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_INITIAL_REDELIVERY_DELAY = "jms.smartmetering.responses.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_MAXIMUM_REDELIVERIES = "jms.smartmetering.responses.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_MAXIMUM_REDELIVERY_DELAY = "jms.smartmetering.responses.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_REDELIVERY_DELAY = "jms.smartmetering.responses.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_BACK_OFF_MULTIPLIER = "jms.smartmetering.responses.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_USE_EXPONENTIAL_BACK_OFF = "jms.smartmetering.responses.use.exponential.back.off";

    @Autowired
    public SmartMeteringResponseMessageListener smartMeteringResponseMessageListener;

    // === JMS SETTINGS ===

    @Bean
    public ActiveMQDestination smartMeteringLoggingQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_LOGGING_QUEUE));
    }

    /**
     * @return
     */
    @Bean
    public JmsTemplate loggingJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.smartMeteringLoggingQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_LOGGING_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_LOGGING_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_LOGGING_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    /**
     * @return
     */
    @Bean
    public RedeliveryPolicy smartMeteringLoggingRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_LOGGING_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_LOGGING_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_LOGGING_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_LOGGING_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.smartMeteringLoggingQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_LOGGING_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_LOGGING_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

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
        redeliveryPolicyMap.put(this.smartMeteringRequestsQueue(), this.smartMeteringRequestsRedeliveryPolicy());
        redeliveryPolicyMap.put(this.smartMeteringResponsesQueue(), this.smartMeteringResponsesRedeliveryPolicy());
        redeliveryPolicyMap.put(this.smartMeteringLoggingQueue(), this.smartMeteringLoggingRedeliveryPolicy());
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

    // === JMS SETTINGS: SMART METERING REQUESTS ===

    @Bean
    public ActiveMQDestination smartMeteringRequestsQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_QUEUE));
    }

    /**
     * @return
     */
    @Bean(name = "wsSmartMeteringOutgoingRequestsJmsTemplate")
    public JmsTemplate smartMeteringRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.smartMeteringRequestsQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean
    public RedeliveryPolicy smartMeteringRequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.smartMeteringRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));

        return redeliveryPolicy;
    }

    /**
     * @return
     */
    @Bean
    public SmartMeteringRequestMessageSender smartMeteringRequestMessageSender() {
        return new SmartMeteringRequestMessageSender();
    }

    // === JMS SETTINGS: SMART METERING RESPONSES ===

    @Bean(name = "wsSmartMeteringResponsesQueue")
    public ActiveMQDestination smartMeteringResponsesQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_QUEUE));
    }

    @Bean(name = "wsSmartMeteringResponsesRedeliveryPolicy")
    public RedeliveryPolicy smartMeteringResponsesRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.smartMeteringResponsesQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean(name = "wsSmartMeteringResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer smartMeteringResponseMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(this.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.smartMeteringResponsesQueue());
        messageListenerContainer.setConcurrentConsumers(Integer.parseInt(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_MAX_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMessageListener(this.smartMeteringResponseMessageListener);
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    @Bean
    public SmartMeteringResponseMessageListener smartMeteringResponseMessageListener() {
        return new SmartMeteringResponseMessageListener();
    }

    @Bean
    public LoggingMessageSender loggingMessageSender() {
        return new LoggingMessageSender();
    }
}
