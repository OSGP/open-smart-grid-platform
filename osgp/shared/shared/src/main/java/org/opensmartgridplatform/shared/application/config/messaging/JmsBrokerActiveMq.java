/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_MESSAGE_PRIORITY_SUPPORTED;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_QUEUE_PREFETCH;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_SEND_TIMEOUT;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_INITIAL_REDELIVERY_DELAY;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_MAXIMUM_REDELIVERIES;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_MAXIMUM_REDELIVERY_DELAY;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_MAX_THREAD_POOL_SIZE;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_REDELIVERY_DELAY;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_TRUSTED_PACKAGES;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_TRUST_ALL_PACKAGES;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_USE_EXPONENTIAL_BACK_OFF;

import java.util.Arrays;
import java.util.concurrent.ThreadPoolExecutor;
import javax.jms.Destination;
import javax.net.ssl.SSLException;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsBrokerActiveMq implements JmsBroker {

  private static final Logger LOGGER = LoggerFactory.getLogger(JmsBrokerActiveMq.class);

  private final JmsPropertyReader propertyReader;

  public JmsBrokerActiveMq(final JmsPropertyReader propertyReader) {
    this.propertyReader = propertyReader;
  }

  @Override
  public JmsBrokerType getBrokerType() {
    return JmsBrokerType.ACTIVE_MQ;
  }

  @Override
  public Destination getQueue(final String queueName) {
    return new ActiveMQQueue(queueName);
  }

  @Override
  public ActiveMQSslConnectionFactory initConnectionFactory() throws SSLException {
    LOGGER.debug("Initializing connection factory.");

    final ActiveMQPrefetchPolicy activeMQPrefetchPolicy = new ActiveMQPrefetchPolicy();
    activeMQPrefetchPolicy.setQueuePrefetch(
        this.propertyReader.get(PROPERTY_NAME_CONNECTION_QUEUE_PREFETCH, int.class));

    final ActiveMQSslConnectionFactory activeMQConnectionFactory =
        new ActiveMQSslConnectionFactory();

    activeMQConnectionFactory.setRedeliveryPolicyMap(this.getRedeliveryPolicyMap());
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

  private RedeliveryPolicyMap getRedeliveryPolicyMap() {
    LOGGER.debug("Initializing redelivery policy map.");

    final RedeliveryPolicyMap redeliveryPolicyMap = new RedeliveryPolicyMap();
    redeliveryPolicyMap.setDefaultEntry(this.getRedeliveryPolicy());
    return redeliveryPolicyMap;
  }

  @Override
  public RedeliveryPolicy getRedeliveryPolicy() {
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
