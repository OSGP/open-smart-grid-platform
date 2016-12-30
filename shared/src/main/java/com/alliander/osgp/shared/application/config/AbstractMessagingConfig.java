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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * This abstract class can be used by modules to configure to Jms configuration
 * with little code. It relies heavily on the use of standard naming conventions
 * in properties files to overwrite default values, set by this class. Hence,
 * for simple default cases only the prefix value must be specified, in the
 * astract method: getJmsPropertyPrefix() and this value is then concatenated
 * with postfix keys, to retrieve this file in one of the properties defined in
 * the module's '@Configuration' section. The layout of this standard naming
 * convention is like this: <br>
 * <jmsPrefix>.<quename>.queue <jmsPrefix>.<quename>.explicit.qos.enabled
 * <jmsPrefix>.<quename>.delivery.persistent <jmsPrefix>.<quename>.time.to.live
 * <jmsPrefix>.<quename>.concurrent.consumers
 * <jmsPrefix>.<quename>.max.concurrent.consumers
 * <jmsPrefix>.<quename>.maximum.redeliveries
 * <jmsPrefix>.<quename>.initial.redelivery.delay
 * <jmsPrefix>.<quename>.redelivery.delay
 * <jmsPrefix>.<quename>.maximum.redelivery.delay
 * <jmsPrefix>.<quename>.back.off.multiplier
 * <jmsPrefix>.<quename>.use.exponential.back.off where <quename> can be:
 * requests, responses or logging Example:
 * jms.smartmetering.requests.queue=domain
 * -smartmetering.1_0.ws-smartmetering.1_0.requests
 * jms.smartmetering.requests.explicit.qos.enabled=true
 * jms.smartmetering.requests.delivery.persistent=true
 * jms.smartmetering.requests.time.to.live=3600000
 * jms.smartmetering.requests.concurrent.consumers=2
 * jms.smartmetering.requests.max.concurrent.consumers=10
 * jms.smartmetering.requests.maximum.redeliveries=3
 * jms.smartmetering.requests.initial.redelivery.delay=60000
 * jms.smartmetering.requests.redelivery.delay=60000
 * jms.smartmetering.requests.maximum.redelivery.delay=300000
 * jms.smartmetering.requests.back.off.multiplier=2
 * jms.smartmetering.requests.use.exponential.back.off=true
 *
 * The first key (that ends with 'queue='),is required, all other values are
 * optional and have default values, that are currenly maintained in
 * global.properties
 *
 * jms.default.initial.redelivery.delay=0 jms.default.maximum.redeliveries=3
 * jms.default.maximum.redelivery.delay=60000 jms.default.redelivery.delay=10000
 * jms.default.back.off.multiplier=2 jms.default.use.exponential.back.off=true
 * jms.default.explicit.qos.enabled=true jms.default.time.to.live=3600000
 * jms.default.delivery.persistent=true jms.default.concurrent.consumers=2
 * jms.default.max.concurrent.consumers=10
 *
 */
public abstract class AbstractMessagingConfig extends AbstractConfig {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractMessagingConfig.class);

    // JMS Settings
    protected static final String PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL = "jms.activemq.broker.url";

    protected static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.default.initial.redelivery.delay";
    protected static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.default.maximum.redeliveries";
    protected static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.default.maximum.redelivery.delay";
    protected static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.default.redelivery.delay";
    protected static final String PROPERTY_NAME_JMS_DEFAULT_BACK_OFF_MULTIPLIER = "jms.default.back.off.multiplier";
    protected static final String PROPERTY_NAME_JMS_DEFAULT_USE_EXPONENTIAL_BACK_OFF = "jms.default.use.exponential.back.off";
    protected static final String PROPERTY_NAME_JMS_DEFAULT_EXPLICIT_QOS_ENABLED = "jms.default.explicit.qos.enabled";
    protected static final String PROPERTY_NAME_JMS_DEFAULT_TIME_TO_LIVE = "jms.default.time.to.live";
    protected static final String PROPERTY_NAME_JMS_DEFAULT_DELIVERY_PERSISTENT = "jms.default.delivery.persistent";
    protected static final String PROPERTY_NAME_JMS_DEFAULT_CONCURRENT_CONSUMERS = "jms.default.concurrent.consumers";
    protected static final String PROPERTY_NAME_JMS_DEFAULT_MAX_CONCURRENT_CONSUMERS = "jms.default.max.concurrent.consumers";

    // the settings below are concatenated with the (abstract) prefix
    protected static final String PROPERTY_NAME_REQUESTS = "requests";
    protected static final String PROPERTY_NAME_RESPONSES = "responses";
    protected static final String PROPERTY_NAME_LOGGING = "logging";
    protected static final String PROPERTY_NAME_REQUESTS_QUEUE = "requests.queue";
    protected static final String PROPERTY_NAME_RESPONSES_QUEUE = "responses.queue";
    protected static final String PROPERTY_NAME_LOGGING_QUEUE = "logging.queue";

    protected static final String PROPERTY_NAME_INITIAL_REDELIVERY_DELAY = "initial.redelivery.delay";
    protected static final String PROPERTY_NAME_MAXIMUM_REDELIVERIES = "maximum.redeliveries";
    protected static final String PROPERTY_NAME_MAXIMUM_REDELIVERY_DELAY = "maximum.redelivery.delay";
    protected static final String PROPERTY_NAME_REDELIVERY_DELAY = "redelivery.delay";

    protected static final String PROPERTY_NAME_EXPLICIT_QOS_ENABLED = "explicit.qos.enabled";
    protected static final String PROPERTY_NAME_DELIVERY_PERSISTENT = "delivery.persistent";
    protected static final String PROPERTY_NAME_TIME_TO_LIVE = "time.to.live";

    protected static final String PROPERTY_NAME_CONCURRENT_CONSUMERS = "concurrent.consumers";
    protected static final String PROPERTY_NAME_MAX_CONCURRENT_CONSUMERS = "max.concurrent.consumers";
    protected static final String PROPERTY_NAME_TRANSACTED = "transacted";

    protected static final String DOT = ".";

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

    /**
     * For default casesm this is the only method that should be implemented by
     * the base class. All variables are set based on this prefix concatenated
     * with the constants defined above, to obtain the values from the
     * proprerties file. Most properties have default values, the folowing are
     * mandatory: pefix + PROPERTY_NAME_REQUEST_QUEUE pefix +
     * PROPERTY_NAME_RESPONSES_QUEUE pefix + PROPERTY_NAME_LOGGING_QUEUE
     *
     * @return
     */
    protected abstract String getJmsPropertyPrefix();

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
        return new ActiveMQQueue(this.getPrefixedValue(PROPERTY_NAME_REQUESTS_QUEUE));
    }

    protected ActiveMQDestination responsesQueue() {
        return new ActiveMQQueue(this.getPrefixedValue(PROPERTY_NAME_RESPONSES_QUEUE));
    }

    protected ActiveMQDestination loggingQueue() {
        return new ActiveMQQueue(this.getPrefixedValue(PROPERTY_NAME_LOGGING_QUEUE));
    }

    private RedeliveryPolicy requestsRedeliveryPolicy(final ActiveMQDestination queue) {
        final RedeliveryPolicy result = this.redeliveryPolicy(PROPERTY_NAME_REQUESTS);
        result.setDestination(queue);
        return result;
    }

    protected RedeliveryPolicy requestsRedeliveryPolicy() {
        return this.redeliveryPolicy(PROPERTY_NAME_REQUESTS);
    }

    private RedeliveryPolicy responsesRedeliveryPolicy(final ActiveMQDestination queue) {
        final RedeliveryPolicy result = this.redeliveryPolicy(PROPERTY_NAME_RESPONSES);
        result.setDestination(queue);
        return result;
    }

    protected RedeliveryPolicy responsesRedeliveryPolicy() {
        return this.redeliveryPolicy(PROPERTY_NAME_RESPONSES);
    }

    private RedeliveryPolicy loggingRedeliveryPolicy(final ActiveMQDestination queue) {
        final RedeliveryPolicy result = this.redeliveryPolicy(PROPERTY_NAME_LOGGING);
        result.setDestination(queue);
        return result;
    }

    protected RedeliveryPolicy loggingRedeliveryPolicy() {
        return this.redeliveryPolicy(PROPERTY_NAME_LOGGING);
    }

    /**
     * @param queueName
     *            this String is appended to (abstract) prefix.
     * @return
     */
    private RedeliveryPolicy redeliveryPolicy(final String queueName) {
        final RedeliveryPolicy redeliveryPolicy = this.defaultRedeliveryPolicy();

        final String initialDelay = this.getPrefixedValue(this.concatKeys(queueName,
                PROPERTY_NAME_INITIAL_REDELIVERY_DELAY));
        if (initialDelay != null && !initialDelay.isEmpty()) {
            redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(initialDelay));
        }

        final String maximum = this.getPrefixedValue(this.concatKeys(queueName, PROPERTY_NAME_MAXIMUM_REDELIVERIES));
        if (maximum != null && !maximum.isEmpty()) {
            redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(maximum));
        }

        final String maxDelay = this.getPrefixedValue(this
                .concatKeys(queueName, PROPERTY_NAME_MAXIMUM_REDELIVERY_DELAY));
        if (maxDelay != null && !maxDelay.isEmpty()) {
            redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(maxDelay));
        }

        final String delay = this.getPrefixedValue(this.concatKeys(queueName, PROPERTY_NAME_REDELIVERY_DELAY));
        if (delay != null && !delay.isEmpty()) {
            redeliveryPolicy.setRedeliveryDelay(Integer.parseInt(delay));
        }

        final String backOff = this.getPrefixedValue(this.concatKeys(queueName,
                PROPERTY_NAME_JMS_DEFAULT_BACK_OFF_MULTIPLIER));
        if (backOff != null && !backOff.isEmpty()) {
            redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(backOff));
        }

        final String exponential = this.getPrefixedValue(this.concatKeys(queueName,
                PROPERTY_NAME_JMS_DEFAULT_USE_EXPONENTIAL_BACK_OFF));
        if (exponential != null && !exponential.isEmpty()) {
            redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(exponential));
        }

        return redeliveryPolicy;
    }

    private RedeliveryPolicy defaultRedeliveryPolicy() {
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

    protected JmsTemplate jmsTemplate(final String queueName, final ActiveMQDestination destinationQueue) {
        final JmsTemplate jmsTemplate = this.defaultJmsTemplate(destinationQueue);

        final String qosEnabled = this.getPrefixedValue(this.concatKeys(queueName, PROPERTY_NAME_EXPLICIT_QOS_ENABLED));
        if (qosEnabled != null && !qosEnabled.isEmpty()) {
            jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(qosEnabled));
        }

        final String timeToLive = this.getPrefixedValue(this.concatKeys(queueName, PROPERTY_NAME_TIME_TO_LIVE));
        if (timeToLive != null && !timeToLive.isEmpty()) {
            jmsTemplate.setTimeToLive(Long.parseLong(timeToLive));
        }

        final String persistent = this.getPrefixedValue(this.concatKeys(queueName, PROPERTY_NAME_DELIVERY_PERSISTENT));
        if (persistent != null && !persistent.isEmpty()) {
            jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(persistent));
        }

        return jmsTemplate;
    }

    private JmsTemplate defaultJmsTemplate(final ActiveMQDestination destinationQueue) {
        final JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(destinationQueue);
        jmsTemplate.setExplicitQosEnabled(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_EXPLICIT_QOS_ENABLED)));
        jmsTemplate.setTimeToLive(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_TIME_TO_LIVE)));
        jmsTemplate.setDeliveryPersistent(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_DELIVERY_PERSISTENT)));
        jmsTemplate.setConnectionFactory(this.pooledConnectionFactory());
        return jmsTemplate;
    }

    protected DefaultMessageListenerContainer messageListenerContainer(final String queueName,
            final ActiveMQDestination destinationQueue, final MessageListener messageListener) {
        final DefaultMessageListenerContainer defaultMessageListenerContainer = this.defaultMessageListenerContainer(
                destinationQueue, messageListener);

        final String concurrentUsers = this.getPrefixedValue(this.concatKeys(queueName,
                PROPERTY_NAME_CONCURRENT_CONSUMERS));
        if (concurrentUsers != null && !concurrentUsers.isEmpty()) {
            defaultMessageListenerContainer.setConcurrentConsumers(Integer.parseInt(concurrentUsers));
        }

        final String maxConcurrentUsers = this.getPrefixedValue(this.concatKeys(queueName,
                PROPERTY_NAME_MAX_CONCURRENT_CONSUMERS));
        if (maxConcurrentUsers != null && !maxConcurrentUsers.isEmpty()) {
            defaultMessageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(maxConcurrentUsers));
        }

        final String transacted = this.getPrefixedValue(this.concatKeys(queueName, PROPERTY_NAME_TRANSACTED));
        if (transacted != null && !transacted.isEmpty()) {
            defaultMessageListenerContainer.setSessionTransacted(Boolean.parseBoolean(transacted));
        }

        return defaultMessageListenerContainer;
    }

    private DefaultMessageListenerContainer defaultMessageListenerContainer(final ActiveMQDestination destinationQueue,
            final MessageListener messageListener) {
        final DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();
        defaultMessageListenerContainer.setConnectionFactory(this.pooledConnectionFactory());
        defaultMessageListenerContainer.setDestination(destinationQueue);
        defaultMessageListenerContainer.setConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_CONCURRENT_CONSUMERS)));
        defaultMessageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_MAX_CONCURRENT_CONSUMERS)));
        defaultMessageListenerContainer.setMessageListener(messageListener);
        defaultMessageListenerContainer.setSessionTransacted(true);
        return defaultMessageListenerContainer;
    }

    private String concatKeys(final String key1, final String key2) {
        return this.trimDots(key1) + DOT + this.trimDots(key2);
    }

    private String trimDots(final String key) {
        String result = key;
        if (key.startsWith(DOT)) {
            result = key.substring(1);
        }
        if (result.endsWith(DOT)) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private String getPrefixedValue(final String postFix) {
        return this.environment.getProperty(this.makePrefixedKey(postFix));
    }

    private String makePrefixedKey(final String postFix) {
        return this.concatKeys(this.getJmsPropertyPrefix(), postFix);
    }

}
