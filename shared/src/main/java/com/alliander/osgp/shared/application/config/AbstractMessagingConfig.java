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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

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

    @Value("${jms.requests.explicit.qos.enabled:true}")
    protected boolean requestQosEnabled;

    @Value("${jms.requests.delivery.persistent:true}")
    protected boolean requestDeliveryPersistent;

    @Value("${jms.requests.time.to.live:3600000}")
    protected long requestTimeToLive;

    @Value("${jms.requests.concurrent.consumers:2}")
    protected int requestConcurrentConsumers;

    @Value("${jms.requests.max.concurrent.consumers:10}")
    protected int requestMaxConcurrentConsumers;

    @Value("${jms.requests.maximum.redeliveries:3}")
    protected int requestMaxRedeliveries;

    @Value("${jms.requests.initial.redelivery.delay:60000}")
    protected long requestInitialRedeliveryDelay;

    @Value("${jms.requests.redelivery.delay:60000}")
    protected int requestRedeliveryDelay;

    @Value("${jms.requests.maximum.redelivery.delay:300000}")
    protected long requestMaxRedeliveryDelay;

    @Value("${jms.requests.back.off.multiplier:2}")
    protected long requestBackOffMultiplier;

    @Value("${jms.requests.use.exponential.back.off:true}")
    protected boolean requestUseExpBackOff;

    @Value("${jms.responses.explicit.qos.enabled:true}")
    protected boolean responsesQosEnabled;

    @Value("${jms.responses.delivery.persistent:true}")
    protected boolean responsesDeliveryPersistent;

    @Value("${jms.responses.time.to.live:3600000}")
    protected long responsesTimeToLive;

    @Value("${jms.responses.concurrent.consumers:2}")
    protected int responsesConcurrentConsumers;

    @Value("${jms.responses.max.concurrent.consumers:10}")
    protected int responsesMaxConcurrentConsumers;

    @Value("${jms.responses.maximum.redeliveries:3}")
    protected int responsesMaxRedeliveries;

    @Value("${jms.responses.initial.redelivery.delay:60000}")
    protected long responsesInitialRedeliveryDelay;

    @Value("${jms.responses.redelivery.delay:60000}")
    protected int responsesRedeliveryDelay;

    @Value("${jms.responses.maximum.redelivery.delay:300000}")
    protected long responsesMaxRedeliveryDelay;

    @Value("${jms.responses.back.off.multiplier:2}")
    protected long responsesBackOffMultiplier;

    @Value("${jms.responses.use.exponential.back.off:true}")
    protected boolean responsesUseExpBackOff;

    @Value("${jms.logging.explicit.qos.enabled:true}")
    protected boolean loggingQosEnabled;

    @Value("${jms.logging.delivery.persistent:true}")
    protected boolean loggingDeliveryPersistent;

    @Value("${jms.logging.time.to.live:3600000}")
    protected long loggingTimeToLive;

    @Value("${jms.logging.concurrent.consumers:2}")
    protected int loggingConcurrentConsumers;

    @Value("${jms.logging.max.concurrent.consumers:10}")
    protected int loggingMaxConcurrentConsumers;

    @Value("${jms.logging.maximum.redeliveries:3}")
    protected int loggingMaxRedeliveries;

    @Value("${jms.logging.initial.redelivery.delay:60000}")
    protected long loggingInitialRedeliveryDelay;

    @Value("${jms.logging.redelivery.delay:60000}")
    protected int loggingRedeliveryDelay;

    @Value("${jms.logging.maximum.redelivery.delay:300000}")
    protected long loggingMaxRedeliveryDelay;

    @Value("${jms.logging.back.off.multiplier:2}")
    protected long loggingBackOffMultiplier;

    @Value("${jms.logging.use.exponential.back.off:true}")
    protected boolean loggingUseExpBackOff;

    protected Environment environment;

    @Override
    @Autowired
    public void setConfigurableEnvironment(final ConfigurableEnvironment configurableEnvironment) {
        this.environment = configurableEnvironment;
    }

    @Bean
    protected PropertiesFactoryBean propertiesFactoryBean() {
        return new PropertiesFactoryBean();
    }

    @Bean
    protected JmsConfigurationFactory JmsConfigurationFactory(
            @Qualifier("propertiesFactoryBean") final Properties properties,
            final PooledConnectionFactory pooledConnectionFactory, final RedeliveryPolicyMap redeliveryPolicyMap) {
        return new JmsConfigurationFactory(this.environment, pooledConnectionFactory, redeliveryPolicyMap);
    }

    @Bean(destroyMethod = "stop")
    protected PooledConnectionFactory pooledConnectionFactory() {
        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(this.connectionFactory());
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
        final RedeliveryPolicyMap redeliveryPolicyMap = new RedeliveryPolicyMap();
        // redeliveryPolicyMap.put(this.requestsQueue(),
        // this.requestsRedeliveryPolicy(this.requestsQueue()));
        // redeliveryPolicyMap.put(this.responsesQueue(),
        // this.responsesRedeliveryPolicy(this.responsesQueue()));
        // redeliveryPolicyMap.put(this.loggingQueue(),
        // this.loggingRedeliveryPolicy(this.loggingQueue()));
        return redeliveryPolicyMap;
    }

}
