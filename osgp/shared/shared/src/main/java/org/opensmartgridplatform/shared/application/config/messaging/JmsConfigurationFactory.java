/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config.messaging;

import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BACK_OFF_MULTIPLIER;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_CLIENT_KEY_STORE;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_CLIENT_KEY_STORE_SECRET;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_CLIENT_TRUST_STORE;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_CLIENT_TRUST_STORE_SECRET;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_SECRET;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_URL;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_USERNAME;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONCURRENT_CONSUMERS;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_MESSAGE_PRIORITY_SUPPORTED;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_POOL_BLOCK_IF_SESSION_POOL_IS_FULL;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_POOL_BLOCK_IF_SESSION_POOL_IS_FULL_TIMEOUT;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_POOL_EXPIRY_TIMEOUT;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_POOL_IDLE_TIMEOUT;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_POOL_MAX_ACTIVE_SESSIONS;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_POOL_SIZE;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_POOL_TIME_BETWEEN_EXPIRATION_CHECK_MILLIS;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_QUEUE_PREFETCH;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_SEND_TIMEOUT;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_DELIVERY_PERSISTENT;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_EXPLICIT_QOS_ENABLED;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_INITIAL_REDELIVERY_DELAY;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_MAXIMUM_REDELIVERIES;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_MAXIMUM_REDELIVERY_DELAY;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_MAX_CONCURRENT_CONSUMERS;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_MAX_THREAD_POOL_SIZE;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_QUEUE;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_REDELIVERY_DELAY;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_TIME_TO_LIVE;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_TRUSTED_PACKAGES;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_TRUST_ALL_PACKAGES;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_USE_EXPONENTIAL_BACK_OFF;

import java.util.Arrays;
import java.util.concurrent.ThreadPoolExecutor;
import javax.jms.MessageListener;
import javax.net.ssl.SSLException;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.commons.lang3.StringUtils;
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
  private final RedeliveryPolicy redeliveryPolicy;

  public JmsConfigurationFactory(
      final Environment environment,
      final JmsConfiguration defaultJmsConfiguration,
      final String propertyPrefix)
      throws SSLException {
    LOGGER.info("Initializing JmsConfigurationFactory with propertyPrefix \"{}\".", propertyPrefix);
    this.propertyReader =
        new JmsPropertyReader(environment, propertyPrefix, defaultJmsConfiguration);
    this.redeliveryPolicy = this.initRedeliveryPolicy();
    this.pooledConnectionFactory = this.initPooledConnectionFactory();
  }

  public RedeliveryPolicy getRedeliveryPolicy() {
    return this.redeliveryPolicy;
  }

  public PooledConnectionFactory getPooledConnectionFactory() {
    return this.pooledConnectionFactory;
  }

  public JmsTemplate initJmsTemplate() {
    LOGGER.debug("Initializing JMS template.");

    final ActiveMQDestination destination =
        new ActiveMQQueue(this.propertyReader.get(PROPERTY_NAME_QUEUE, String.class));
    return this.initJmsTemplate(destination);
  }

  public JmsTemplate initJmsTemplate(final ActiveMQDestination destination) {
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
    final ActiveMQDestination destination =
        new ActiveMQQueue(this.propertyReader.get(PROPERTY_NAME_QUEUE, String.class));
    return this.initMessageListenerContainer(messageListener, destination);
  }

  public DefaultMessageListenerContainer initMessageListenerContainer(
      final MessageListener messageListener, final ActiveMQDestination destination) {
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

  private DefaultMessageListenerContainer initMessageListenerContainer() {
    LOGGER.debug("Initializing default message listener container.");

    final DefaultMessageListenerContainer defaultMessageListenerContainer =
        new DefaultMessageListenerContainer();
    defaultMessageListenerContainer.setConnectionFactory(this.pooledConnectionFactory);
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

  private ActiveMQSslConnectionFactory initConnectionFactory() throws SSLException {
    LOGGER.debug("Initializing connection factory.");

    final ActiveMQPrefetchPolicy activeMQPrefetchPolicy = new ActiveMQPrefetchPolicy();
    activeMQPrefetchPolicy.setQueuePrefetch(
        this.propertyReader.get(PROPERTY_NAME_CONNECTION_QUEUE_PREFETCH, int.class));

    final ActiveMQSslConnectionFactory activeMQConnectionFactory =
        new ActiveMQSslConnectionFactory();

    activeMQConnectionFactory.setRedeliveryPolicyMap(this.initRedeliveryPolicyMap());
    activeMQConnectionFactory.setBrokerURL(
        this.propertyReader.get(PROPERTY_NAME_BROKER_URL, String.class));
    activeMQConnectionFactory.setNonBlockingRedelivery(true);
    activeMQConnectionFactory.setPrefetchPolicy(activeMQPrefetchPolicy);
    activeMQConnectionFactory.setSendTimeout(
        this.propertyReader.get(PROPERTY_NAME_CONNECTION_SEND_TIMEOUT, int.class));
    final boolean trustAllPackages =
        this.propertyReader.get(PROPERTY_NAME_TRUST_ALL_PACKAGES, boolean.class);
    activeMQConnectionFactory.setTrustAllPackages(trustAllPackages);
    if (!trustAllPackages) {
      activeMQConnectionFactory.setTrustedPackages(
          Arrays.asList(
              this.propertyReader.get(PROPERTY_NAME_TRUSTED_PACKAGES, String.class).split(",")));
    }

    // Thread management
    activeMQConnectionFactory.setMaxThreadPoolSize(
        this.propertyReader.get(PROPERTY_NAME_MAX_THREAD_POOL_SIZE, int.class));
    activeMQConnectionFactory.setRejectedTaskHandler(new ThreadPoolExecutor.CallerRunsPolicy());

    // Add optional user name/password configuration.
    final String username = this.propertyReader.get(PROPERTY_NAME_BROKER_USERNAME, String.class);
    final String password = this.propertyReader.get(PROPERTY_NAME_BROKER_SECRET, String.class);
    if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
      activeMQConnectionFactory.setUserName(username);
      activeMQConnectionFactory.setPassword(password);
    }

    final JmsBrokerSslSettings jmsBrokerSslSettings =
        new JmsBrokerSslSettings(
            this.propertyReader.get(PROPERTY_NAME_BROKER_CLIENT_KEY_STORE, String.class),
            this.propertyReader.get(PROPERTY_NAME_BROKER_CLIENT_KEY_STORE_SECRET, String.class),
            this.propertyReader.get(PROPERTY_NAME_BROKER_CLIENT_TRUST_STORE, String.class),
            this.propertyReader.get(PROPERTY_NAME_BROKER_CLIENT_TRUST_STORE_SECRET, String.class));
    jmsBrokerSslSettings.applyToFactory(activeMQConnectionFactory);

    // Enable message priority
    activeMQConnectionFactory.setMessagePrioritySupported(
        this.propertyReader.get(
            PROPERTY_NAME_CONNECTION_MESSAGE_PRIORITY_SUPPORTED, boolean.class));

    return activeMQConnectionFactory;
  }

  private RedeliveryPolicyMap initRedeliveryPolicyMap() {
    LOGGER.debug("Initializing redelivery policy map.");

    final RedeliveryPolicyMap redeliveryPolicyMap = new RedeliveryPolicyMap();
    redeliveryPolicyMap.setDefaultEntry(this.redeliveryPolicy);
    return redeliveryPolicyMap;
  }

  private RedeliveryPolicy initRedeliveryPolicy() {
    LOGGER.debug("Initializing redelivery policy.");

    final RedeliveryPolicy policy = new RedeliveryPolicy();
    policy.setUseExponentialBackOff(
        this.propertyReader.get(PROPERTY_NAME_USE_EXPONENTIAL_BACK_OFF, boolean.class));
    policy.setBackOffMultiplier(
        this.propertyReader.get(PROPERTY_NAME_BACK_OFF_MULTIPLIER, double.class));

    policy.setMaximumRedeliveries(
        this.propertyReader.get(PROPERTY_NAME_MAXIMUM_REDELIVERIES, int.class));
    policy.setInitialRedeliveryDelay(
        this.propertyReader.get(PROPERTY_NAME_INITIAL_REDELIVERY_DELAY, long.class));
    policy.setRedeliveryDelay(this.propertyReader.get(PROPERTY_NAME_REDELIVERY_DELAY, long.class));
    policy.setMaximumRedeliveryDelay(
        this.propertyReader.get(PROPERTY_NAME_MAXIMUM_REDELIVERY_DELAY, long.class));
    return policy;
  }
}
