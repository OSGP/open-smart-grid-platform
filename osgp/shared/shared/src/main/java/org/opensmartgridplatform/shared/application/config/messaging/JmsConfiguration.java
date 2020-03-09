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
