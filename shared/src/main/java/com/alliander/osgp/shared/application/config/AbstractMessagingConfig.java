/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.application.config;

import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.alliander.osgp.shared.application.config.jms.JmsConfigurationFactory;

/**
 * This class provides the basic components used for JMS messaging.
 */
public abstract class AbstractMessagingConfig extends AbstractConfig {

    @Value("${jms.activemq.broker.url:tcp://localhost:61616}")
    private String activeMqBroker;

    @Value("${jms.activemq.connection.pool.size:10}")
    private int connectionPoolSize;

    @Value("${jms.activemq.connection.pool.max.active.sessions:500}")
    private int maximumActiveSessionPerConnection;

    @Value("${jms.activemq.connection.queue.prefetch:1000}")
    private int queuePrefetch;

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
    protected PooledConnectionFactory pooledConnectionFactory() {
        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(this.connectionFactory());
        pooledConnectionFactory.setMaxConnections(this.getConnectionPoolSize());
        pooledConnectionFactory.setMaximumActiveSessionPerConnection(this.getMaximumActiveSessionPerConnection());
        return pooledConnectionFactory;
    }

    protected ActiveMQConnectionFactory connectionFactory() {
        final ActiveMQPrefetchPolicy activeMQPrefetchPolicy = new ActiveMQPrefetchPolicy();
        activeMQPrefetchPolicy.setQueuePrefetch(this.getQueuePrefetch());

        final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setRedeliveryPolicyMap(this.redeliveryPolicyMap());
        activeMQConnectionFactory.setBrokerURL(this.getActiveMQBroker());
        activeMQConnectionFactory.setNonBlockingRedelivery(true);
        activeMQConnectionFactory.setPrefetchPolicy(activeMQPrefetchPolicy);
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
