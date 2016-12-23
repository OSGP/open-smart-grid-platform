/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.application.config;

import java.util.Map;

import javax.jms.MessageListener;

import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

public abstract class AbstractMessagingConfig extends AbstractConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMessagingConfig.class);

    // JMS Settings

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.default.redelivery.delay";

    private static final String PROPERTY_NAME_INITIAL_REDELIVERY_DELAY = "initial.redelivery.delay";
    private static final String PROPERTY_NAME_MAXIMUM_REDELIVERIES = "maximum.redeliveries";
    private static final String PROPERTY_NAME_MAXIMUM_REDELIVERY_DELAY = "maximum.redelivery.delay";
    private static final String PROPERTY_NAME_REDELIVERY_DELAY = "redelivery.delay";

    private static final String PROPERTY_NAME_EXPLICIT_QOS_ENABLED = "explicit.qos.enabled";
    private static final String PROPERTY_NAME_DELIVERY_PERSISTENT = "delivery.persistent";
    private static final String PROPERTY_NAME_TIME_TO_LIVE = "time.to.live";

    private static final String PROPERTY_NAME_CONCURRENT_CONSUMERS = "concurrent.consumers";
    private static final String PROPERTY_NAME_MAX_CONCURRENT_CONSUMERS = "max.concurrent.consumers";

    @Bean(destroyMethod = "stop")
    public PooledConnectionFactory pooledConnectionFactory() {
        LOGGER.debug("Creating bean: pooledConnectionFactory");
        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(this.connectionFactory());
        return pooledConnectionFactory;
    }

    protected ActiveMQConnectionFactory connectionFactory() {
        LOGGER.debug("Calling method: connectionFactory");
        final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setRedeliveryPolicyMap(this.redeliveryPolicyMap());
        activeMQConnectionFactory.setBrokerURL(this.getBrokerUrl());
        activeMQConnectionFactory.setNonBlockingRedelivery(true);
        return activeMQConnectionFactory;
    }

    protected abstract String getBrokerUrl();

    protected RedeliveryPolicyMap redeliveryPolicyMap() {
        LOGGER.debug("Calling method: redeliveryPolicyMap");
        final RedeliveryPolicyMap redeliveryPolicyMap = new RedeliveryPolicyMap();
        redeliveryPolicyMap.setDefaultEntry(this.defaultRedeliveryPolicy());
        final Map<ActiveMQDestination, Object> map = this.registerRedeliveryPolicies();
        for (final Map.Entry<ActiveMQDestination, Object> entry : map.entrySet()) {
            final ActiveMQDestination amd = entry.getKey();
            final Object value = entry.getValue();
            redeliveryPolicyMap.put(amd, value);
        }
        return redeliveryPolicyMap;
    }

    /**
     * This method should return a Map of redelivery policies. If there are
     * none, always return an empty map, never null.
     *
     * @return Map<ActiveMQDestination, Object>
     */
    protected abstract Map<ActiveMQDestination, Object> registerRedeliveryPolicies();

    protected RedeliveryPolicy defaultRedeliveryPolicy() {
        LOGGER.debug("Calling method: defaultRedeliveryPolicy");

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

    protected RedeliveryPolicy createCustomRedeliveryPolicy(final String prefix) {

        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(
                Long.parseLong(this.environment.getRequiredProperty(prefix + PROPERTY_NAME_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(
                Integer.parseInt(this.environment.getRequiredProperty(prefix + PROPERTY_NAME_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(
                Long.parseLong(this.environment.getRequiredProperty(prefix + PROPERTY_NAME_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(
                Long.parseLong(this.environment.getRequiredProperty(prefix + PROPERTY_NAME_REDELIVERY_DELAY)));
        return redeliveryPolicy;
    }

    protected JmsTemplate createCustomJmsTemplate(final String prefix) {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setExplicitQosEnabled(Boolean
                .parseBoolean(this.environment.getRequiredProperty(prefix + PROPERTY_NAME_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(
                Long.parseLong(this.environment.getRequiredProperty(prefix + PROPERTY_NAME_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(
                Boolean.parseBoolean(this.environment.getRequiredProperty(prefix + PROPERTY_NAME_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    protected DefaultMessageListenerContainer defaultMessageListenerContainer(final String prefix,
            final ActiveMQDestination destinationQueue, final MessageListener messageListener) {
        final DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();
        defaultMessageListenerContainer.setConnectionFactory(this.pooledConnectionFactory());
        defaultMessageListenerContainer.setDestination(destinationQueue);
        defaultMessageListenerContainer.setConcurrentConsumers(
                Integer.parseInt(this.environment.getRequiredProperty(prefix + PROPERTY_NAME_CONCURRENT_CONSUMERS)));
        defaultMessageListenerContainer.setMaxConcurrentConsumers(Integer
                .parseInt(this.environment.getRequiredProperty(prefix + PROPERTY_NAME_MAX_CONCURRENT_CONSUMERS)));
        defaultMessageListenerContainer.setMessageListener(messageListener);
        defaultMessageListenerContainer.setSessionTransacted(true);
        return defaultMessageListenerContainer;
    }

}
