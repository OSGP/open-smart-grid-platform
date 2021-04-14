/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.domain;

import org.opensmartgridplatform.shared.application.config.messaging.JmsConfiguration;
import org.springframework.beans.factory.annotation.Value;

public class DefaultDomainJmsConfiguration implements JmsConfiguration {

  @Value("${jms.domain.default.broker.url:failover:(tcp://localhost:61616)}")
  private String jmsDefaultBrokerUrl;

  @Value("${jms.domain.default.queue:osgp-default-domain}")
  private String jmsDefaultQueue;

  @Value("${jms.domain.default.connection.pool.size:10}")
  private int jmsDefaultConnectionPoolSize;

  @Value("${jms.domain.default.connection.pool.max.active.sessions:500}")
  private int jmsDefaultConnectionPoolMaxActiveSessions;

  @Value("${jms.domain.default.connection.pool.block.if.session.pool.is.full:true}")
  private boolean jmsDefaultConnectionPoolBlockIfSessionPoolIsFull;

  @Value("${jms.domain.default.connection.pool.block.if.session.pool.is.full.timeout:-1}")
  private long jmsDefaultConnectionPoolBlockIfSessionPoolIsFullTimeout;

  @Value("${jms.domain.default.connection.pool.expiry.timeout:0}")
  private long jmsDefaultConnectionPoolExpiryTimeout;

  @Value("${jms.domain.default.connection.pool.time.between.expiration.check.millis:-1}")
  private long jmsDefaultConnectionPoolTimeBetweenExpirationCheckMillis;

  @Value("${jms.domain.default.connection.pool.idle.timeout:30000}")
  private int jmsDefaultConnectionPoolIdleTimeout;

  @Value("${jms.domain.default.connection.queue.prefetch:1000}")
  private int jmsDefaultConnectionQueuePrefetch;

  @Value("${jms.domain.default.connection.send.timeout:0}")
  private int jmsDefaultConnectionSendTimeout;

  @Value("${jms.domain.default.trust.all.packages:true}")
  private boolean jmsDefaultTrustAllPackages;

  @Value("${jms.domain.default.trusted.packages:org.opensmartgridplatform,org.joda.time,java.util}")
  private String jmsDefaultTrustedPackages;

  @Value("${jms.domain.default.broker.client.key.store:/etc/osp/activemq/client.ks}")
  private String jmsDefaultBrokerClientKeyStore;

  @Value("${jms.domain.default.broker.client.key.store.pwd:password}")
  private String jmsDefaultBrokerClientKeyStorePwd;

  @Value("${jms.domain.default.broker.client.trust.store:/etc/osp/activemq/client.ts}")
  private String jmsDefaultBrokerClientTrustStore;

  @Value("${jms.domain.default.broker.client.trust.store.pwd:password}")
  private String jmsDefaultBrokerClientTrustStorePwd;

  @Value("${jms.domain.default.broker.username:#{null}}")
  private String jmsDefaultBrokerUsername;

  @Value("${jms.domain.default.broker.password:#{null}}")
  private String jmsDefaultBrokerPassword;

  // === MESSAGE LISTENER SETTINGS ===

  @Value("${jms.domain.default.max.concurrent.consumers:10}")
  private int jmsDefaultMaxConcurrentConsumers;

  @Value("${jms.domain.default.concurrent.consumers:2}")
  private int jmsDefaultConcurrentConsumers;

  // Thread pool size
  @Value("${jms.domain.default.max.thread.pool.size:10}")
  private int jmsDefaultMaxThreadPoolSize;

  // === REDELIVERY POLICY SETTINGS ===

  @Value("${jms.domain.default.use.exponential.back.off:true}")
  private boolean jmsDefaultUseExponentialBackOff;

  @Value("${jms.domain.default.back.off.multiplier:2}")
  private double jmsDefaultBackOffMultiplier;

  @Value("${jms.domain.default.maximum.redelivery.delay:60000}")
  private long jmsDefaultMaximumRedeliveryDelay;

  @Value("${jms.domain.default.initial.redelivery.delay:0}")
  private long jmsDefaultInitialRedeliveryDelay;

  @Value("${jms.domain.default.maximum.redeliveries:3}")
  private int jmsDefaultMaximumRedeliveries;

  @Value("${jms.domain.default.redelivery.delay:10000}")
  private long jmsDefaultRedeliveryDelay;

  // === JMS TEMPLATE SETTINGS ===

  @Value("${jms.domain.default.delivery.persistent:true}")
  private boolean jmsDefaultDeliveryPersistent;

  @Value("${jms.domain.default.time.to.live:3600000}")
  private long jmsDefaultTimeToLive;

  @Value("${jms.domain.default.explicit.qos.enabled:true}")
  private boolean jmsDefaultExplicitQosEnabled;

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
