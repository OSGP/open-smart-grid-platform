/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config.messaging;

import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.springframework.beans.factory.annotation.Value;

/**
 * This class provides the basic components used for JMS messaging.
 */
public class JmsDefaultConfig extends AbstractConfig {

    @Value("${jms.default.broker.url:failover:(ssl://localhost:61617)}")
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

    // Redelivery Policy fields, default values are corresponding to the
    // defaults in the RedeliveryPolicy class,but could be overridden by a value
    // in the properties files
    @Value("${jms.default.use.exponential.back.off:false}")
    private boolean jmsDefaultUseExponentialBackOff;

    @Value("${jms.default.back.off.multiplier:5}")
    private double jmsDefaultBackOffMultiplier;

    @Value("${jms.default.maximum.redelivery.delay:-1}")
    private long jmsDefaultMaximumRedeliveryDelay;

    @Value("${jms.default.initial.redelivery.delay:1000}")
    private long jmsDefaultInitialRedeliveryDelay;

    @Value("${jms.default.maximum.redeliveries:6}")
    private int jmsDefaultMaximumRedeliveries;

    @Value("${jms.default.redelivery.delay:1000}")
    private long jmsDefaultRedeliveryDelay;

    // JMS Template fields
    @Value("${jms.default.delivery.persistent:true}")
    private boolean jmsDefaultDeliveryPersistent;

    @Value("${jms.default.time.to.live:3600}")
    private long jmsDefaultTimeToLive;

    @Value("${jms.default.explicit.qos.enabled:true}")
    private boolean jmsDefaultExplicitQosEnabled;

    public String getJmsDefaultBrokerUrl() {
        return this.jmsDefaultBrokerUrl;
    }

    public int getJmsDefaultConnectionPoolSize() {
        return this.jmsDefaultConnectionPoolSize;
    }

    public int getJmsDefaultConnectionPoolMaxActiveSessions() {
        return this.jmsDefaultConnectionPoolMaxActiveSessions;
    }

    public boolean isJmsDefaultConnectionPoolBlockIfSessionPoolIsFull() {
        return this.jmsDefaultConnectionPoolBlockIfSessionPoolIsFull;
    }

    public long getJmsDefaultConnectionPoolBlockIfSessionPoolIsFullTimeout() {
        return this.jmsDefaultConnectionPoolBlockIfSessionPoolIsFullTimeout;
    }

    public long getJmsDefaultConnectionPoolExpiryTimeout() {
        return this.jmsDefaultConnectionPoolExpiryTimeout;
    }

    public long getJmsDefaultConnectionPoolTimeBetweenExpirationCheckMillis() {
        return this.jmsDefaultConnectionPoolTimeBetweenExpirationCheckMillis;
    }

    public int getJmsDefaultConnectionPoolIdleTimeout() {
        return this.jmsDefaultConnectionPoolIdleTimeout;
    }

    public int getJmsDefaultConnectionQueuePrefetch() {
        return this.jmsDefaultConnectionQueuePrefetch;
    }

    public int getJmsDefaultConnectionSendTimeout() {
        return this.jmsDefaultConnectionSendTimeout;
    }

    public boolean isJmsDefaultTrustAllPackages() {
        return this.jmsDefaultTrustAllPackages;
    }

    public String getJmsDefaultTrustedPackages() {
        return this.jmsDefaultTrustedPackages;
    }

    public String getJmsDefaultBrokerClientKeyStore() {
        return this.jmsDefaultBrokerClientKeyStore;
    }

    public String getJmsDefaultBrokerClientKeyStorePwd() {
        return this.jmsDefaultBrokerClientKeyStorePwd;
    }

    public String getJmsDefaultBrokerClientTrustStore() {
        return this.jmsDefaultBrokerClientTrustStore;
    }

    public String getJmsDefaultBrokerClientTrustStorePwd() {
        return this.jmsDefaultBrokerClientTrustStorePwd;
    }

    public String getJmsDefaultBrokerUsername() {
        return this.jmsDefaultBrokerUsername;
    }

    public String getJmsDefaultBrokerPassword() {
        return this.jmsDefaultBrokerPassword;
    }

    public int getJmsDefaultMaxConcurrentConsumers() {
        return this.jmsDefaultMaxConcurrentConsumers;
    }

    public int getJmsDefaultConcurrentConsumers() {
        return this.jmsDefaultConcurrentConsumers;
    }

    public boolean isJmsDefaultUseExponentialBackOff() {
        return this.jmsDefaultUseExponentialBackOff;
    }

    public double getJmsDefaultBackOffMultiplier() {
        return this.jmsDefaultBackOffMultiplier;
    }

    public long getJmsDefaultMaximumRedeliveryDelay() {
        return this.jmsDefaultMaximumRedeliveryDelay;
    }

    public long getJmsDefaultInitialRedeliveryDelay() {
        return this.jmsDefaultInitialRedeliveryDelay;
    }

    public int getJmsDefaultMaximumRedeliveries() {
        return this.jmsDefaultMaximumRedeliveries;
    }

    public long getJmsDefaultRedeliveryDelay() {
        return this.jmsDefaultRedeliveryDelay;
    }

    public boolean isJmsDefaultDeliveryPersistent() {
        return this.jmsDefaultDeliveryPersistent;
    }

    public long getJmsDefaultTimeToLive() {
        return this.jmsDefaultTimeToLive;
    }

    public boolean isJmsDefaultExplicitQosEnabled() {
        return this.jmsDefaultExplicitQosEnabled;
    }

    public String getJmsDefaultQueue() {
        return this.jmsDefaultQueue;
    }
}
