/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.application.config.jms;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.SessionAwareMessageListener;

/**
 * Factory object for creating and initializing JMS configuration objects with
 * properties from the Environment.
 *
 * A {@link JmsConfiguration} will be returned containing the created instances.
 * This class can be used to retrieve the instances and expose them as Beans.
 *
 * Properties are located by their prefix. If a property is not found, a default
 * prefix will be tried.
 *
 */
public class JmsConfigurationFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmsConfigurationFactory.class);

    private final Environment environment;

    private final PooledConnectionFactory pooledConnectionFactory;

    private final RedeliveryPolicyMap redeliveryPolicyMap;

    /**
     *
     * @param environment
     *            Environment to retrieve the properties from.
     * @param pooledConnectionFactory
     *            Created objects will be linked to this connection factory.
     * @param redeliveryPolicyMap
     *            Created redelivery policy will be added to this map.
     */
    public JmsConfigurationFactory(final Environment environment,
            final PooledConnectionFactory pooledConnectionFactory, final RedeliveryPolicyMap redeliveryPolicyMap) {
        this.environment = environment;
        this.pooledConnectionFactory = pooledConnectionFactory;
        this.redeliveryPolicyMap = redeliveryPolicyMap;
    }

    /**
     * Initialize configuration.
     *
     * @param propertyPrefix
     *            Prefix for all properties.
     * @return JmsConfiguration containing created objects.
     */
    public JmsConfiguration initializeConfiguration(final String propertyPrefix) {
        return new JmsConfigurationCreator<>(propertyPrefix).create();
    }

    /**
     * Initialize configuration for JmsTemplate and MessageListenerContainer.
     *
     * @param propertyPrefix
     *            Prefix for all properties.
     * @param messageListener
     *            The {@link MessageListener} to put on the queue.
     * @return JmsConfiguration containing created objects.
     */
    public JmsConfiguration initializeConfiguration(final String propertyPrefix, final MessageListener messageListener) {
        return new JmsConfigurationCreator<>(propertyPrefix, messageListener).create();
    }

    /**
     * Initialize configuration for MessageListenerContainer.
     *
     * @param propertyPrefix
     *            Prefix for all properties.
     * @param messageListener
     *            The {@link SessionAwareMessageListener} to put on the queue.
     * @return JmsConfiguration containing created objects.
     */
    public JmsConfiguration initializeReceiveConfiguration(final String propertyPrefix,
            final SessionAwareMessageListener<Message> messageListener) {
        return new JmsConfigurationCreator<>(propertyPrefix, messageListener).createReceiveConfiguration();
    }

    /**
     * Initialize configuration for MessageListenerContainer.
     *
     * @param propertyPrefix
     *            Prefix for all properties.
     * @param messageListener
     *            The {@link MessageListener} to put on the queue.
     * @return JmsConfiguration containing created objects.
     */
    public JmsConfiguration initializeReceiveConfiguration(final String propertyPrefix,
            final MessageListener messageListener) {
        return new JmsConfigurationCreator<>(propertyPrefix, messageListener).createReceiveConfiguration();
    }

    /**
     * Initialize configuration for MessageListenerContainer.
     *
     * @param propertyPrefix
     *            Prefix for all properties.
     * @param messageListener
     *            The {@link MessageListener} to put on the queue.
     * @param replyToQueue
     *            {@link ActiveMQDestination} instance where reply messages
     *            should be sent.
     * @return JmsConfiguration containing created objects.
     */
    public JmsConfiguration initializeReceiveConfiguration(final String propertyPrefix,
            final MessageListener messageListener, final ActiveMQDestination replyToQueue) {
        return new JmsConfigurationCreator<>(propertyPrefix, messageListener, replyToQueue)
                .createReceiveConfiguration();
    }

    private class JmsConfigurationCreator<V> {

        private static final String PROPERTY_MAX_CONCURRENT_CONSUMERS = "max.concurrent.consumers";
        private static final String PROPERTY_CONCURRENT_CONSUMERS = "concurrent.consumers";
        private static final String PROPERTY_USE_EXPONENTIAL_BACK_OFF = "use.exponential.back.off";
        private static final String PROPERTY_BACK_OFF_MULTIPLIER = "back.off.multiplier";
        private static final String PROPERTY_MAXIMUM_REDELIVERY_DELAY = "maximum.redelivery.delay";
        private static final String PROPERTY_INITIAL_REDELIVERY_DELAY = "initial.redelivery.delay";
        private static final String PROPERTY_MAXIMUM_REDELIVERIES = "maximum.redeliveries";
        private static final String PROPERTY_REDELIVERY_DELAY = "redelivery.delay";
        private static final String PROPERTY_DELIVERY_PERSISTENT = "delivery.persistent";
        private static final String PROPERTY_TIME_TO_LIVE = "time.to.live";
        private static final String PROPERTY_RECEIVE_TIMEOUT = "receive.timeout";
        private static final String PROPERTY_EXPLICIT_QOS_ENABLED = "explicit.qos.enabled";
        private static final String PROPERTY_QUEUE = "queue";

        private static final String JMS_DEFAULT = "jms.default";

        private final String propertyPrefix;

        private final ActiveMQDestination destinationQueue;

        private final V messageListener;

        public JmsConfigurationCreator(final String propertyPrefix, final V messageListener,
                final ActiveMQDestination replyToQueue) {
            this.propertyPrefix = propertyPrefix;
            this.destinationQueue = replyToQueue;
            this.messageListener = messageListener;
        }

        public JmsConfigurationCreator(final String propertyPrefix, final V messageListener) {
            this.propertyPrefix = propertyPrefix;
            this.destinationQueue = new ActiveMQQueue(this.property(PROPERTY_QUEUE, String.class));
            this.messageListener = messageListener;
        }

        public JmsConfigurationCreator(final String propertyPrefix) {
            this(propertyPrefix, null);
        }

        /**
         * Create a JmsConfiguration with only a redeliveryPolicy and
         * MessageListenerContainer and no JmsTemplate.
         */
        public JmsConfiguration createReceiveConfiguration() {
            final JmsConfiguration configuration = new JmsConfiguration();
            configuration.setRedeliveryPolicy(this.redeliveryPolicy());
            if (this.messageListener != null) {
                configuration.setMessageListenerContainer(this.messageListenerContainer());
            }
            return configuration;

        }

        public JmsConfiguration create() {
            final JmsConfiguration configuration = new JmsConfiguration();
            configuration.setJmsTemplate(this.jmsTemplate());
            configuration.setRedeliveryPolicy(this.redeliveryPolicy());
            if (this.messageListener != null) {
                configuration.setMessageListenerContainer(this.messageListenerContainer());
            }
            return configuration;
        }

        private <T> T property(final String propertyName, final Class<T> targetType) {
            try {
                LOGGER.info("Trying to find property {}.{}", this.propertyPrefix, propertyName);
                T property = JmsConfigurationFactory.this.environment.getProperty(this.propertyPrefix + "."
                        + propertyName, targetType);

                if (property == null) {
                    LOGGER.info("Property {}.{} not found, trying default property.", this.propertyPrefix, propertyName);
                    property = this.fallbackProperty(propertyName, targetType);
                }

                return property;
            } catch (final NoSuchFieldError e) {
                LOGGER.warn("Exception while retrieving field " + propertyName, e);
                return this.fallbackProperty(propertyName, targetType);
            }
        }

        private <T> T fallbackProperty(final String propertyName, final Class<T> targetType) {
            try {
                return JmsConfigurationFactory.this.environment.getRequiredProperty(JMS_DEFAULT + "." + propertyName,
                        targetType);
            } catch (NoSuchFieldError | IllegalStateException e) {
                LOGGER.error("Property {}.{} not found, cannot instantiate JMS configuration.", JMS_DEFAULT,
                        propertyName);
                throw e;
            }
        }

        private JmsTemplate jmsTemplate() {
            final JmsTemplate jmsTemplate = new JmsTemplate();
            jmsTemplate.setDefaultDestination(this.destinationQueue);
            jmsTemplate.setExplicitQosEnabled(this.property(PROPERTY_EXPLICIT_QOS_ENABLED, boolean.class));
            jmsTemplate.setTimeToLive(this.property(PROPERTY_TIME_TO_LIVE, long.class));
            jmsTemplate.setDeliveryPersistent(this.property(PROPERTY_DELIVERY_PERSISTENT, boolean.class));
            jmsTemplate.setReceiveTimeout(this.property(PROPERTY_RECEIVE_TIMEOUT, long.class));
            jmsTemplate.setConnectionFactory(JmsConfigurationFactory.this.pooledConnectionFactory);
            return jmsTemplate;
        }

        private RedeliveryPolicy redeliveryPolicy() {
            final RedeliveryPolicy redeliveryPolicy = this.redeliveryPolicy(this.destinationQueue,
                    this.property(PROPERTY_INITIAL_REDELIVERY_DELAY, long.class),
                    this.property(PROPERTY_MAXIMUM_REDELIVERIES, int.class),
                    this.property(PROPERTY_MAXIMUM_REDELIVERY_DELAY, long.class),
                    this.property(PROPERTY_REDELIVERY_DELAY, long.class),
                    this.property(PROPERTY_BACK_OFF_MULTIPLIER, long.class),
                    this.property(PROPERTY_USE_EXPONENTIAL_BACK_OFF, boolean.class));

            JmsConfigurationFactory.this.redeliveryPolicyMap.put(this.destinationQueue, redeliveryPolicy);

            return redeliveryPolicy;
        }

        private RedeliveryPolicy redeliveryPolicy(final ActiveMQDestination queue, final long initialRedeliveryDelay,
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

        private DefaultMessageListenerContainer messageListenerContainer() {
            return this.defaultMessageListenerContainer(this.destinationQueue, this.messageListener,
                    this.property(PROPERTY_CONCURRENT_CONSUMERS, int.class),
                    this.property(PROPERTY_MAX_CONCURRENT_CONSUMERS, int.class));
        }

        private DefaultMessageListenerContainer defaultMessageListenerContainer(final ActiveMQDestination destination,
                final V messageListener, final int concConsumers, final int maxConcConsumers) {

            final DefaultMessageListenerContainer defaultMessageListenerContainer = new DefaultMessageListenerContainer();
            defaultMessageListenerContainer.setConnectionFactory(JmsConfigurationFactory.this.pooledConnectionFactory);
            defaultMessageListenerContainer.setDestination(destination);
            defaultMessageListenerContainer.setMessageListener(messageListener);
            defaultMessageListenerContainer.setConcurrentConsumers(concConsumers);
            defaultMessageListenerContainer.setMaxConcurrentConsumers(maxConcConsumers);
            defaultMessageListenerContainer.setSessionTransacted(true);
            return defaultMessageListenerContainer;
        }
    }

}
