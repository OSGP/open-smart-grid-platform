/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config;

import java.util.Arrays;

import javax.net.ssl.SSLException;

import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.opensmartgridplatform.shared.application.config.jms.JmsBrokerSslSettings;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * This class provides the basic components used for JMS messaging.
 */
public abstract class AbstractMessagingConfig extends AbstractConfig {

    @Value("${jms.activemq.broker.url:ssl://localhost:61617}")
    private String activeMqBroker;

    @Value("${jms.activemq.connection.pool.size:10}")
    private int connectionPoolSize;

    @Value("${jms.activemq.connection.pool.max.active.sessions:500}")
    private int maximumActiveSessionPerConnection;

    @Value("${jms.activemq.connection.pool.block.if.session.pool.is.full:true}")
    private boolean blockIfSessionPoolIsFull;

    @Value("${jms.activemq.connection.pool.block.if.session.pool.is.full.timeout:-1}")
    private long blockIfSessionPoolIsFullTimeout;

    @Value("${jms.activemq.connection.pool.expiry.timeout:0}")
    private long expiryTimeout;

    @Value("${jms.activemq.connection.pool.time.between.expiration.check.millis:-1}")
    private long timeBetweenExpirationCheckMillis;

    @Value("${jms.activemq.connection.pool.idle.timeout:30000}")
    private int idleTimeout;

    @Value("${jms.activemq.connection.queue.prefetch:1000}")
    private int queuePrefetch;

    @Value("${jms.activemq.connection.send.timeout:0}")
    private int sendTimeout;

    @Value("${jms.activemq.trust.all.packages:true}")
    private boolean trustAllPackages;

    @Value("${jms.activemq.trusted.packages:org.opensmartgridplatform,org.joda.time,java.util}")
    private String trustedPackages;

    @Value("${jms.activemq.broker.client.key.store:/etc/ssl/certs/activemq-osgp-client.ks}")
    private String clientKeyStore;

    @Value("${jms.activemq.broker.client.key.store.pwd:1234}")
    private String clientKeyStorePwd;

    @Value("${jms.activemq.broker.client.trust.store:/etc/ssl/certs/trust.jks}")
    private String trustKeyStore;

    @Value("${jms.activemq.broker.client.trust.store.pwd:123456}")
    private String trustKeyStorePwd;

    protected String getActiveMQBroker() {
        return this.activeMqBroker;
    }

    protected int getConnectionPoolSize() {
        return this.connectionPoolSize;
    }

    protected int getMaximumActiveSessionPerConnection() {
        return this.maximumActiveSessionPerConnection;
    }

    protected int getQueuePrefetch() {
        return this.queuePrefetch;
    }

    @Bean
    protected JmsConfigurationFactory jmsConfigurationFactory(final PooledConnectionFactory pooledConnectionFactory,
            final RedeliveryPolicyMap redeliveryPolicyMap) {
        return new JmsConfigurationFactory(this.environment, pooledConnectionFactory, redeliveryPolicyMap);
    }

    @Bean(destroyMethod = "stop")
    protected PooledConnectionFactory pooledConnectionFactory() throws SSLException {
        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(this.connectionFactory());
        pooledConnectionFactory.setMaxConnections(this.getConnectionPoolSize());
        pooledConnectionFactory.setMaximumActiveSessionPerConnection(this.getMaximumActiveSessionPerConnection());
        pooledConnectionFactory.setBlockIfSessionPoolIsFull(this.blockIfSessionPoolIsFull);
        if (this.blockIfSessionPoolIsFull) {
            pooledConnectionFactory.setBlockIfSessionPoolIsFullTimeout(this.blockIfSessionPoolIsFullTimeout);
        }
        pooledConnectionFactory.setExpiryTimeout(this.expiryTimeout);
        pooledConnectionFactory.setIdleTimeout(this.idleTimeout);
        pooledConnectionFactory.setTimeBetweenExpirationCheckMillis(this.timeBetweenExpirationCheckMillis);
        return pooledConnectionFactory;
    }

    protected ActiveMQSslConnectionFactory connectionFactory() throws SSLException {
        final ActiveMQPrefetchPolicy activeMQPrefetchPolicy = new ActiveMQPrefetchPolicy();
        activeMQPrefetchPolicy.setQueuePrefetch(this.getQueuePrefetch());

        final ActiveMQSslConnectionFactory activeMQConnectionFactory = new ActiveMQSslConnectionFactory();

        activeMQConnectionFactory.setRedeliveryPolicyMap(this.redeliveryPolicyMap());
        activeMQConnectionFactory.setBrokerURL(this.getActiveMQBroker());
        activeMQConnectionFactory.setNonBlockingRedelivery(true);
        activeMQConnectionFactory.setPrefetchPolicy(activeMQPrefetchPolicy);
        activeMQConnectionFactory.setSendTimeout(this.sendTimeout);
        activeMQConnectionFactory.setTrustAllPackages(this.trustAllPackages);
        if (!this.trustAllPackages) {
            activeMQConnectionFactory.setTrustedPackages(Arrays.asList(this.trustedPackages.split(",")));
        }

        final JmsBrokerSslSettings jmsBrokerSslSettings = new JmsBrokerSslSettings(this.clientKeyStore,
                this.clientKeyStorePwd, this.trustKeyStore, this.trustKeyStorePwd);
        jmsBrokerSslSettings.applyToFactory(activeMQConnectionFactory);

        return activeMQConnectionFactory;
    }

    @Bean
    protected RedeliveryPolicyMap redeliveryPolicyMap() {
        final RedeliveryPolicyMap redeliveryPolicyMap = new RedeliveryPolicyMap();
        redeliveryPolicyMap.setDefaultEntry(this.defaultRedeliveryPolicy());
        return redeliveryPolicyMap;
    }

    @Bean
    public RedeliveryPolicy defaultRedeliveryPolicy() {
        return new RedeliveryPolicy();
    }
}
