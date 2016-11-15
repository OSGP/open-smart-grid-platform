/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.config;

import javax.annotation.Resource;

import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

import com.alliander.osgp.adapter.ws.core.infra.jms.CommonRequestMessageSender;
import com.alliander.osgp.adapter.ws.core.infra.jms.CommonResponseMessageFinder;
import com.alliander.osgp.adapter.ws.infra.jms.LoggingMessageSender;
import com.alliander.osgp.shared.application.config.AbstractConfig;

@Configuration
@PropertySources({
	@PropertySource("classpath:osgp-adapter-ws-core.properties"),
    @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
	@PropertySource(value = "file:${osgp/AdapterWsCore/config}", ignoreResourceNotFound = true),
})
public class MessagingConfig extends AbstractConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingConfig.class);

    // JMS Settings
    private static final String PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL = "jms.activemq.broker.url";

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.default.redelivery.delay";
    // JMS Settings: Common requests
    private static final String PROPERTY_NAME_JMS_COMMON_REQUESTS_QUEUE = "jms.common.requests.queue";
    private static final String PROPERTY_NAME_JMS_COMMON_REQUESTS_EXPLICIT_QOS_ENABLED = "jms.common.requests.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_COMMON_REQUESTS_DELIVERY_PERSISTENT = "jms.common.requests.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_COMMON_REQUESTS_TIME_TO_LIVE = "jms.common.requests.time.to.live";

    private static final String PROPERTY_NAME_JMS_COMMON_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.common.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_COMMON_REQUESTS_MAXIMUM_REDELIVERIES = "jms.common.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_COMMON_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.common.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_COMMON_REQUESTS_REDELIVERY_DELAY = "jms.common.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_COMMON_REQUESTS_BACK_OFF_MULTIPLIER = "jms.common.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_COMMON_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.common.requests.use.exponential.back.off";

    // JMS Settings: Common logging
    private static final String PROPERTY_NAME_JMS_COMMON_LOGGING_QUEUE = "jms.common.logging.queue";
    private static final String PROPERTY_NAME_JMS_COMMON_LOGGING_EXPLICIT_QOS_ENABLED = "jms.common.logging.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_COMMON_LOGGING_DELIVERY_PERSISTENT = "jms.common.logging.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_COMMON_LOGGING_TIME_TO_LIVE = "jms.common.logging.time.to.live";

    private static final String PROPERTY_NAME_JMS_COMMON_LOGGING_INITIAL_REDELIVERY_DELAY = "jms.common.logging.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_COMMON_LOGGING_MAXIMUM_REDELIVERIES = "jms.common.logging.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_COMMON_LOGGING_MAXIMUM_REDELIVERY_DELAY = "jms.common.logging.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_COMMON_LOGGING_REDELIVERY_DELAY = "jms.common.logging.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_COMMON_LOGGING_BACK_OFF_MULTIPLIER = "jms.common.logging.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_COMMON_LOGGING_USE_EXPONENTIAL_BACK_OFF = "jms.common.logging.use.exponential.back.off";

    // JMS Settings: Common responses
    private static final String PROPERTY_NAME_JMS_COMMON_RESPONSES_QUEUE = "jms.common.responses.queue";
    private static final String PROPERTY_NAME_JMS_COMMON_RESPONSES_EXPLICIT_QOS_ENABLED = "jms.common.responses.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_COMMON_RESPONSES_DELIVERY_PERSISTENT = "jms.common.responses.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_COMMON_RESPONSES_TIME_TO_LIVE = "jms.common.responses.time.to.live";
    private static final String PROPERTY_NAME_JMS_COMMON_RESPONSES_RECEIVE_TIMEOUT = "jms.common.responses.receive.timeout";

    private static final String PROPERTY_NAME_JMS_COMMON_RESPONSES_INITIAL_REDELIVERY_DELAY = "jms.common.responses.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_COMMON_RESPONSES_MAXIMUM_REDELIVERIES = "jms.common.responses.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_COMMON_RESPONSES_MAXIMUM_REDELIVERY_DELAY = "jms.common.responses.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_COMMON_RESPONSES_REDELIVERY_DELAY = "jms.common.responses.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_COMMON_RESPONSES_BACK_OFF_MULTIPLIER = "jms.common.responses.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_COMMON_RESPONSES_USE_EXPONENTIAL_BACK_OFF = "jms.common.responses.use.exponential.back.off";

    @Resource
    private Environment environment;

    // === JMS SETTINGS ===

    @Bean(destroyMethod = "stop")
    public PooledConnectionFactory pooledConnectionFactory() {
        LOGGER.debug("Creating Pooled Connection Factory Bean");

        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(this.connectionFactory());
        return pooledConnectionFactory;
    }

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        LOGGER.debug("Creating Connection Factory Bean");

        final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setRedeliveryPolicyMap(this.redeliveryPolicyMap());
        activeMQConnectionFactory.setBrokerURL(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL));

        activeMQConnectionFactory.setNonBlockingRedelivery(true);

        return activeMQConnectionFactory;
    }

    @Bean
    public RedeliveryPolicyMap redeliveryPolicyMap() {
        LOGGER.debug("Creating Redelivery Policy Map Bean");

        final RedeliveryPolicyMap redeliveryPolicyMap = new RedeliveryPolicyMap();
        redeliveryPolicyMap.setDefaultEntry(this.defaultRedeliveryPolicy());
        redeliveryPolicyMap.put(this.commonRequestsQueue(), this.commonRequestsRedeliveryPolicy());
        redeliveryPolicyMap.put(this.commonResponsesQueue(), this.commonResponsesRedeliveryPolicy());
        redeliveryPolicyMap.put(this.commonLoggingQueue(), this.commonLoggingRedeliveryPolicy());
        return redeliveryPolicyMap;
    }

    @Bean
    public RedeliveryPolicy defaultRedeliveryPolicy() {
        LOGGER.debug("Creating Default Redelivery Policy Bean");

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

    // === JMS SETTINGS: COMMON REQUESTS ===

    /**
     * @return
     */
    @Bean(name = "wsCoreOutgoingRequestsJmsTemplate")
    public JmsTemplate commonRequestsJmsTemplate() {
        LOGGER.debug("Creating Common Requests JMS Template Bean");

        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.commonRequestsQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_REQUESTS_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_REQUESTS_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_REQUESTS_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean
    public ActiveMQDestination commonRequestsQueue() {
        LOGGER.debug("Creating Common Requests Queue Bean");

        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_COMMON_REQUESTS_QUEUE));
    }

    @Bean
    public RedeliveryPolicy commonRequestsRedeliveryPolicy() {
        LOGGER.debug("Creating Common Requests Redelivery Policy Bean");

        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.commonRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    /**
     * @return
     */
    @Bean
    public CommonRequestMessageSender commonRequestMessageSender() {
        LOGGER.debug("Creating Common Request Message Sender Bean");

        return new CommonRequestMessageSender();
    }

    // === JMS SETTINGS: COMMON RESPONSES ===

    /**
     * @return
     */
    @Bean(name = "wsCoreIncomingResponsesJmsTemplate")
    public JmsTemplate commonResponsesJmsTemplate() {
        LOGGER.debug("Creating Common Responses JMS Template Bean");

        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.commonResponsesQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_RESPONSES_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_RESPONSES_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_RESPONSES_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_RESPONSES_RECEIVE_TIMEOUT)));
        return jmsTemplate;
    }

    @Bean
    public ActiveMQDestination commonResponsesQueue() {
        LOGGER.debug("Creating Common Response Queue Bean");

        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_COMMON_RESPONSES_QUEUE));
    }

    @Bean
    public RedeliveryPolicy commonResponsesRedeliveryPolicy() {
        LOGGER.debug("Creating Common Response Redelivery Policy Bean");

        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_RESPONSES_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_RESPONSES_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_RESPONSES_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_RESPONSES_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.commonRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_RESPONSES_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_RESPONSES_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    /**
     * @return
     */
    @Bean(name = "wsCoreIncomingResponsesMessageFinder")
    public CommonResponseMessageFinder commonResponseMessageFinder() {
        LOGGER.debug("Creating Common Response Message Finder Bean");

        return new CommonResponseMessageFinder();
    }

    // === JMS SETTINGS: COMMON LOGGING ===

    @Bean
    public ActiveMQDestination commonLoggingQueue() {
        LOGGER.debug("Creating Common Logging Queue Bean");

        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_COMMON_LOGGING_QUEUE));
    }

    /**
     * @return
     */
    @Bean
    public JmsTemplate loggingJmsTemplate() {
        LOGGER.debug("Creating Common Logging JMS Template Bean");

        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.commonLoggingQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_LOGGING_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_LOGGING_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_LOGGING_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    /**
     * @return
     */
    @Bean
    public RedeliveryPolicy commonLoggingRedeliveryPolicy() {
        LOGGER.debug("Creating Common Loggging Redelivery Policy Bean");

        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_LOGGING_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_LOGGING_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_LOGGING_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_LOGGING_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.commonLoggingQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_LOGGING_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_LOGGING_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public LoggingMessageSender loggingMessageSender() {
        LOGGER.debug("Creating Common Logging Message Sender Bean");

        return new LoggingMessageSender();
    }
}
