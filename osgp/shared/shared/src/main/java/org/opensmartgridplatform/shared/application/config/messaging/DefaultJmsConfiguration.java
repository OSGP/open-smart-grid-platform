//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.application.config.messaging;

import org.springframework.beans.factory.annotation.Value;

/** This class provides the default configuration properties used for JMS messaging. */
public class DefaultJmsConfiguration implements JmsConfiguration {

  @Value("${jms.default.broker.type:ACTIVE_MQ}")
  private String jmsDefaultBrokerType;

  @Value("${jms.default.broker.url:failover:(tcp://localhost:61616)}")
  private String jmsDefaultBrokerUrl;

  @Value("${jms.default.queue:osgp-default}")
  private String jmsDefaultQueue;

  @Value("${jms.default.connection.pool.size:10}")
  private int jmsDefaultConnectionPoolSize;

  @Value("${jms.default.connection.pool.max.active.sessions:500}")
  private int jmsDefaultConnectionPoolMaxActiveSessions;

  @Value("${jms.default.connection.pool.block.if.session.pool.is.full:true}")
  private boolean jmsDefaultConnectionPoolBlockIfSessionPoolIsFull;

  @Value("${jms.default.connection.pool.block.if.session.pool.is.full.timeout:-1}")
  private long jmsDefaultConnectionPoolBlockIfSessionPoolIsFullTimeout;

  @Value("${jms.default.connection.pool.expiry.timeout:0}")
  private long jmsDefaultConnectionPoolExpiryTimeout;

  @Value("${jms.default.connection.pool.time.between.expiration.check.millis:-1}")
  private long jmsDefaultConnectionPoolTimeBetweenExpirationCheckMillis;

  @Value("${jms.default.connection.pool.idle.timeout:30000}")
  private int jmsDefaultConnectionPoolIdleTimeout;

  @Value("${jms.default.connection.queue.prefetch:1000}")
  private int jmsDefaultConnectionQueuePrefetch;

  @Value("${jms.default.connection.queue.consumer.window.size:1000}")
  private int jmsDefaultConnectionQueueConsumerWindowSize;

  @Value("${jms.default.connection.message.priority.supported:true}")
  private boolean jmsDefaultConnectionMessagePrioritySupported;

  @Value("${jms.default.connection.send.timeout:0}")
  private int jmsDefaultConnectionSendTimeout;

  @Value("${jms.default.trust.all.packages:true}")
  private boolean jmsDefaultTrustAllPackages;

  @Value("${jms.default.trusted.packages:org.opensmartgridplatform,org.joda.time,java.util}")
  private String jmsDefaultTrustedPackages;

  @Value("${jms.default.broker.client.key.store:/etc/osp/activemq/client.ks}")
  private String jmsDefaultBrokerClientKeyStore;

  @Value("${jms.default.broker.client.key.store.pwd:password}")
  private String jmsDefaultBrokerClientKeyStorePwd;

  @Value("${jms.default.broker.client.trust.store:/etc/osp/activemq/client.ts}")
  private String jmsDefaultBrokerClientTrustStore;

  @Value("${jms.default.broker.client.trust.store.pwd:password}")
  private String jmsDefaultBrokerClientTrustStorePwd;

  @Value("${jms.default.broker.username:#{null}}")
  private String jmsDefaultBrokerUsername;

  @Value("${jms.default.broker.password:#{null}}")
  private String jmsDefaultBrokerPassword;

  // Message Listener Container fields
  @Value("${jms.default.max.concurrent.consumers:10}")
  private int jmsDefaultMaxConcurrentConsumers;

  @Value("${jms.default.concurrent.consumers:2}")
  private int jmsDefaultConcurrentConsumers;

  // Thread pool size
  @Value("${jms.default.max.thread.pool.size:10}")
  private int jmsDefaultMaxThreadPoolSize;

  // Redelivery Policy fields
  @Value("${jms.default.use.exponential.back.off:true}")
  private boolean jmsDefaultUseExponentialBackOff;

  @Value("${jms.default.back.off.multiplier:2}")
  private double jmsDefaultBackOffMultiplier;

  @Value("${jms.default.maximum.redelivery.delay:300000}")
  private long jmsDefaultMaximumRedeliveryDelay;

  @Value("${jms.default.initial.redelivery.delay:60000}")
  private long jmsDefaultInitialRedeliveryDelay;

  @Value("${jms.default.maximum.redeliveries:3}")
  private int jmsDefaultMaximumRedeliveries;

  @Value("${jms.default.redelivery.delay:60000}")
  private long jmsDefaultRedeliveryDelay;

  // JMS Template fields
  @Value("${jms.default.delivery.persistent:true}")
  private boolean jmsDefaultDeliveryPersistent;

  @Value("${jms.default.time.to.live:3600000}")
  private long jmsDefaultTimeToLive;

  @Value("${jms.default.explicit.qos.enabled:true}")
  private boolean jmsDefaultExplicitQosEnabled;

  @Override
  public JmsBrokerType getBrokerType() {
    return JmsBrokerType.valueOf(this.jmsDefaultBrokerType);
  }

  @Override
  public String getBrokerUrl() {
    return this.jmsDefaultBrokerUrl;
  }

  @Override
  public int getConnectionPoolSize() {
    return this.jmsDefaultConnectionPoolSize;
  }

  @Override
  public int getConnectionPoolMaxActiveSessions() {
    return this.jmsDefaultConnectionPoolMaxActiveSessions;
  }

  @Override
  public boolean isConnectionPoolBlockIfSessionPoolIsFull() {
    return this.jmsDefaultConnectionPoolBlockIfSessionPoolIsFull;
  }

  @Override
  public long getConnectionPoolBlockIfSessionPoolIsFullTimeout() {
    return this.jmsDefaultConnectionPoolBlockIfSessionPoolIsFullTimeout;
  }

  @Override
  public long getConnectionPoolExpiryTimeout() {
    return this.jmsDefaultConnectionPoolExpiryTimeout;
  }

  @Override
  public long getConnectionPoolTimeBetweenExpirationCheckMillis() {
    return this.jmsDefaultConnectionPoolTimeBetweenExpirationCheckMillis;
  }

  @Override
  public int getConnectionPoolIdleTimeout() {
    return this.jmsDefaultConnectionPoolIdleTimeout;
  }

  @Override
  public int getConnectionQueuePrefetch() {
    return this.jmsDefaultConnectionQueuePrefetch;
  }

  @Override
  public int getConnectionQueueConsumerWindowSize() {
    return this.jmsDefaultConnectionQueueConsumerWindowSize;
  }

  @Override
  public boolean isConnectionMessagePrioritySupported() {
    return this.jmsDefaultConnectionMessagePrioritySupported;
  }

  @Override
  public int getConnectionSendTimeout() {
    return this.jmsDefaultConnectionSendTimeout;
  }

  @Override
  public boolean isTrustAllPackages() {
    return this.jmsDefaultTrustAllPackages;
  }

  @Override
  public String getTrustedPackages() {
    return this.jmsDefaultTrustedPackages;
  }

  @Override
  public String getBrokerClientKeyStore() {
    return this.jmsDefaultBrokerClientKeyStore;
  }

  @Override
  public String getBrokerClientKeyStorePwd() {
    return this.jmsDefaultBrokerClientKeyStorePwd;
  }

  @Override
  public String getBrokerClientTrustStore() {
    return this.jmsDefaultBrokerClientTrustStore;
  }

  @Override
  public String getBrokerClientTrustStorePwd() {
    return this.jmsDefaultBrokerClientTrustStorePwd;
  }

  @Override
  public String getBrokerUsername() {
    return this.jmsDefaultBrokerUsername;
  }

  @Override
  public String getBrokerPassword() {
    return this.jmsDefaultBrokerPassword;
  }

  @Override
  public int getMaxConcurrentConsumers() {
    return this.jmsDefaultMaxConcurrentConsumers;
  }

  @Override
  public int getConcurrentConsumers() {
    return this.jmsDefaultConcurrentConsumers;
  }

  @Override
  public boolean isUseExponentialBackOff() {
    return this.jmsDefaultUseExponentialBackOff;
  }

  @Override
  public double getBackOffMultiplier() {
    return this.jmsDefaultBackOffMultiplier;
  }

  @Override
  public long getMaximumRedeliveryDelay() {
    return this.jmsDefaultMaximumRedeliveryDelay;
  }

  @Override
  public long getInitialRedeliveryDelay() {
    return this.jmsDefaultInitialRedeliveryDelay;
  }

  @Override
  public int getMaximumRedeliveries() {
    return this.jmsDefaultMaximumRedeliveries;
  }

  @Override
  public long getRedeliveryDelay() {
    return this.jmsDefaultRedeliveryDelay;
  }

  @Override
  public boolean isDeliveryPersistent() {
    return this.jmsDefaultDeliveryPersistent;
  }

  @Override
  public long getTimeToLive() {
    return this.jmsDefaultTimeToLive;
  }

  @Override
  public boolean isExplicitQosEnabled() {
    return this.jmsDefaultExplicitQosEnabled;
  }

  @Override
  public String getQueue() {
    return this.jmsDefaultQueue;
  }

  @Override
  public int getMaxThreadPoolSize() {
    return this.jmsDefaultMaxThreadPoolSize;
  }
}
