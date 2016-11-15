/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.logging.application.config;

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
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.alliander.osgp.logging.infra.jms.ProtocolLogItemRequestMessageListener;
import com.alliander.osgp.shared.application.config.AbstractConfig;

@Configuration
@PropertySources({
	@PropertySource("classpath:osgp-logging.properties"),
	@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
    @PropertySource(value = "file:${osgp/Logging/config}", ignoreResourceNotFound = true),
})
public class ProtocolMessagingConfig extends AbstractConfig {

    // JMS Settings
    private static final String PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL = "jms.protocol.activemq.broker.url";

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.protocol.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.protocol.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.protocol.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.protocol.default.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_BACK_OFF_MULTIPLIER = "jms.protocol.default.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_DEFAULT_USE_EXPONENTIAL_BACK_OFF = "jms.protocol.default.use.exponential.back.off";

    // JMS Settings: Incoming protocol log item requests (receive)
    private static final String PROPERTY_NAME_JMS_INCOMING_PROTOCOL_LOG_ITEM_REQUESTS_QUEUE = "jms.incoming.protocol.log.item.requests.queue";
    private static final String PROPERTY_NAME_JMS_INCOMING_PROTOCOL_LOG_ITEM_REQUESTS_CONCURRENT_CONSUMERS = "jms.incoming.protocol.log.item.requests.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_INCOMING_PROTOCOL_LOG_ITEM_REQUESTS_MAX_CONCURRENT_CONSUMERS = "jms.incoming.protocol.log.item.requests.max.concurrent.consumers";

    private static final String PROPERTY_NAME_MAX_RETRY_COUNT = "max.retry.count";

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolMessagingConfig.class);

    @Bean(destroyMethod = "stop")
    public PooledConnectionFactory protocolPooledConnectionFactory() {
        LOGGER.debug("Creating bean: pooledConnectionFactory");

        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(this.protocolConnectionFactory());
        return pooledConnectionFactory;
    }

    @Bean
    public ActiveMQConnectionFactory protocolConnectionFactory() {
        LOGGER.debug("Creating bean: connectionFactory");

        final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setRedeliveryPolicyMap(this.protocolRedeliveryPolicyMap());
        activeMQConnectionFactory.setBrokerURL(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL));

        activeMQConnectionFactory.setNonBlockingRedelivery(true);

        return activeMQConnectionFactory;
    }

    @Bean
    public RedeliveryPolicyMap protocolRedeliveryPolicyMap() {
        LOGGER.debug("Creating bean: redeliveryPolicyMap");

        final RedeliveryPolicyMap redeliveryPolicyMap = new RedeliveryPolicyMap();
        redeliveryPolicyMap.setDefaultEntry(this.defaultProtocolRedeliveryPolicy());
        return redeliveryPolicyMap;
    }

    @Bean
    public RedeliveryPolicy defaultProtocolRedeliveryPolicy() {
        LOGGER.debug("Creating bean: defaultRedeliveryPolicy");

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

    // === INCOMING PROTOCOL LOG ITEM REQUESTS ===
    // beans used for receiving protocol log item request messages

    @Bean
    public ActiveMQDestination protocolLogItemRequestsQueue() {
        return new ActiveMQQueue(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_PROTOCOL_LOG_ITEM_REQUESTS_QUEUE));
    }

    @Bean
    public DefaultMessageListenerContainer protocolLogItemRequestsMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(this.protocolPooledConnectionFactory());
        messageListenerContainer.setDestination(this.protocolLogItemRequestsQueue());
        messageListenerContainer.setConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_PROTOCOL_LOG_ITEM_REQUESTS_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_PROTOCOL_LOG_ITEM_REQUESTS_MAX_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMessageListener(this.protocolLogItemRequestMessageListener());
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    @Bean
    public ProtocolLogItemRequestMessageListener protocolLogItemRequestMessageListener() {
        return new ProtocolLogItemRequestMessageListener();
    }

    // The Max count to retry a failed response

    @Bean
    public int getMaxRetryCount() {
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_MAX_RETRY_COUNT));
    }
}
