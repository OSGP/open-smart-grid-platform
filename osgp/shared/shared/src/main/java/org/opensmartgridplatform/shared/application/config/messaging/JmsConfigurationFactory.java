// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.application.config.messaging;

import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_TYPE;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONCURRENT_CONSUMERS;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_POOL_BLOCK_IF_SESSION_POOL_IS_FULL;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_POOL_BLOCK_IF_SESSION_POOL_IS_FULL_TIMEOUT;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_POOL_EXPIRY_TIMEOUT;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_POOL_IDLE_TIMEOUT;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_POOL_MAX_ACTIVE_SESSIONS;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_POOL_SIZE;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_POOL_TIME_BETWEEN_EXPIRATION_CHECK_MILLIS;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_DELIVERY_PERSISTENT;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_EXPLICIT_QOS_ENABLED;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_MAX_CONCURRENT_CONSUMERS;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_QUEUE;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_TIME_TO_LIVE;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Destination;
import jakarta.jms.MessageListener;
import javax.net.ssl.SSLException;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.opensmartgridplatform.shared.infra.jms.OsgpJmsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/** This class provides the basic components used for JMS messaging. */
public class JmsConfigurationFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(JmsConfigurationFactory.class);

  private final JmsPropertyReader propertyReader;
  private final PooledConnectionFactory pooledConnectionFactory;
  private final ConnectionFactory consumerConnectionFactory;
  private final JmsBroker jmsBroker;

  public JmsConfigurationFactory(
      final Environment environment,
      final JmsConfiguration defaultJmsConfiguration,
      final String propertyPrefix)
      throws SSLException {
    LOGGER.info("Initializing JmsConfigurationFactory with propertyPrefix \"{}\".", propertyPrefix);
    this.propertyReader =
        new JmsPropertyReader(environment, propertyPrefix, defaultJmsConfiguration);
    this.jmsBroker = this.getBroker();
    this.pooledConnectionFactory = this.initPooledConnectionFactory();
    this.consumerConnectionFactory = this.initConnectionFactory();
  }

  public Destination getQueue(final String queueName) {
    return this.jmsBroker.getQueue(queueName);
  }

  public PooledConnectionFactory getPooledConnectionFactory() {
    return this.pooledConnectionFactory;
  }

  public ConnectionFactory getConsumerConnectionFactory() {
    return this.consumerConnectionFactory;
  }

  public JmsTemplate initJmsTemplate() {
    LOGGER.debug("Initializing JMS template.");

    final Destination destination = this.getQueue(this.getQueueName());
    return this.initJmsTemplate(destination);
  }

  public JmsTemplate initJmsTemplate(final Destination destination) {
    LOGGER.debug("Initializing JMS template for destination {}", destination);
    final OsgpJmsTemplate jmsTemplate = new OsgpJmsTemplate();
    jmsTemplate.setDefaultDestination(destination);
    jmsTemplate.setExplicitQosEnabled(
        this.propertyReader.get(PROPERTY_NAME_EXPLICIT_QOS_ENABLED, boolean.class));
    jmsTemplate.setTimeToLive(this.propertyReader.get(PROPERTY_NAME_TIME_TO_LIVE, long.class));
    jmsTemplate.setDeliveryPersistent(
        this.propertyReader.get(PROPERTY_NAME_DELIVERY_PERSISTENT, boolean.class));
    jmsTemplate.setConnectionFactory(this.pooledConnectionFactory);
    return jmsTemplate;
  }

  public DefaultMessageListenerContainer initMessageListenerContainer(
      final MessageListener messageListener) {
    LOGGER.debug(
        "Initializing message listener container for message listener: {}.", messageListener);
    final Destination destination = this.getQueue(this.getQueueName());
    return this.initMessageListenerContainer(messageListener, destination);
  }

  public DefaultMessageListenerContainer initMessageListenerContainer(
      final MessageListener messageListener, final Destination destination) {
    LOGGER.debug(
        "Initializing message listener container for message listener: {}, and destination {}.",
        messageListener,
        destination);

    final DefaultMessageListenerContainer messageListenerContainer =
        this.initMessageListenerContainer();
    messageListenerContainer.setDestination(destination);
    messageListenerContainer.setMessageListener(messageListener);
    return messageListenerContainer;
  }

  public RedeliveryPolicy getRedeliveryPolicy() {
    return this.jmsBroker.getRedeliveryPolicy();
  }

  private ConnectionFactory initConnectionFactory() throws SSLException {
    return this.jmsBroker.initConnectionFactory();
  }

  private JmsBroker getBroker() {
    final JmsBrokerType jmsBrokerType =
        this.propertyReader.get(PROPERTY_NAME_BROKER_TYPE, JmsBrokerType.class);
    final JmsBrokerFactory jmsBrokerFactory = new JmsBrokerFactory(this.propertyReader);
    return jmsBrokerFactory.getBroker(jmsBrokerType);
  }

  private String getQueueName() {
    return this.propertyReader.get(PROPERTY_NAME_QUEUE, String.class);
  }

  private DefaultMessageListenerContainer initMessageListenerContainer() {
    LOGGER.debug("Initializing default message listener container.");

    final DefaultMessageListenerContainer defaultMessageListenerContainer =
        new DefaultMessageListenerContainer();
    defaultMessageListenerContainer.setConnectionFactory(this.consumerConnectionFactory);
    defaultMessageListenerContainer.setConcurrentConsumers(
        this.propertyReader.get(PROPERTY_NAME_CONCURRENT_CONSUMERS, int.class));
    defaultMessageListenerContainer.setMaxConcurrentConsumers(
        this.propertyReader.get(PROPERTY_NAME_MAX_CONCURRENT_CONSUMERS, int.class));
    defaultMessageListenerContainer.setSessionTransacted(true);
    return defaultMessageListenerContainer;
  }

  private PooledConnectionFactory initPooledConnectionFactory() throws SSLException {
    LOGGER.debug("Initializing pooled connection factory.");

    final PooledConnectionFactory connectionFactory = new PooledConnectionFactory();
    connectionFactory.setConnectionFactory(this.initConnectionFactory());
    connectionFactory.setMaxConnections(
        this.propertyReader.get(PROPERTY_NAME_CONNECTION_POOL_SIZE, int.class));
    connectionFactory.setMaximumActiveSessionPerConnection(
        this.propertyReader.get(PROPERTY_NAME_CONNECTION_POOL_MAX_ACTIVE_SESSIONS, int.class));
    final boolean blockIfSessionPoolIsFull =
        this.propertyReader.get(
            PROPERTY_NAME_CONNECTION_POOL_BLOCK_IF_SESSION_POOL_IS_FULL, boolean.class);
    connectionFactory.setBlockIfSessionPoolIsFull(blockIfSessionPoolIsFull);
    if (blockIfSessionPoolIsFull) {
      connectionFactory.setBlockIfSessionPoolIsFullTimeout(
          this.propertyReader.get(
              PROPERTY_NAME_CONNECTION_POOL_BLOCK_IF_SESSION_POOL_IS_FULL_TIMEOUT, long.class));
    }
    connectionFactory.setExpiryTimeout(
        this.propertyReader.get(PROPERTY_NAME_CONNECTION_POOL_EXPIRY_TIMEOUT, long.class));
    connectionFactory.setIdleTimeout(
        this.propertyReader.get(PROPERTY_NAME_CONNECTION_POOL_IDLE_TIMEOUT, int.class));
    connectionFactory.setTimeBetweenExpirationCheckMillis(
        this.propertyReader.get(
            PROPERTY_NAME_CONNECTION_POOL_TIME_BETWEEN_EXPIRATION_CHECK_MILLIS, long.class));

    return connectionFactory;
  }
}
