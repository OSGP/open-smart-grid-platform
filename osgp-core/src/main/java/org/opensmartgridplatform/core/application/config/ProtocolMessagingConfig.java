/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.application.config;

import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import org.opensmartgridplatform.core.infra.jms.JmsTemplateSettings;
import org.opensmartgridplatform.core.infra.jms.protocol.ProtocolRequestMessageJmsTemplateFactory;
import org.opensmartgridplatform.core.infra.jms.protocol.ProtocolResponseMessageListenerContainerFactory;
import org.opensmartgridplatform.core.infra.jms.protocol.in.ProtocolRequestMessageListenerContainerFactory;
import org.opensmartgridplatform.core.infra.jms.protocol.in.ProtocolRequestMessageProcessorMap;
import org.opensmartgridplatform.core.infra.jms.protocol.in.ProtocolResponseMessageJmsTemplateFactory;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.ProtocolInfoRepository;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;

@Configuration
@PropertySource("classpath:osgp-core.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/Core/config}", ignoreResourceNotFound = true)
public class ProtocolMessagingConfig extends AbstractConfig {

    // JMS Settings
    private static final String PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL = "jms.protocol.activemq.broker.url";

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.protocol.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.protocol.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.protocol.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.protocol.default.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_BACK_OFF_MULTIPLIER = "jms.protocol.default.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_DEFAULT_USE_EXPONENTIAL_BACK_OFF = "jms.protocol.default.use.exponential.back.off";

    // JMS Settings: Outgoing protocol requests (send)
    private static final String PROPERTY_NAME_JMS_OUTGOING_PROTOCOL_REQUESTS_EXPLICIT_QOS_ENABLED = "jms.outgoing.protocol.requests.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_OUTGOING_PROTOCOL_REQUESTS_DELIVERY_PERSISTENT = "jms.outgoing.protocol.requests.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_OUTGOING_PROTOCOL_REQUESTS_TIME_TO_LIVE = "jms.outgoing.protocol.requests.time.to.live";

    // JMS Settings: Incoming protocol responses (receive)
    private static final String PROPERTY_NAME_JMS_INCOMING_PROTOCOL_RESPONSES_CONCURRENT_CONSUMERS = "jms.incoming.protocol.responses.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_INCOMING_PROTOCOL_RESPONSES_MAX_CONCURRENT_CONSUMERS = "jms.incoming.protocol.responses.max.concurrent.consumers";

    // JMS Settings: Incoming protocol requests (receive)
    private static final String PROPERTY_NAME_JMS_INCOMING_PROTOCOL_REQUESTS_CONCURRENT_CONSUMERS = "jms.incoming.protocol.requests.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_INCOMING_PROTOCOL_REQUESTS_MAX_CONCURRENT_CONSUMERS = "jms.incoming.protocol.requests.max.concurrent.consumers";

    // JMS Settings: Outgoing protocol responses (send)
    private static final String PROPERTY_NAME_JMS_OUTGOING_PROTOCOL_RESPONSES_EXPLICIT_QOS_ENABLED = "jms.outgoing.protocol.responses.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_OUTGOING_PROTOCOL_RESPONSES_TIME_TO_LIVE = "jms.outgoing.protocol.responses.time.to.live";
    private static final String PROPERTY_NAME_JMS_OUTGOING_PROTOCOL_RESPONSES_DELIVERY_PERSISTENT = "jms.outgoing.protocol.responses.delivery.persistent";

    private static final String PROPERTY_NAME_MAX_RETRY_COUNT = "max.retry.count";

    @Autowired
    private DomainInfoRepository domainInfoRepository;

    @Autowired
    private ProtocolInfoRepository protocolInfoRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolMessagingConfig.class);

    @Autowired
    @Qualifier("osgpCoreIncomingProtocolRequestMessageProcessorMap")
    private ProtocolRequestMessageProcessorMap protocolRequestMessageProcessorMap;

    @Bean(destroyMethod = "stop")
    public PooledConnectionFactory protocolPooledConnectionFactory() {
        LOGGER.debug("Creating bean: pooledConnectionFactory");

        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(this.protocolConnectionFactory());
        return pooledConnectionFactory;
    }

    @Bean
    public ActiveMQConnectionFactory protocolConnectionFactory() {
        LOGGER.debug("Creating bean: connectionFactory");

        final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setRedeliveryPolicyMap(this.protocolRedeliveryPolicyMap());
        activeMQConnectionFactory
                .setBrokerURL(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL));

        activeMQConnectionFactory.setNonBlockingRedelivery(true);

        return activeMQConnectionFactory;
    }

    @Bean
    public RedeliveryPolicyMap protocolRedeliveryPolicyMap() {
        LOGGER.debug("Creating bean: redeliveryPolicyMap");

        final RedeliveryPolicyMap redeliveryPolicyMap = new RedeliveryPolicyMap();
        redeliveryPolicyMap.setDefaultEntry(this.defaultProtocolRedeliveryPolicy());
        return redeliveryPolicyMap;
    }

    @Bean
    public RedeliveryPolicy defaultProtocolRedeliveryPolicy() {
        LOGGER.debug("Creating bean: defaultRedeliveryPolicy");

        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();

        redeliveryPolicy.setInitialRedeliveryDelay(Long
                .parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(
                Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long
                .parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(
                Long.parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY)));
        redeliveryPolicy.setBackOffMultiplier(Double
                .parseDouble(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_USE_EXPONENTIAL_BACK_OFF)));

        return redeliveryPolicy;
    }

    // === OUTGOING PROTOCOL REQUESTS ===
    // beans used for sending protocol request messages

    @Bean
    public ProtocolRequestMessageJmsTemplateFactory protocolRequestsJmsTemplate() {
        final JmsTemplateSettings jmsTemplateSettings = new JmsTemplateSettings(
                Boolean.parseBoolean(this.environment
                        .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_PROTOCOL_REQUESTS_EXPLICIT_QOS_ENABLED)),
                Long.parseLong(this.environment
                        .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_PROTOCOL_REQUESTS_TIME_TO_LIVE)),
                Boolean.parseBoolean(this.environment
                        .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_PROTOCOL_REQUESTS_DELIVERY_PERSISTENT)));

        return new ProtocolRequestMessageJmsTemplateFactory(this.protocolPooledConnectionFactory(), jmsTemplateSettings,
                this.protocolInfoRepository.findAll());
    }

    // === INCOMING PROTOCOL RESPONSES ===
    // beans used for receiving protocol response messages

    @Bean
    public ProtocolResponseMessageListenerContainerFactory protocolResponseMessageListenerContainer() {
        final ProtocolResponseMessageListenerContainerFactory messageListenerContainer = new ProtocolResponseMessageListenerContainerFactory(
                this.protocolInfoRepository.findAll());
        messageListenerContainer.setConnectionFactory(this.protocolPooledConnectionFactory());
        messageListenerContainer.setConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_PROTOCOL_RESPONSES_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_PROTOCOL_RESPONSES_MAX_CONCURRENT_CONSUMERS)));

        return messageListenerContainer;
    }

    // === INCOMING PROTOCOL REQUESTS ===
    // beans used for receiving incoming protocol request messages

    @Bean
    public ProtocolRequestMessageListenerContainerFactory protocolRequestMessageListenerContainer() {
        final ProtocolRequestMessageListenerContainerFactory messageListenerContainer = new ProtocolRequestMessageListenerContainerFactory(
                this.protocolInfoRepository.findAll(), this.domainInfoRepository.findAll(),
                this.protocolRequestMessageProcessorMap);
        messageListenerContainer.setConnectionFactory(this.protocolPooledConnectionFactory());
        messageListenerContainer.setConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_PROTOCOL_REQUESTS_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_PROTOCOL_REQUESTS_MAX_CONCURRENT_CONSUMERS)));

        return messageListenerContainer;
    }

    // === OUTGOING PROTOCOL RESPONSES ===
    // beans used for sending protocol response messages

    @Bean
    public ProtocolResponseMessageJmsTemplateFactory protocolResponseJmsTemplateFactory() {
        final JmsTemplateSettings jmsTemplateSettings = new JmsTemplateSettings(
                Boolean.parseBoolean(this.environment
                        .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_PROTOCOL_RESPONSES_EXPLICIT_QOS_ENABLED)),
                Long.parseLong(this.environment
                        .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_PROTOCOL_RESPONSES_TIME_TO_LIVE)),
                Boolean.parseBoolean(this.environment
                        .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_PROTOCOL_RESPONSES_DELIVERY_PERSISTENT)));

        return new ProtocolResponseMessageJmsTemplateFactory(this.protocolPooledConnectionFactory(),
                jmsTemplateSettings, this.protocolInfoRepository.findAll());
    }

    // The Max count to retry a failed response

    @Bean
    public int getMaxRetryCount() {
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_MAX_RETRY_COUNT));
    }
}
