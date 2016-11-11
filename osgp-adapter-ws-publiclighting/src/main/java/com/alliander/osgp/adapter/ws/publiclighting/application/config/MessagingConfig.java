/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.publiclighting.application.config;

import javax.annotation.Resource;

import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

import com.alliander.osgp.adapter.ws.infra.jms.LoggingMessageSender;
import com.alliander.osgp.adapter.ws.publiclighting.infra.jms.PublicLightingRequestMessageSender;
import com.alliander.osgp.adapter.ws.publiclighting.infra.jms.PublicLightingResponseMessageFinder;
import com.alliander.osgp.shared.application.config.AbstractConfig;

@Configuration
@PropertySources({
	@PropertySource("classpath:osgp-adapter-ws-publiclighting.properties"),
    @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
	@PropertySource(value = "file:${osgp/AdapterWsPublicLighting/config}", ignoreResourceNotFound = true),
})
public class MessagingConfig extends AbstractConfig {

    @Resource
    private Environment environment;

    // JMS Settings
    private static final String PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL = "jms.activemq.broker.url";

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.default.redelivery.delay";

    // JMS Settings: Public Lighting logging
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_QUEUE = "jms.publiclighting.logging.queue";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_EXPLICIT_QOS_ENABLED = "jms.publiclighting.logging.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_DELIVERY_PERSISTENT = "jms.publiclighting.logging.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_TIME_TO_LIVE = "jms.publiclighting.logging.time.to.live";

    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_INITIAL_REDELIVERY_DELAY = "jms.publiclighting.logging.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_MAXIMUM_REDELIVERIES = "jms.publiclighting.logging.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_MAXIMUM_REDELIVERY_DELAY = "jms.publiclighting.logging.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_REDELIVERY_DELAY = "jms.publiclighting.logging.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_BACK_OFF_MULTIPLIER = "jms.publiclighting.logging.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_USE_EXPONENTIAL_BACK_OFF = "jms.publiclighting.logging.use.exponential.back.off";

    // JMS Settings: Public Lighting requests
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_QUEUE = "jms.publiclighting.requests.queue";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_EXPLICIT_QOS_ENABLED = "jms.publiclighting.requests.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_TIME_TO_LIVE = "jms.publiclighting.responses.time.to.live";

    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_DELIVERY_PERSISTENT = "jms.publiclighting.requests.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_TIME_TO_LIVE = "jms.publiclighting.requests.time.to.live";

    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.publiclighting.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_MAXIMUM_REDELIVERIES = "jms.publiclighting.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.publiclighting.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_REDELIVERY_DELAY = "jms.publiclighting.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_BACK_OFF_MULTIPLIER = "jms.publiclighting.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.publiclighting.requests.use.exponential.back.off";

    // JMS Settings: Public Lighting responses
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_QUEUE = "jms.publiclighting.responses.queue";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_EXPLICIT_QOS_ENABLED = "jms.publiclighting.responses.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_DELIVERY_PERSISTENT = "jms.publiclighting.responses.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_RECEIVE_TIMEOUT = "jms.publiclighting.responses.receive.timeout";

    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_INITIAL_REDELIVERY_DELAY = "jms.publiclighting.responses.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_MAXIMUM_REDELIVERIES = "jms.publiclighting.responses.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_MAXIMUM_REDELIVERY_DELAY = "jms.publiclighting.responses.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_REDELIVERY_DELAY = "jms.publiclighting.responses.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_BACK_OFF_MULTIPLIER = "jms.publiclighting.responses.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_USE_EXPONENTIAL_BACK_OFF = "jms.publiclighting.responses.use.exponential.back.off";

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
        redeliveryPolicyMap.put(this.publicLightingRequestsQueue(), this.publicLightingRequestsRedeliveryPolicy());
        redeliveryPolicyMap.put(this.publicLightingResponsesQueue(), this.publicLightingResponsesRedeliveryPolicy());
        redeliveryPolicyMap.put(this.publicLightingLoggingQueue(), this.publicLightingLoggingRedeliveryPolicy());
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

    // === JMS SETTINGS: PUBLIC LIGHTING REQUESTS ===

    @Bean
    public ActiveMQDestination publicLightingRequestsQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_QUEUE));
    }

    /**
     * @return
     */
    @Bean(name = "wsPublicLightingOutgoingRequestsJmsTemplate")
    public JmsTemplate publicLightingRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.publicLightingRequestsQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean
    public RedeliveryPolicy publicLightingRequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.publicLightingRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));

        return redeliveryPolicy;
    }

    /**
     * @return
     */
    @Bean(name = "wsPublicLightingOutgoingRequestsMessageSender")
    public PublicLightingRequestMessageSender publicLightingRequestMessageSender() {
        return new PublicLightingRequestMessageSender();
    }

    // === JMS SETTINGS: PUBLIC LIGHTING RESPONSES ===

    /**
     * @return
     */
    @Bean(name = "wsPublicLightingIncomingResponsesJmsTemplate")
    public JmsTemplate publicLightingResponsesJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.publicLightingResponsesQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_RECEIVE_TIMEOUT)));
        return jmsTemplate;
    }

    @Bean
    public ActiveMQDestination publicLightingResponsesQueue() {
        return new ActiveMQQueue(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_QUEUE));
    }

    @Bean
    public RedeliveryPolicy publicLightingResponsesRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.publicLightingRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_RESPONSES_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    /**
     * @return
     */
    @Bean(name = "wsPublicLightingIncomingResponsesMessageFinder")
    public PublicLightingResponseMessageFinder publicLightingResponseMessageFinder() {
        return new PublicLightingResponseMessageFinder();
    }

    // === JMS SETTINGS: PUBLIC LIGHTING LOGGING ===

    @Bean
    public ActiveMQDestination publicLightingLoggingQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_QUEUE));
    }

    /**
     * @return
     */
    @Bean
    public JmsTemplate loggingJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.publicLightingLoggingQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    /**
     * @return
     */
    @Bean
    public RedeliveryPolicy publicLightingLoggingRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.publicLightingLoggingQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_PUBLIC_LIGHTING_LOGGING_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    @Bean
    public LoggingMessageSender loggingMessageSender() {
        return new LoggingMessageSender();
    }

}
