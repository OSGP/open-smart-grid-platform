/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.admin.application.config;

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
import org.springframework.jms.core.JmsTemplate;

import com.alliander.osgp.adapter.ws.admin.infra.jms.AdminRequestMessageSender;
import com.alliander.osgp.adapter.ws.admin.infra.jms.AdminResponseMessageFinder;
import com.alliander.osgp.adapter.ws.infra.jms.LoggingMessageSender;
import com.alliander.osgp.shared.application.config.AbstractConfig;

@Configuration
@PropertySources({
	@PropertySource("classpath:osgp-adapter-ws-admin.properties"),
    @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
	@PropertySource(value = "file:${osgp/AdapterWsAdmin/config}", ignoreResourceNotFound = true),
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
    private static final String PROPERTY_NAME_JMS_ADMIN_REQUESTS_QUEUE = "jms.admin.requests.queue";
    private static final String PROPERTY_NAME_JMS_ADMIN_REQUESTS_EXPLICIT_QOS_ENABLED = "jms.admin.requests.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_ADMIN_REQUESTS_DELIVERY_PERSISTENT = "jms.admin.requests.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_ADMIN_REQUESTS_TIME_TO_LIVE = "jms.admin.requests.time.to.live";

    private static final String PROPERTY_NAME_JMS_ADMIN_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.admin.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_ADMIN_REQUESTS_MAXIMUM_REDELIVERIES = "jms.admin.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_ADMIN_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.admin.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_ADMIN_REQUESTS_REDELIVERY_DELAY = "jms.admin.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_ADMIN_REQUESTS_BACK_OFF_MULTIPLIER = "jms.admin.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_ADMIN_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.admin.requests.use.exponential.back.off";

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
    private static final String PROPERTY_NAME_JMS_ADMIN_RESPONSES_QUEUE = "jms.admin.responses.queue";
    private static final String PROPERTY_NAME_JMS_ADMIN_RESPONSES_EXPLICIT_QOS_ENABLED = "jms.admin.responses.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_ADMIN_RESPONSES_DELIVERY_PERSISTENT = "jms.admin.responses.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_ADMIN_RESPONSES_TIME_TO_LIVE = "jms.admin.responses.time.to.live";
    private static final String PROPERTY_NAME_JMS_ADMIN_RESPONSES_RECEIVE_TIMEOUT = "jms.admin.responses.receive.timeout";

    private static final String PROPERTY_NAME_JMS_ADMIN_RESPONSES_INITIAL_REDELIVERY_DELAY = "jms.admin.responses.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_ADMIN_RESPONSES_MAXIMUM_REDELIVERIES = "jms.admin.responses.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_ADMIN_RESPONSES_MAXIMUM_REDELIVERY_DELAY = "jms.admin.responses.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_ADMIN_RESPONSES_REDELIVERY_DELAY = "jms.admin.responses.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_ADMIN_RESPONSES_BACK_OFF_MULTIPLIER = "jms.admin.responses.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_ADMIN_RESPONSES_USE_EXPONENTIAL_BACK_OFF = "jms.admin.responses.use.exponential.back.off";

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
        LOGGER.debug("Creating Admin Redelivery Policy Map Bean");

        final RedeliveryPolicyMap redeliveryPolicyMap = new RedeliveryPolicyMap();
        redeliveryPolicyMap.setDefaultEntry(this.defaultRedeliveryPolicy());
        redeliveryPolicyMap.put(this.adminRequestsQueue(), this.adminRequestsRedeliveryPolicy());
        redeliveryPolicyMap.put(this.adminResponsesQueue(), this.adminResponsesRedeliveryPolicy());
        redeliveryPolicyMap.put(this.adminLoggingQueue(), this.adminLoggingRedeliveryPolicy());
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

    // === JMS SETTINGS: ADMIN REQUESTS ===

    @Bean(name = "wsAdminOutgoingRequestsJmsTemplate")
    public JmsTemplate adminRequestsJmsTemplate() {
        LOGGER.debug("Creating Admin Requests JMS Template Bean");

        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.adminRequestsQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_REQUESTS_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_REQUESTS_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_REQUESTS_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean
    public ActiveMQDestination adminRequestsQueue() {
        LOGGER.debug("Creating Admin Requests Queue Bean");

        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_REQUESTS_QUEUE));
    }

    @Bean
    public RedeliveryPolicy adminRequestsRedeliveryPolicy() {
        LOGGER.debug("Creating Admin Requests Redelivery Policy Bean");

        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.adminRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public AdminRequestMessageSender adminRequestMessageSender() {
        LOGGER.debug("Creating Admin Requests Message Sender Bean");

        return new AdminRequestMessageSender();
    }

    // === JMS SETTINGS: ADMIN RESPONSES ===

    @Bean(name = "wsAdminIncomingResponsesJmsTemplate")
    public JmsTemplate adminResponsesJmsTemplate() {
        LOGGER.debug("Creating Admin Responses JMS Template Bean");

        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.adminResponsesQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_RESPONSES_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_RESPONSES_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_RESPONSES_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_RESPONSES_RECEIVE_TIMEOUT)));
        return jmsTemplate;
    }

    @Bean
    public ActiveMQDestination adminResponsesQueue() {
        LOGGER.debug("Creating Admin Responses Queue Bean");

        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_RESPONSES_QUEUE));
    }

    @Bean
    public RedeliveryPolicy adminResponsesRedeliveryPolicy() {
        LOGGER.debug("Creating Admin Responses Redelivery Policy Bean");

        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_RESPONSES_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_RESPONSES_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_RESPONSES_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_RESPONSES_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.adminRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_RESPONSES_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ADMIN_RESPONSES_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public AdminResponseMessageFinder adminResponseMessageFinder() {
        LOGGER.debug("Creating Admin Response Message Finder Bean");

        return new AdminResponseMessageFinder();
    }

    // === JMS SETTINGS: ADMIN LOGGING ===

    @Bean
    public ActiveMQDestination adminLoggingQueue() {
        LOGGER.debug("Creating Admin Logging JMS Queue Bean");

        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_COMMON_LOGGING_QUEUE));
    }

    @Bean
    public JmsTemplate loggingJmsTemplate() {
        LOGGER.debug("Creating Admin Logging JMS Template Bean");

        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.adminLoggingQueue());
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

    @Bean
    public RedeliveryPolicy adminLoggingRedeliveryPolicy() {
        LOGGER.debug("Creating Admin Logging Redelivery Policy Bean");

        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_LOGGING_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_LOGGING_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_LOGGING_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_LOGGING_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.adminLoggingQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_LOGGING_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_COMMON_LOGGING_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public LoggingMessageSender loggingMessageSender() {
        LOGGER.debug("Creating Admin Logging Message Sender Bean");

        return new LoggingMessageSender();
    }
}
