/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.application.config;

import javax.jms.MessageListener;

import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * This abstract class can be used by modules to configure to Jms configuration
 * with little code. The base class only needs to provide the names of the
 * request / responses / logging queue the rest is filled in by this class with
 * default values. The base class can overwrite these values, that can be found
 * in jms.properties, by providing its own in value in the corresponding
 * properties file.
 */
@Configuration
@PropertySources({ @PropertySource("classpath:jms.properties"),
    @PropertySource(value = "file:/etc/osp/jms.properties", ignoreResourceNotFound = true) })
public abstract class AbstractMessagingConfig extends AbstractConfig {

    @Value("${jms.requests.explicit.qos.enabled}")
    public boolean requestQosEnabled;

    @Value("${jms.requests.delivery.persistent}")
    public boolean requestDeliveryPersistent;

    @Value("${jms.requests.time.to.live}")
    public long requestTimeToLive;

    @Value("${jms.requests.concurrent.consumers}")
    public int requestConcurrentConsumers;

    @Value("${jms.requests.max.concurrent.consumers}")
    public int requestMaxConcurrentConsumers;

    @Value("${jms.requests.maximum.redeliveries}")
    public int requestMaxRedeliveries;

    @Value("${jms.requests.initial.redelivery.delay}")
    public long requestInitialRedeliveryDelay;

    @Value("${jms.requests.redelivery.delay}")
    public int requestRedeliveryDelay;

    @Value("${jms.requests.maximum.redelivery.delay}")
    public long requestMaxRedeliveryDelay;

    @Value("${jms.requests.back.off.multiplier}")
    public long requestBackOffMultiplier;

    @Value("${jms.requests.use.exponential.back.off}")
    public boolean requestUseExpBackOff;

    @Value("${jms.responses.explicit.qos.enabled}")
    public boolean responsesQosEnabled;

    @Value("${jms.responses.delivery.persistent}")
    public boolean responsesDeliveryPersistent;

    @Value("${jms.responses.time.to.live}")
    public long responsesTimeToLive;

    @Value("${jms.responses.concurrent.consumers}")
    public int responsesConcurrentConsumers;

    @Value("${jms.responses.max.concurrent.consumers}")
    public int responsesMaxConcurrentConsumers;

    @Value("${jms.responses.maximum.redeliveries}")
    public int responsesMaxRedeliveries;

    @Value("${jms.responses.initial.redelivery.delay}")
    public long responsesInitialRedeliveryDelay;

    @Value("${jms.responses.redelivery.delay}")
    public int responsesRedeliveryDelay;

    @Value("${jms.responses.maximum.redelivery.delay}")
    public long responsesMaxRedeliveryDelay;

    @Value("${jms.responses.back.off.multiplier}")
    public long responsesBackOffMultiplier;

    @Value("${jms.responses.use.exponential.back.off}")
    public boolean responsesUseExpBackOff;

    @Value("${jms.logging.explicit.qos.enabled}")
    public boolean loggingQosEnabled;

    @Value("${jms.logging.delivery.persistent}")
    public boolean loggingDeliveryPersistent;

    @Value("${jms.logging.time.to.live}")
    public long loggingTimeToLive;

    @Value("${jms.logging.concurrent.consumers}")
    public int loggingConcurrentConsumers;

    @Value("${jms.logging.max.concurrent.consumers}")
    public int loggingMaxConcurrentConsumers;

    @Value("${jms.logging.maximum.redeliveries}")
    public int loggingMaxRedeliveries;

    @Value("${jms.logging.initial.redelivery.delay}")
    public long loggingInitialRedeliveryDelay;

    @Value("${jms.logging.redelivery.delay}")
    public int loggingRedeliveryDelay;

    @Value("${jms.logging.maximum.redelivery.delay}")
    public long loggingMaxRedeliveryDelay;

    @Value("${jms.logging.back.off.multiplier}")
    public long loggingBackOffMultiplier;

    @Value("${jms.logging.use.exponential.back.off}")
    public boolean loggingUseExpBackOff;

    protected static final String PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL = "jms.activemq.broker.url";

    @Bean(destroyMethod = "stop")
    public PooledConnectionFactory pooledConnectionFactory() {
        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(this.connectionFactory());
        return pooledConnectionFactory;
    }

    protected ActiveMQConnectionFactory connectionFactory() {
        final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setRedeliveryPolicyMap(this.redeliveryPolicyMap());
        activeMQConnectionFactory.setBrokerURL(this.getBrokerUrl());
        activeMQConnectionFactory.setNonBlockingRedelivery(true);
        return activeMQConnectionFactory;
    }

    protected abstract String getRequestQueueName();

    protected abstract String getResponsesQueueName();

    protected abstract String getLoggingQueueName();

    protected String getBrokerUrl() {
        return this.environment.getRequiredProperty(PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL);
    }

    protected RedeliveryPolicyMap redeliveryPolicyMap() {
        final RedeliveryPolicyMap redeliveryPolicyMap = new RedeliveryPolicyMap();
        redeliveryPolicyMap.put(this.requestsQueue(), this.requestsRedeliveryPolicy(this.requestsQueue()));
        redeliveryPolicyMap.put(this.responsesQueue(), this.responsesRedeliveryPolicy(this.responsesQueue()));
        redeliveryPolicyMap.put(this.loggingQueue(), this.loggingRedeliveryPolicy(this.loggingQueue()));
        return redeliveryPolicyMap;
    }

    protected ActiveMQDestination requestsQueue() {
        return new ActiveMQQueue(this.getRequestQueueName());
    }

    protected ActiveMQDestination responsesQueue() {
        return new ActiveMQQueue(this.getResponsesQueueName());
    }

    protected ActiveMQDestination loggingQueue() {
        return new ActiveMQQueue(this.getLoggingQueueName());
    }

    protected RedeliveryPolicy requestsRedeliveryPolicy(final ActiveMQDestination queue) {
        return this.redeliveryPolicy(queue, this.requestInitialRedeliveryDelay, this.requestRedeliveryDelay,
                this.requestMaxRedeliveryDelay, this.requestRedeliveryDelay, this.requestBackOffMultiplier,
                this.requestUseExpBackOff);
    }

    private RedeliveryPolicy responsesRedeliveryPolicy(final ActiveMQDestination queue) {
        return this.redeliveryPolicy(queue, this.responsesInitialRedeliveryDelay, this.responsesRedeliveryDelay,
                this.responsesMaxRedeliveryDelay, this.responsesRedeliveryDelay, this.responsesBackOffMultiplier,
                this.responsesUseExpBackOff);
    }

    private RedeliveryPolicy loggingRedeliveryPolicy(final ActiveMQDestination queue) {
        return this.redeliveryPolicy(queue, this.loggingInitialRedeliveryDelay, this.loggingRedeliveryDelay,
                this.loggingMaxRedeliveryDelay, this.loggingRedeliveryDelay, this.loggingBackOffMultiplier,
                this.loggingUseExpBackOff);
    }

    protected RedeliveryPolicy redeliveryPolicy(final ActiveMQDestination queue, final long initialRedeliveryDelay,
            final int maxRedeliveries, final long maxRedeliveryDelay, final long redeliveryDelay,
            final long backOffMultiplier, final boolean useExpBackOff) {

        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setDestination(queue);
        redeliveryPolicy.setInitialRedeliveryDelay(initialRedeliveryDelay);
        redeliveryPolicy.setMaximumRedeliveries(maxRedeliveries);
        redeliveryPolicy.setMaximumRedeliveryDelay(maxRedeliveryDelay);
        redeliveryPolicy.setRedeliveryDelay(redeliveryDelay);
        redeliveryPolicy.setBackOffMultiplier(backOffMultiplier);
        redeliveryPolicy.setUseExponentialBackOff(useExpBackOff);
        return redeliveryPolicy;
    }

    protected JmsTemplate jmsRequestTemplate() {
        return this.jmsTemplate(this.requestsQueue(), this.requestQosEnabled, this.requestTimeToLive,
                this.requestDeliveryPersistent);
    }

    protected JmsTemplate jmsResponsesTemplate() {
        return this.jmsTemplate(this.responsesQueue(), this.responsesQosEnabled, this.responsesTimeToLive,
                this.responsesDeliveryPersistent);
    }

    protected JmsTemplate jmsLoggingTemplate() {
        return this.jmsTemplate(this.loggingQueue(), this.loggingQosEnabled, this.loggingTimeToLive,
                this.loggingDeliveryPersistent);
    }

    private JmsTemplate jmsTemplate(final ActiveMQDestination destinationQueue, final boolean qosEnabled,
            final long timeToLive, final boolean deliveryPersistent) {

        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(destinationQueue);
        jmsTemplate.setExplicitQosEnabled(qosEnabled);
        jmsTemplate.setTimeToLive(timeToLive);
        jmsTemplate.setDeliveryPersistent(deliveryPersistent);
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    protected DefaultMessageListenerContainer defaultRequestMessageListenerContainer(
            final MessageListener messageListener) {
        return this.defaultMessageListenerContainer(this.requestsQueue(), messageListener,
                this.requestConcurrentConsumers, this.requestMaxConcurrentConsumers);
    }

    protected DefaultMessageListenerContainer defaultResponsesMessageListenerContainer(
            final MessageListener messageListener) {
        return this.defaultMessageListenerContainer(this.responsesQueue(), messageListener,
                this.responsesConcurrentConsumers, this.responsesConcurrentConsumers);
    }

    protected DefaultMessageListenerContainer defaultLoggingMessageListenerContainer(
            final MessageListener messageListener) {
        return this.defaultMessageListenerContainer(this.loggingQueue(), messageListener,
                this.loggingConcurrentConsumers, this.loggingMaxConcurrentConsumers);
    }

    private DefaultMessageListenerContainer defaultMessageListenerContainer(final ActiveMQDestination destination,
            final MessageListener messageListener, final int concConsumers, final int maxConcConsumers) {

        final DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();
        defaultMessageListenerContainer.setConnectionFactory(this.pooledConnectionFactory());
        defaultMessageListenerContainer.setDestination(destination);
        defaultMessageListenerContainer.setMessageListener(messageListener);
        defaultMessageListenerContainer.setConcurrentConsumers(concConsumers);
        defaultMessageListenerContainer.setMaxConcurrentConsumers(maxConcConsumers);
        defaultMessageListenerContainer.setSessionTransacted(true);
        return defaultMessageListenerContainer;
    }

}
