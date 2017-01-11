package com.alliander.osgp.shared.application.config;

import javax.jms.MessageListener;

import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

public class JmsConfigurationFactory {

    protected Environment environment;

    protected PooledConnectionFactory pooledConnectionFactory;

    private RedeliveryPolicyMap redeliveryPolicyMap;

    public JmsConfigurationFactory(final Environment environment,
            final PooledConnectionFactory pooledConnectionFactory, final RedeliveryPolicyMap redeliveryPolicyMap) {
        this.environment = environment;
        this.pooledConnectionFactory = pooledConnectionFactory;
        this.redeliveryPolicyMap = redeliveryPolicyMap;
    }

    public JmsRequestConfiguration getInstance(final String propertyPrefix) {
        return new RequestInstanceCreator(propertyPrefix).create();
    }

    public JmsResponseConfiguration getInstance(final String propertyPrefix, final MessageListener messageListener) {
        return new ResponseInstanceCreator(propertyPrefix, messageListener).create();
    }

    private abstract class InstanceCreator {

        private String propertyPrefix;

        private ActiveMQDestination destinationQueue;

        public InstanceCreator(final String propertyPrefix) {
            this.propertyPrefix = propertyPrefix;
            this.destinationQueue = new ActiveMQQueue(this.property("queue", String.class));
        }

        protected <T> T property(final String propertyName, final Class<T> targetType) {
            if (JmsConfigurationFactory.this.environment.containsProperty(this.propertyPrefix + "." + propertyName)) {
                return JmsConfigurationFactory.this.environment.getProperty(this.propertyPrefix + "." + propertyName,
                        targetType);
            } else {
                final T property = this.propertyFallback(propertyName, targetType);
                return property;
            }
        }

        protected abstract <T> T propertyFallback(final String propertyName, final Class<T> targetType);

        protected ActiveMQDestination destinationQueue() {
            return this.destinationQueue;
        }

        protected JmsTemplate jmsTemplate() {
            final JmsTemplate jmsTemplate = new JmsTemplate();
            jmsTemplate.setDefaultDestination(this.destinationQueue());
            jmsTemplate.setExplicitQosEnabled(this.property("explicit.qos.enabled", boolean.class));
            jmsTemplate.setTimeToLive(this.property("time.to.live", int.class));
            jmsTemplate.setDeliveryPersistent(this.property("delivery.persistent", boolean.class));
            jmsTemplate.setConnectionFactory(JmsConfigurationFactory.this.pooledConnectionFactory);
            return jmsTemplate;
        }

        protected RedeliveryPolicy redeliveryPolicy() {
            final RedeliveryPolicy redeliveryPolicy = this.redeliveryPolicy(this.destinationQueue(),
                    this.property("initial.redelivery.delay", int.class),
                    this.property("maximum.redeliveries", int.class),
                    this.property("maximum.redelivery.delay", int.class), this.property("redelivery.delay", int.class),
                    this.property("back.off.multiplier", int.class),
                    this.property("use.exponential.back.off", boolean.class));

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

    }

    private class RequestInstanceCreator extends InstanceCreator {

        public RequestInstanceCreator(final String propertyPrefix) {
            super(propertyPrefix);
        }

        public JmsRequestConfiguration create() {
            final JmsRequestConfiguration configuration = new JmsRequestConfiguration();
            configuration.setJmsTemplate(this.jmsTemplate());
            configuration.setRedeliveryPolicy(this.redeliveryPolicy());
            return configuration;
        }

        @Override
        protected <T> T propertyFallback(final String propertyName, final Class<T> targetType) {
            return JmsConfigurationFactory.this.environment
                    .getProperty("jms.requests" + "." + propertyName, targetType);
        }
    }

    private class ResponseInstanceCreator extends InstanceCreator {

        private MessageListener messageListener;

        public ResponseInstanceCreator(final String propertyPrefix, final MessageListener messageListener) {
            super(propertyPrefix);
            this.messageListener = messageListener;
        }

        @Override
        protected <T> T propertyFallback(final String propertyName, final Class<T> targetType) {
            return JmsConfigurationFactory.this.environment.getProperty("jms.responses" + "." + propertyName,
                    targetType);
        }

        public JmsResponseConfiguration create() {
            final JmsResponseConfiguration configuration = new JmsResponseConfiguration();
            configuration.setJmsTemplate(this.jmsTemplate());
            configuration.setRedeliveryPolicy(this.redeliveryPolicy());
            configuration.setMessageListenerContainer(this.messageListenerContainer(this.messageListener));
            return configuration;
        }

        protected DefaultMessageListenerContainer messageListenerContainer(final MessageListener messageListener) {
            return this.defaultMessageListenerContainer(this.destinationQueue(), this.messageListener,
                    this.property("concurrent.consumers", int.class),
                    this.property("max.concurrent.consumers", int.class));
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

    public class JmsConfiguration {
        private JmsTemplate jmsTemplate;

        private RedeliveryPolicy redeliveryPolicy;

        public JmsTemplate getJmsTemplate() {
            return this.jmsTemplate;
        }

        public void setJmsTemplate(final JmsTemplate jmsTemplate) {
            this.jmsTemplate = jmsTemplate;
        }

        public RedeliveryPolicy getRedeliveryPolicy() {
            return this.redeliveryPolicy;
        }

        public void setRedeliveryPolicy(final RedeliveryPolicy redeliveryPolicy) {
            this.redeliveryPolicy = redeliveryPolicy;
        }
    }

    public class JmsRequestConfiguration extends JmsConfiguration {

    }

    public class JmsResponseConfiguration extends JmsConfiguration {

        private DefaultMessageListenerContainer messageListenerContainer;

        public JmsResponseConfiguration() {
            // TODO Auto-generated constructor stub
        }

        public DefaultMessageListenerContainer getMessageListenerContainer() {
            return this.messageListenerContainer;
        }

        public void setMessageListenerContainer(final DefaultMessageListenerContainer messageListenerContainer) {
            this.messageListenerContainer = messageListenerContainer;
        }

    }
}
