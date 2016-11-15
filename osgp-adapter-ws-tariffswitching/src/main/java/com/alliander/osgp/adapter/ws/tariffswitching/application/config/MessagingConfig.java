/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.tariffswitching.application.config;

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
import com.alliander.osgp.adapter.ws.tariffswitching.infra.jms.TariffSwitchingRequestMessageSender;
import com.alliander.osgp.adapter.ws.tariffswitching.infra.jms.TariffSwitchingResponseMessageFinder;
import com.alliander.osgp.shared.application.config.AbstractConfig;

@Configuration
@PropertySources({
	@PropertySource("classpath:osgp-adapter-ws-tariffswitching.properties"),
    @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
	@PropertySource(value = "file:${osgp/AdapterWsTariffSwitching/config}", ignoreResourceNotFound = true),
})
public class MessagingConfig extends AbstractConfig {
    // JMS Settings
    private static final String PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL = "jms.activemq.broker.url";

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.default.redelivery.delay";

    // JMS Settings: Tariff Switching logging
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_QUEUE = "jms.tariffswitching.logging.queue";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_EXPLICIT_QOS_ENABLED = "jms.tariffswitching.logging.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_DELIVERY_PERSISTENT = "jms.tariffswitching.logging.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_TIME_TO_LIVE = "jms.tariffswitching.logging.time.to.live";

    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_INITIAL_REDELIVERY_DELAY = "jms.tariffswitching.logging.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_MAXIMUM_REDELIVERIES = "jms.tariffswitching.logging.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_MAXIMUM_REDELIVERY_DELAY = "jms.tariffswitching.logging.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_REDELIVERY_DELAY = "jms.tariffswitching.logging.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_BACK_OFF_MULTIPLIER = "jms.tariffswitching.logging.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_USE_EXPONENTIAL_BACK_OFF = "jms.tariffswitching.logging.use.exponential.back.off";

    // JMS Settings: Tariff Switching requests
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_QUEUE = "jms.tariffswitching.requests.queue";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_EXPLICIT_QOS_ENABLED = "jms.tariffswitching.requests.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_DELIVERY_PERSISTENT = "jms.tariffswitching.requests.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_TIME_TO_LIVE = "jms.tariffswitching.requests.time.to.live";

    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_INITIAL_REDELIVERY_DELAY = "jms.tariffswitching.requests.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_MAXIMUM_REDELIVERIES = "jms.tariffswitching.requests.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_MAXIMUM_REDELIVERY_DELAY = "jms.tariffswitching.requests.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_REDELIVERY_DELAY = "jms.tariffswitching.requests.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_BACK_OFF_MULTIPLIER = "jms.tariffswitching.requests.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_USE_EXPONENTIAL_BACK_OFF = "jms.tariffswitching.requests.use.exponential.back.off";

    // JMS Settings: Tariff Switching responses
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_QUEUE = "jms.tariffswitching.responses.queue";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_EXPLICIT_QOS_ENABLED = "jms.tariffswitching.responses.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_DELIVERY_PERSISTENT = "jms.tariffswitching.responses.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_TIME_TO_LIVE = "jms.tariffswitching.responses.time.to.live";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_RECEIVE_TIMEOUT = "jms.tariffswitching.responses.receive.timeout";

    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_INITIAL_REDELIVERY_DELAY = "jms.tariffswitching.responses.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_MAXIMUM_REDELIVERIES = "jms.tariffswitching.responses.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_MAXIMUM_REDELIVERY_DELAY = "jms.tariffswitching.responses.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_REDELIVERY_DELAY = "jms.tariffswitching.responses.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_BACK_OFF_MULTIPLIER = "jms.tariffswitching.responses.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_USE_EXPONENTIAL_BACK_OFF = "jms.tariffswitching.responses.use.exponential.back.off";

    @Resource
    private Environment environment;

    // === JMS SETTINGS ===

    @Bean
    public ActiveMQDestination tariffSwitchingLoggingQueue() {
        return new ActiveMQQueue(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_QUEUE));
    }

    /**
     * @return
     */
    @Bean
    public JmsTemplate loggingJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.tariffSwitchingLoggingQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    /**
     * @return
     */
    @Bean
    public RedeliveryPolicy tariffSwitchingLoggingRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.tariffSwitchingLoggingQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_LOGGING_USE_EXPONENTIAL_BACK_OFF)));
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
        redeliveryPolicyMap.put(this.tariffSwitchingRequestsQueue(), this.tariffSwitchingRequestsRedeliveryPolicy());
        redeliveryPolicyMap.put(this.tariffSwitchingResponsesQueue(), this.tariffSwitchingResponsesRedeliveryPolicy());
        redeliveryPolicyMap.put(this.tariffSwitchingLoggingQueue(), this.tariffSwitchingLoggingRedeliveryPolicy());
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

    // === JMS SETTINGS: TARIFF SWITCHING REQUESTS ===

    @Bean
    public ActiveMQDestination tariffSwitchingRequestsQueue() {
        return new ActiveMQQueue(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_QUEUE));
    }

    /**
     * @return
     */
    @Bean(name = "wsTariffSwitchingOutgoingRequestsJmsTemplate")
    public JmsTemplate tariffSwitchingRequestsJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(this.tariffSwitchingRequestsQueue());
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    @Bean
    public RedeliveryPolicy tariffSwitchingRequestsRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.tariffSwitchingRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_REQUESTS_USE_EXPONENTIAL_BACK_OFF)));

        return redeliveryPolicy;
    }

    /**
     * @return
     */
    @Bean
    public TariffSwitchingRequestMessageSender tariffSwitchingRequestMessageSender() {
        return new TariffSwitchingRequestMessageSender();
    }

    // === JMS SETTINGS: TARIFF SWITCHING RESPONSES ===

    @Bean
    public ActiveMQDestination tariffSwitchingResponsesQueue() {
        return new ActiveMQQueue(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_QUEUE));
    }

    /**
     * @return
     */
    @Bean(name = "wsTariffSwitchingIncomingResponsesJmsTemplate")
    public JmsTemplate tariffSwitchingResponsesJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(new ActiveMQQueue(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_QUEUE)));
        // Enable the use of deliveryMode, priority, and timeToLive
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        jmsTemplate.setReceiveTimeout(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_RECEIVE_TIMEOUT)));
        return jmsTemplate;
    }

    @Bean
    public RedeliveryPolicy tariffSwitchingResponsesRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_REDELIVERY_DELAY)));
        redeliveryPolicy.setDestination(this.tariffSwitchingRequestsQueue());
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_TARIFF_SWITCHING_RESPONSES_USE_EXPONENTIAL_BACK_OFF)));
        return redeliveryPolicy;
    }

    /**
     * @return
     */
    @Bean(name = "wsTariffSwitchingIncomingResponsesMessageFinder")
    public TariffSwitchingResponseMessageFinder tariffSwitchingResponseMessageFinder() {
        return new TariffSwitchingResponseMessageFinder();
    }

    @Bean
    public LoggingMessageSender loggingMessageSender() {
        return new LoggingMessageSender();
    }
}
