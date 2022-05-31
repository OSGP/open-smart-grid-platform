/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config.messaging;

public interface JmsConfiguration {

  String getBrokerUrl();

  int getConnectionPoolSize();

  int getConnectionPoolMaxActiveSessions();

  int getMaxThreadPoolSize();

  boolean isConnectionPoolBlockIfSessionPoolIsFull();

  long getConnectionPoolBlockIfSessionPoolIsFullTimeout();

  long getConnectionPoolExpiryTimeout();

  long getConnectionPoolTimeBetweenExpirationCheckMillis();

  int getConnectionPoolIdleTimeout();

  int getConnectionQueuePrefetch();

  boolean isConnectionMessagePrioritySupported();

  int getConnectionSendTimeout();

  boolean isTrustAllPackages();

  String getTrustedPackages();

  String getBrokerClientKeyStore();

  String getBrokerClientKeyStorePwd();

  String getBrokerClientTrustStore();

  String getBrokerClientTrustStorePwd();

  String getBrokerUsername();

  String getBrokerPassword();

  int getMaxConcurrentConsumers();

  int getConcurrentConsumers();

  boolean isUseExponentialBackOff();

  double getBackOffMultiplier();

  long getMaximumRedeliveryDelay();

  long getInitialRedeliveryDelay();

  int getMaximumRedeliveries();

  long getRedeliveryDelay();

  boolean isDeliveryPersistent();

  long getTimeToLive();

  boolean isExplicitQosEnabled();

  String getQueue();
}
