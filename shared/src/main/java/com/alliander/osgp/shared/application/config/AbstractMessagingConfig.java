/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.application.config;

import java.util.Properties;

import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;

import com.alliander.osgp.shared.application.config.jms.JmsConfigurationFactory;

/**
 * This abstract class can be used by modules to configure to Jms configuration
 * with little code. The base class only needs to provide the names of the
 * request / responses / logging queue the rest is filled in by this class with
 * default values. The base class can overwrite these values by providing its
 * own property (using one of the keys below) in value in the corresponding
 * properties file.
 */
public abstract class AbstractMessagingConfig extends AbstractConfig {

    @Value("${jms.activemq.broker.url:tcp://localhost:61616}")
    protected String aciveMqBroker;

    @Value("${jms.activemq.connection.pool.size:10}")
    protected int connectionPoolSize;

    @Bean
    protected PropertiesFactoryBean propertiesFactoryBean() {
        return new PropertiesFactoryBean();
    }

    @Bean
    protected JmsConfigurationFactory jmsConfigurationFactory(
            @Qualifier("propertiesFactoryBean") final Properties properties,
            final PooledConnectionFactory pooledConnectionFactory, final RedeliveryPolicyMap redeliveryPolicyMap) {
        return new JmsConfigurationFactory(this.environment, pooledConnectionFactory, redeliveryPolicyMap);
    }

    @Bean(destroyMethod = "stop")
    protected PooledConnectionFactory pooledConnectionFactory() {
        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(this.connectionFactory());
        pooledConnectionFactory.setMaxConnections(this.connectionPoolSize);
        return pooledConnectionFactory;
    }

    protected ActiveMQConnectionFactory connectionFactory() {
        final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setRedeliveryPolicyMap(this.redeliveryPolicyMap());
        activeMQConnectionFactory.setBrokerURL(this.aciveMqBroker);
        activeMQConnectionFactory.setNonBlockingRedelivery(true);
        return activeMQConnectionFactory;
    }

    @Bean
    protected RedeliveryPolicyMap redeliveryPolicyMap() {
        return new RedeliveryPolicyMap();
    }

}
