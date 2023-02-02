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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BACK_OFF_MULTIPLIER;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_CLIENT_KEY_STORE;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_CLIENT_KEY_STORE_SECRET;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_CLIENT_TRUST_STORE;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_CLIENT_TRUST_STORE_SECRET;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_SECRET;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_TYPE;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_URL;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_BROKER_USERNAME;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONCURRENT_CONSUMERS;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_MESSAGE_PRIORITY_SUPPORTED;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_CONNECTION_QUEUE_CONSUMER_WINDOW_SIZE;
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
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_TRUST_ALL_PACKAGES;
import static org.opensmartgridplatform.shared.application.config.messaging.JmsPropertyNames.PROPERTY_NAME_USE_EXPONENTIAL_BACK_OFF;

import java.util.Map;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageListener;
import javax.net.ssl.SSLException;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

@ExtendWith(MockitoExtension.class)
public class JmsConfigurationFactoryTest {

  @Mock private Environment environment;
  @Mock private DefaultJmsConfiguration defaultJmsConfiguration;
  private final String propertyPrefix = "test.prefix";

  @ParameterizedTest
  @EnumSource(JmsBrokerType.class)
  void testGetQueue(final JmsBrokerType jmsBrokerType) throws ClassNotFoundException {
    final String queueName = "blabla";
    final JmsConfigurationFactory jmsConfigurationFactory =
        this.createConnectionFactory(jmsBrokerType);

    final Destination destination = jmsConfigurationFactory.getQueue(queueName);

    final String expectedClassName =
        jmsBrokerType == JmsBrokerType.ARTEMIS
            ? "org.apache.activemq.artemis.jms.client.ActiveMQQueue"
            : "org.apache.activemq.command.ActiveMQQueue";

    assertThat(destination).isInstanceOf(Class.forName(expectedClassName));
  }

  @ParameterizedTest
  @EnumSource(JmsBrokerType.class)
  void testGetPooledConnectionFactory(final JmsBrokerType jmsBrokerType) {
    final JmsConfigurationFactory jmsConfigurationFactory =
        this.createConnectionFactory(jmsBrokerType);

    final PooledConnectionFactory pooledConnectionFactory =
        jmsConfigurationFactory.getPooledConnectionFactory();

    assertThat(pooledConnectionFactory).isInstanceOf(PooledConnectionFactory.class);
    if (jmsBrokerType == JmsBrokerType.ARTEMIS) {
      assertThat(((ActiveMQConnectionFactory) pooledConnectionFactory.getConnectionFactory()))
          .isInstanceOf(ActiveMQConnectionFactory.class);

      final String expectedBrokerUrl =
          String.format(
              "%s?"
                  + "sslEnabled=true"
                  + "&trustStorePath=%s"
                  + "&trustStorePassword=%s"
                  + "&keyStorePath=%s"
                  + "&keyStorePassword=%s",
              "tcp://localhost:61616",
              "trust_store",
              "trust_store_secret",
              "key_store",
              "key_store_secret");
      final ActiveMQConnectionFactory connectionFactory =
          (ActiveMQConnectionFactory) pooledConnectionFactory.getConnectionFactory();
      final Map<String, Object> params = connectionFactory.getStaticConnectors()[0].getParams();
      assertThat(params.get("host")).isEqualTo("localhost");
      assertThat(params.get("port")).isEqualTo("61616");
      assertThat(params.get("sslEnabled")).isEqualTo("true");
      assertThat(params.get("keyStorePath")).isEqualTo("key_store");
      assertThat(params.get("keyStorePassword")).isEqualTo("key_store_secret");
      assertThat(params.get("trustStorePath")).isEqualTo("trust_store");
      assertThat(params.get("trustStorePassword")).isEqualTo("trust_store_secret");
    } else if (jmsBrokerType == JmsBrokerType.ACTIVE_MQ) {
      final String expectedBrokerUrl =
          String.format(
              "%s?"
                  + "sslEnabled=true"
                  + "&trustStorePath=%s"
                  + "&trustStorePassword=%s"
                  + "&keyStorePath=%s"
                  + "&keyStorePassword=%s",
              "tcp://localhost:61616",
              "trust_store",
              "trust_store_secret",
              "key_store",
              "key_store_secret");
      final ActiveMQSslConnectionFactory connectionFactory =
          (ActiveMQSslConnectionFactory) pooledConnectionFactory.getConnectionFactory();
      assertThat(connectionFactory.getBrokerURL()).isEqualTo("ssl://localhost:61616");
      assertThat(connectionFactory.getKeyStore()).isEqualTo("key_store");
      assertThat(connectionFactory.getKeyStorePassword()).isEqualTo("key_store_secret");
      assertThat(connectionFactory.getTrustStore()).isEqualTo("trust_store");
      assertThat(connectionFactory.getTrustStorePassword()).isEqualTo("trust_store_secret");
    }
  }

  @ParameterizedTest
  @EnumSource(JmsBrokerType.class)
  void testGetConsumerConnectionFactory(final JmsBrokerType jmsBrokerType) {
    final JmsConfigurationFactory jmsConfigurationFactory =
        this.createConnectionFactory(jmsBrokerType);

    final ConnectionFactory consumerConnectionFactory =
        jmsConfigurationFactory.getConsumerConnectionFactory();

    if (jmsBrokerType == JmsBrokerType.ARTEMIS) {
      assertThat(consumerConnectionFactory).isInstanceOf(ActiveMQConnectionFactory.class);
    } else if (jmsBrokerType == JmsBrokerType.ACTIVE_MQ) {
      assertThat(consumerConnectionFactory).isInstanceOf(ActiveMQSslConnectionFactory.class);
    }
  }

  @ParameterizedTest
  @EnumSource(JmsBrokerType.class)
  void testInitJmsTemplate(final JmsBrokerType jmsBrokerType) throws ClassNotFoundException {
    final JmsConfigurationFactory jmsConfigurationFactory =
        this.createConnectionFactory(jmsBrokerType);

    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_QUEUE, String.class))
        .thenReturn("queue-name");

    final JmsTemplate jmsTemplate = jmsConfigurationFactory.initJmsTemplate();

    final String expectedClassName =
        jmsBrokerType == JmsBrokerType.ARTEMIS
            ? "org.apache.activemq.artemis.jms.client.ActiveMQQueue"
            : "org.apache.activemq.command.ActiveMQQueue";

    assertThat(jmsTemplate.getDefaultDestination()).isInstanceOf(Class.forName(expectedClassName));
    assertThat(jmsTemplate.getConnectionFactory())
        .isEqualTo(jmsConfigurationFactory.getPooledConnectionFactory());
  }

  @ParameterizedTest
  @EnumSource(JmsBrokerType.class)
  void testInitJmsTemplateWithDestination(final JmsBrokerType jmsBrokerType)
      throws ClassNotFoundException {
    final JmsConfigurationFactory jmsConfigurationFactory =
        this.createConnectionFactory(jmsBrokerType);

    final Destination destination =
        jmsBrokerType == JmsBrokerType.ARTEMIS
            ? new ActiveMQQueue("xxx")
            : new org.apache.activemq.command.ActiveMQQueue("xxx");

    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_EXPLICIT_QOS_ENABLED, boolean.class))
        .thenReturn(true);
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_TIME_TO_LIVE, long.class))
        .thenReturn(666l);
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_DELIVERY_PERSISTENT, boolean.class))
        .thenReturn(false);

    final JmsTemplate jmsTemplate = jmsConfigurationFactory.initJmsTemplate(destination);

    final String expectedClassName =
        jmsBrokerType == JmsBrokerType.ARTEMIS
            ? "org.apache.activemq.artemis.jms.client.ActiveMQQueue"
            : "org.apache.activemq.command.ActiveMQQueue";

    assertThat(jmsTemplate.getDefaultDestination()).isInstanceOf(Class.forName(expectedClassName));
    assertThat(jmsTemplate.getConnectionFactory())
        .isEqualTo(jmsConfigurationFactory.getPooledConnectionFactory());
  }

  @ParameterizedTest
  @EnumSource(JmsBrokerType.class)
  void testInitMessageListenerContainer(final JmsBrokerType jmsBrokerType)
      throws ClassNotFoundException {
    final JmsConfigurationFactory jmsConfigurationFactory =
        this.createConnectionFactory(jmsBrokerType);

    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_QUEUE, String.class))
        .thenReturn("queue-name");

    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_CONCURRENT_CONSUMERS, int.class))
        .thenReturn(9);
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_MAX_CONCURRENT_CONSUMERS, int.class))
        .thenReturn(19);

    final MessageListener messageListener = mock(MessageListener.class);

    final DefaultMessageListenerContainer defaultMessageListenerContainer =
        jmsConfigurationFactory.initMessageListenerContainer(messageListener);

    final String expectedClassName =
        jmsBrokerType == JmsBrokerType.ARTEMIS
            ? "org.apache.activemq.artemis.jms.client.ActiveMQQueue"
            : "org.apache.activemq.command.ActiveMQQueue";

    assertThat(defaultMessageListenerContainer.getDestination())
        .isInstanceOf(Class.forName(expectedClassName));
    assertThat(defaultMessageListenerContainer.getMessageListener()).isEqualTo(messageListener);
    assertThat(defaultMessageListenerContainer.getConnectionFactory())
        .isEqualTo(jmsConfigurationFactory.getConsumerConnectionFactory());
  }

  @ParameterizedTest
  @EnumSource(JmsBrokerType.class)
  void testInitMessageListenerContainerWithDestination(final JmsBrokerType jmsBrokerType)
      throws ClassNotFoundException {
    final JmsConfigurationFactory jmsConfigurationFactory =
        this.createConnectionFactory(jmsBrokerType);

    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_CONCURRENT_CONSUMERS, int.class))
        .thenReturn(9);
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_MAX_CONCURRENT_CONSUMERS, int.class))
        .thenReturn(19);

    final MessageListener messageListener = mock(MessageListener.class);
    final Destination destination =
        jmsBrokerType == JmsBrokerType.ARTEMIS
            ? new ActiveMQQueue("xxx")
            : new org.apache.activemq.command.ActiveMQQueue("xxx");

    final DefaultMessageListenerContainer defaultMessageListenerContainer =
        jmsConfigurationFactory.initMessageListenerContainer(messageListener, destination);

    verify(this.environment, never())
        .getProperty(this.propertyPrefix + "." + PROPERTY_NAME_QUEUE, String.class);

    assertThat(defaultMessageListenerContainer.getDestination()).isEqualTo(destination);
    assertThat(defaultMessageListenerContainer.getMessageListener()).isEqualTo(messageListener);
    assertThat(defaultMessageListenerContainer.getConnectionFactory())
        .isEqualTo(jmsConfigurationFactory.getConsumerConnectionFactory());
  }

  @ParameterizedTest
  @EnumSource(JmsBrokerType.class)
  void testGetRedeliveryPolicy(final JmsBrokerType jmsBrokerType) {
    final JmsConfigurationFactory jmsConfigurationFactory =
        this.createConnectionFactory(jmsBrokerType);

    if (jmsBrokerType == JmsBrokerType.ARTEMIS) {
      assertThrows(NotImplementedException.class, jmsConfigurationFactory::getRedeliveryPolicy);
    } else if (jmsBrokerType == JmsBrokerType.ACTIVE_MQ) {
      when(this.environment.getProperty(
              this.propertyPrefix + "." + PROPERTY_NAME_USE_EXPONENTIAL_BACK_OFF, boolean.class))
          .thenReturn(false);
      when(this.environment.getProperty(
              this.propertyPrefix + "." + PROPERTY_NAME_BACK_OFF_MULTIPLIER, double.class))
          .thenReturn(2.1);
      when(this.environment.getProperty(
              this.propertyPrefix + "." + PROPERTY_NAME_MAXIMUM_REDELIVERIES, int.class))
          .thenReturn(2);
      when(this.environment.getProperty(
              this.propertyPrefix + "." + PROPERTY_NAME_INITIAL_REDELIVERY_DELAY, long.class))
          .thenReturn(3L);
      when(this.environment.getProperty(
              this.propertyPrefix + "." + PROPERTY_NAME_REDELIVERY_DELAY, long.class))
          .thenReturn(7L);
      when(this.environment.getProperty(
              this.propertyPrefix + "." + PROPERTY_NAME_MAXIMUM_REDELIVERY_DELAY, long.class))
          .thenReturn(8L);

      final RedeliveryPolicy redeliveryPolicy = jmsConfigurationFactory.getRedeliveryPolicy();
      assertThat(redeliveryPolicy.isUseExponentialBackOff()).isFalse();
      assertThat(redeliveryPolicy.getBackOffMultiplier()).isEqualTo(2.1);
      assertThat(redeliveryPolicy.getMaximumRedeliveries()).isEqualTo(2);
      assertThat(redeliveryPolicy.getInitialRedeliveryDelay()).isEqualTo(3L);
      assertThat(redeliveryPolicy.getRedeliveryDelay()).isEqualTo(7L);
      assertThat(redeliveryPolicy.getMaximumRedeliveryDelay()).isEqualTo(8L);
    }
  }

  private JmsConfigurationFactory createConnectionFactory(final JmsBrokerType jmsBrokerType) {
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_BROKER_TYPE, JmsBrokerType.class))
        .thenReturn(jmsBrokerType);

    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_BROKER_CLIENT_KEY_STORE, String.class))
        .thenReturn("key_store");
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_BROKER_CLIENT_KEY_STORE_SECRET, String.class))
        .thenReturn("key_store_secret");
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_BROKER_CLIENT_TRUST_STORE, String.class))
        .thenReturn("trust_store");
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_BROKER_CLIENT_TRUST_STORE_SECRET,
            String.class))
        .thenReturn("trust_store_secret");

    if (jmsBrokerType == JmsBrokerType.ACTIVE_MQ) {
      this.setupActiveMq();
    } else if (jmsBrokerType == JmsBrokerType.ARTEMIS) {
      this.setupArtemis();
    }

    try {
      return new JmsConfigurationFactory(
          this.environment, this.defaultJmsConfiguration, this.propertyPrefix);
    } catch (final SSLException e) {
      throw new RuntimeException(e);
    }
  }

  private void setupArtemis() {
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_BROKER_URL, String.class))
        .thenReturn("tcp://localhost:61616");
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_CONNECTION_QUEUE_CONSUMER_WINDOW_SIZE,
            int.class))
        .thenReturn(1);
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_MAX_THREAD_POOL_SIZE, int.class))
        .thenReturn(3);

    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_BROKER_USERNAME, String.class))
        .thenReturn("user");
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_BROKER_SECRET, String.class))
        .thenReturn("pass");
  }

  private void setupActiveMq() {
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_BROKER_URL, String.class))
        .thenReturn("ssl://localhost:61616");
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_CONNECTION_QUEUE_PREFETCH, int.class))
        .thenReturn(1);
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_CONNECTION_SEND_TIMEOUT, int.class))
        .thenReturn(2);
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_TRUST_ALL_PACKAGES, boolean.class))
        .thenReturn(true);
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_MAX_THREAD_POOL_SIZE, int.class))
        .thenReturn(3);

    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_BROKER_USERNAME, String.class))
        .thenReturn("user");
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_BROKER_SECRET, String.class))
        .thenReturn("pass");

    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_CONNECTION_MESSAGE_PRIORITY_SUPPORTED,
            boolean.class))
        .thenReturn(true);

    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_USE_EXPONENTIAL_BACK_OFF, boolean.class))
        .thenReturn(true);
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_BACK_OFF_MULTIPLIER, double.class))
        .thenReturn(2.0);
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_MAXIMUM_REDELIVERIES, int.class))
        .thenReturn(6);
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_INITIAL_REDELIVERY_DELAY, long.class))
        .thenReturn(8l);
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_REDELIVERY_DELAY, long.class))
        .thenReturn(9l);
    when(this.environment.getProperty(
            this.propertyPrefix + "." + PROPERTY_NAME_MAXIMUM_REDELIVERY_DELAY, long.class))
        .thenReturn(10l);
  }
}
