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

import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_CLIENT_KEY_STORE;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_CLIENT_KEY_STORE_SECRET;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_CLIENT_TRUST_STORE;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_CLIENT_TRUST_STORE_SECRET;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_SECRET;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_URL;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_USERNAME;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_QUEUE_CONSUMER_WINDOW_SIZE;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_MAX_THREAD_POOL_SIZE;

import javax.jms.Destination;
import javax.net.ssl.SSLException;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class provides the basic components used for JMS messaging. */
public class JmsBrokerArtemis implements JmsBroker {
  private static final Logger LOGGER = LoggerFactory.getLogger(JmsBrokerArtemis.class);

  private final JmsPropertyReader propertyReader;

  public JmsBrokerArtemis(final JmsPropertyReader propertyReader) {
    this.propertyReader = propertyReader;
  }

  @Override
  public JmsBrokerType getBrokerType() {
    return JmsBrokerType.ARTEMIS;
  }

  @Override
  public Destination getQueue(final String queueName) {
    return new ActiveMQQueue(queueName);
  }

  @Override
  public RedeliveryPolicy getRedeliveryPolicy() {
    throw new NotImplementedException("RedeliveryPolicy should be implemented in broker.xml");
  }

  @Override
  public ActiveMQConnectionFactory initConnectionFactory() throws SSLException {
    LOGGER.debug("Initializing connection factory.");

    final ActiveMQConnectionFactory activeMQConnectionFactory =
        new ActiveMQConnectionFactory(this.initBrokerUrl());

    activeMQConnectionFactory.setConsumerWindowSize(
        this.propertyReader.get(PROPERTY_NAME_CONNECTION_QUEUE_CONSUMER_WINDOW_SIZE, int.class));

    // Thread management
    activeMQConnectionFactory.setThreadPoolMaxSize(
        this.propertyReader.get(PROPERTY_NAME_MAX_THREAD_POOL_SIZE, int.class));

    // Add optional user name/password configuration.
    final String username = this.propertyReader.get(PROPERTY_NAME_BROKER_USERNAME, String.class);
    final String password = this.propertyReader.get(PROPERTY_NAME_BROKER_SECRET, String.class);
    if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
      activeMQConnectionFactory.setUser(username);
      activeMQConnectionFactory.setPassword(password);
    }

    return activeMQConnectionFactory;
  }

  private String initBrokerUrl() {
    final String brokerUrl = this.propertyReader.get(PROPERTY_NAME_BROKER_URL, String.class);
    final String clientKeyStore =
        this.propertyReader.get(PROPERTY_NAME_BROKER_CLIENT_KEY_STORE, String.class);
    final String clientKeyStorePwd =
        this.propertyReader.get(PROPERTY_NAME_BROKER_CLIENT_KEY_STORE_SECRET, String.class);
    final String trustKeyStore =
        this.propertyReader.get(PROPERTY_NAME_BROKER_CLIENT_TRUST_STORE, String.class);
    final String trustKeyStorePwd =
        this.propertyReader.get(PROPERTY_NAME_BROKER_CLIENT_TRUST_STORE_SECRET, String.class);

    if (clientKeyStore == null) {
      LOGGER.debug(
          "No " + PROPERTY_NAME_BROKER_CLIENT_KEY_STORE + " found, use brokerUrl: " + brokerUrl);
      return brokerUrl;
    }

    final String sslBrokerUrl =
        String.format(
            "%s%s"
                + "sslEnabled=true"
                + "&trustStorePath=%s"
                + "&trustStorePassword=%s"
                + "&keyStorePath=%s"
                + "&keyStorePassword=%s",
            brokerUrl,
            brokerUrl.contains("?") ? "&" : "?",
            trustKeyStore,
            trustKeyStorePwd,
            clientKeyStore,
            clientKeyStorePwd);
    LOGGER.info("Using brokerUrl: " + sslBrokerUrl);
    return sslBrokerUrl;
  }
}
