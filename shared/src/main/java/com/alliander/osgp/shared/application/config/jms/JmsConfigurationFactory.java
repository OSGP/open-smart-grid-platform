package com.alliander.osgp.shared.application.config.jms;

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

/**
 * Factory object for creating and initialzing JMS configuration objects with
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

    private Environment environment;

    private PooledConnectionFactory pooledConnectionFactory;

    private RedeliveryPolicyMap redeliveryPolicyMap;

    /**
     * 
     * @param environment
     *            Environment to retrieve the properties from.
     * @param pooledConnectionFactory
     *            Created objects will be linked to this connection factory.
     * @param redeliveryPolicyMap
     *            Created redelivery policy will be added to this map.
     */
    public JmsConfigurationFactory(final Environment environment, final PooledConnectionFactory pooledConnectionFactory,
            final RedeliveryPolicyMap redeliveryPolicyMap) {
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
        return new InstanceCreator(propertyPrefix).create();
    }

    /**
     * Initialize configuration.
     * 
     * @param propertyPrefix
     *            Prefix for all properties.
     * @param messageListener
     *            The message listener to put on the queue.
     * @return JmsConfiguration containing created objects.
     */
    public JmsConfiguration initializeConfiguration(final String propertyPrefix,
            final MessageListener messageListener) {
        return new InstanceCreator(propertyPrefix, messageListener).create();
    }

    private class InstanceCreator {

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

        private static final String PROPERTY_EXPLICIT_QOS_ENABLED = "explicit.qos.enabled";

        private static final String PROPERTY_QUEUE = "queue";

        private static final String JMS_DEFAULT = "jms.default";

        private final String propertyPrefix;

        private final ActiveMQDestination destinationQueue;

        private final MessageListener messageListener;

        public InstanceCreator(final String propertyPrefix, final MessageListener messageListener) {
            this.propertyPrefix = propertyPrefix;
            this.destinationQueue = new ActiveMQQueue(this.property(PROPERTY_QUEUE, String.class));
            this.messageListener = messageListener;
        }

        public InstanceCreator(final String propertyPrefix) {
            this(propertyPrefix, null);
        }

        public JmsConfiguration create() {
            final JmsConfiguration configuration = new JmsConfiguration();
            configuration.setJmsTemplate(this.jmsTemplate());
            configuration.setRedeliveryPolicy(this.redeliveryPolicy());
            if (this.messageListener != null) {
                configuration.setMessageListenerContainer(this.messageListenerContainer(this.messageListener));
            }
            return configuration;
        }

        private <T> T property(final String propertyName, final Class<T> targetType) {
            try {
                T property = JmsConfigurationFactory.this.environment
                        .getProperty(this.propertyPrefix + "." + propertyName, targetType);
                if (property == null) {
                    LOGGER.debug("Property {}.{} not found, trying default property.", this.propertyPrefix,
                            propertyName);
                    property = this.fallbackProperty(propertyName, targetType);
                }
                return property;
            } catch (Throwable e) {
                final T property = this.fallbackProperty(propertyName, targetType);
                return property;
            }

        }

        private <T> T fallbackProperty(final String propertyName, final Class<T> targetType) {
            try {
                return JmsConfigurationFactory.this.environment.getProperty(JMS_DEFAULT + "." + propertyName,
                        targetType);
            } catch (Throwable e) {
                LOGGER.error("Property {}.{} not found, cannot instantiate JMS configuration.", JMS_DEFAULT,
                        propertyName);
                throw e;
            }
        }

        private JmsTemplate jmsTemplate() {
            final JmsTemplate jmsTemplate = new JmsTemplate();
            jmsTemplate.setDefaultDestination(this.destinationQueue);
            jmsTemplate.setExplicitQosEnabled(this.property(PROPERTY_EXPLICIT_QOS_ENABLED, boolean.class));
            jmsTemplate.setTimeToLive(this.property(PROPERTY_TIME_TO_LIVE, int.class));
            jmsTemplate.setDeliveryPersistent(this.property(PROPERTY_DELIVERY_PERSISTENT, boolean.class));
            jmsTemplate.setConnectionFactory(JmsConfigurationFactory.this.pooledConnectionFactory);
            return jmsTemplate;
        }

        private RedeliveryPolicy redeliveryPolicy() {
            final RedeliveryPolicy redeliveryPolicy = this.redeliveryPolicy(this.destinationQueue,
                    this.property(PROPERTY_INITIAL_REDELIVERY_DELAY, int.class),
                    this.property(PROPERTY_MAXIMUM_REDELIVERIES, int.class),
                    this.property(PROPERTY_MAXIMUM_REDELIVERY_DELAY, int.class),
                    this.property(PROPERTY_REDELIVERY_DELAY, int.class),
                    this.property(PROPERTY_BACK_OFF_MULTIPLIER, int.class),
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

        private DefaultMessageListenerContainer messageListenerContainer(final MessageListener messageListener) {
            return this.defaultMessageListenerContainer(this.destinationQueue, this.messageListener,
                    this.property(PROPERTY_CONCURRENT_CONSUMERS, int.class),
                    this.property(PROPERTY_MAX_CONCURRENT_CONSUMERS, int.class));
        }

        private DefaultMessageListenerContainer defaultMessageListenerContainer(final ActiveMQDestination destination,
                final MessageListener messageListener, final int concConsumers, final int maxConcConsumers) {

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
