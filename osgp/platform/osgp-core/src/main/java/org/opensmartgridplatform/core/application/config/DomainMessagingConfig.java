/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.application.config;

import java.util.Arrays;

import javax.net.ssl.SSLException;

import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.opensmartgridplatform.core.infra.jms.JmsTemplateSettings;
import org.opensmartgridplatform.core.infra.jms.domain.DomainRequestMessageListenerContainerFactory;
import org.opensmartgridplatform.core.infra.jms.domain.DomainResponseMessageJmsTemplateFactory;
import org.opensmartgridplatform.core.infra.jms.domain.in.DomainRequestMessageJmsTemplateFactory;
import org.opensmartgridplatform.core.infra.jms.domain.in.DomainResponseMessageListenerContainerFactory;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.ProtocolInfoRepository;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.jms.JmsBrokerSslSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({ @PropertySource("classpath:osgp-core.properties"),
        @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
        @PropertySource(value = "file:${osgp/Core/config}", ignoreResourceNotFound = true), })
public class DomainMessagingConfig extends AbstractConfig {

    // JMS Settings
    private static final String PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL = "jms.domain.activemq.broker.url";
    private static final String PROPERTY_NAME_JMS_ACTIVEMQ_TRUST_ALL_PACKAGES = "jms.activemq.trust.all.packages";
    private static final String PROPERTY_NAME_JMS_ACTIVEMQ_TRUSTED_PACKAGES = "jms.activemq.trusted.packages";

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.domain.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.domain.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.domain.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.domain.default.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_BACK_OFF_MULTIPLIER = "jms.domain.default.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_DEFAULT_USE_EXPONENTIAL_BACK_OFF = "jms.domain.default.use.exponential.back.off";

    // JMS Settings: Incoming domain requests (receive)
    private static final String PROPERTY_NAME_JMS_INCOMING_DOMAIN_REQUESTS_CONCURRENT_CONSUMERS = "jms.incoming.domain.requests.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_INCOMING_DOMAIN_REQUESTS_MAX_CONCURRENT_CONSUMERS = "jms.incoming.domain.requests.max.concurrent.consumers";

    // JMS Settings: Outgoing domain responses (send)
    private static final String PROPERTY_NAME_JMS_OUTGOING_DOMAIN_RESPONSES_EXPLICIT_QOS_ENABLED = "jms.outgoing.domain.responses.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_OUTGOING_DOMAIN_RESPONSES_DELIVERY_PERSISTENT = "jms.outgoing.domain.responses.delivery.persistent";
    private static final String PROPERTY_NAME_JMS_OUTGOING_DOMAIN_RESPONSES_TIME_TO_LIVE = "jms.outgoing.domain.responses.time.to.live";

    // JMS Settings: Outgoing domain requests (send)
    private static final String PROPERTY_NAME_JMS_OUTGOING_DOMAIN_REQUESTS_EXPLICIT_QOS_ENABLED = "jms.outgoing.domain.requests.explicit.qos.enabled";
    private static final String PROPERTY_NAME_JMS_OUTGOING_DOMAIN_REQUESTS_TIME_TO_LIVE = "jms.outgoing.domain.requests.time.to.live";
    private static final String PROPERTY_NAME_JMS_OUTGOING_DOMAIN_REQUESTS_DELIVERY_PERSISTENT = "jms.outgoing.domain.requests.delivery.persistent";

    // JMS Settings: Incoming domain responses (receive)
    private static final String PROPERTY_NAME_JMS_INCOMING_DOMAIN_RESPONSES_CONCURRENT_CONSUMERS = "jms.incoming.domain.responses.concurrent.consumers";
    private static final String PROPERTY_NAME_JMS_INCOMING_DOMAIN_RESPONSES_MAX_CONCURRENT_CONSUMERS = "jms.incoming.domain.responses.max.concurrent.consumers";

    private static final String PROPERTY_NAME_JMS_GET_POWER_USAGE_HISTORY_REQUEST_TIME_TO_LIVE = "jms.get.power.usage.history.request.time.to.live";

    private static final String PROPERTY_NAME_NETMANAGEMENT_ORGANISATION = "netmanagement.organisation";

    // JMS Settings: SSL settings for the domain requests and responses
    @Value("${jms.domain.activemq.broker.client.key.store:/etc/osp/activemq/client.ks}")
    private String clientKeyStore;

    @Value("${jms.domain.activemq.broker.client.key.store.pwd:password}")
    private String clientKeyStorePwd;

    @Value("${jms.domain.activemq.broker.client.trust.store:/etc/osp/activemq/client.ts}")
    private String trustKeyStore;

    @Value("${jms.domain.activemq.broker.client.trust.store.pwd:password}")
    private String trustKeyStorePwd;

    @Autowired
    private DomainInfoRepository domainInfoRepository;

    @Autowired
    private ProtocolInfoRepository protocolInfoRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainMessagingConfig.class);

    @Bean(destroyMethod = "stop")
    public PooledConnectionFactory domainPooledConnectionFactory() throws SSLException {
        LOGGER.debug("Creating bean: pooledConnectionFactory");

        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(this.domainConnectionFactory());
        return pooledConnectionFactory;
    }

    @Bean
    public ActiveMQSslConnectionFactory domainConnectionFactory() throws SSLException {
        LOGGER.debug("Creating bean: connectionFactory");

        final ActiveMQSslConnectionFactory activeMQConnectionFactory = new ActiveMQSslConnectionFactory();
        activeMQConnectionFactory.setRedeliveryPolicyMap(this.domainRedeliveryPolicyMap());
        activeMQConnectionFactory
                .setBrokerURL(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_ACTIVEMQ_BROKER_URL));

        activeMQConnectionFactory.setNonBlockingRedelivery(true);

        final boolean trustAllPackages = Boolean
                .parseBoolean(this.environment.getProperty(PROPERTY_NAME_JMS_ACTIVEMQ_TRUST_ALL_PACKAGES));
        activeMQConnectionFactory.setTrustAllPackages(trustAllPackages);
        if (!trustAllPackages) {
            activeMQConnectionFactory.setTrustedPackages(Arrays.asList(
                    this.environment.getRequiredProperty(PROPERTY_NAME_JMS_ACTIVEMQ_TRUSTED_PACKAGES).split(",")));
        }

        final JmsBrokerSslSettings jmsBrokerSslSettings = new JmsBrokerSslSettings(this.clientKeyStore,
                this.clientKeyStorePwd, this.trustKeyStore, this.trustKeyStorePwd);
        jmsBrokerSslSettings.applyToFactory(activeMQConnectionFactory);

        return activeMQConnectionFactory;
    }

    @Bean
    public RedeliveryPolicyMap domainRedeliveryPolicyMap() {
        LOGGER.debug("Creating bean: redeliveryPolicyMap");

        final RedeliveryPolicyMap redeliveryPolicyMap = new RedeliveryPolicyMap();
        redeliveryPolicyMap.setDefaultEntry(this.defaultDomainRedeliveryPolicy());
        return redeliveryPolicyMap;
    }

    @Bean
    public RedeliveryPolicy defaultDomainRedeliveryPolicy() {
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

    // === OUTGOING DOMAIN RESPONSES ===
    // beans used for sending domain response messages

    @Bean
    public DomainResponseMessageJmsTemplateFactory domainResponseJmsTemplateFactory() throws SSLException {
        LOGGER.debug("Creating bean: domainResponseJmsTemplateFactory");

        final JmsTemplateSettings jmsTemplateSettings = new JmsTemplateSettings(
                Boolean.parseBoolean(this.environment
                        .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_DOMAIN_RESPONSES_EXPLICIT_QOS_ENABLED)),
                Long.parseLong(
                        this.environment.getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_DOMAIN_RESPONSES_TIME_TO_LIVE)),
                Boolean.parseBoolean(this.environment
                        .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_DOMAIN_RESPONSES_DELIVERY_PERSISTENT)));
        return new DomainResponseMessageJmsTemplateFactory(this.domainPooledConnectionFactory(), jmsTemplateSettings,
                this.domainInfoRepository.findAll());
    }

    // === INCOMING DOMAIN REQUESTS ===
    // beans used for receiving domain request messages

    @Bean
    public DomainRequestMessageListenerContainerFactory domainRequestMessageListenerContainerFactory()
            throws SSLException {
        LOGGER.debug("Creating bean: domainResponseMessageListenerContainerFactory");

        final DomainRequestMessageListenerContainerFactory messageListenerContainer = new DomainRequestMessageListenerContainerFactory(
                this.domainInfoRepository.findAll());
        messageListenerContainer.setConnectionFactory(this.domainPooledConnectionFactory());
        messageListenerContainer.setConcurrentConsumers(Integer.parseInt(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_DOMAIN_REQUESTS_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_DOMAIN_REQUESTS_MAX_CONCURRENT_CONSUMERS)));

        return messageListenerContainer;
    }

    // === OUTGOING DOMAIN REQUESTS ==
    // beans used for sending incoming domain request messages

    @Bean
    public DomainRequestMessageJmsTemplateFactory domainRequestMessageJmsTemplateFactory() throws SSLException {
        final JmsTemplateSettings jmsTemplateSettings = new JmsTemplateSettings(
                Boolean.parseBoolean(this.environment
                        .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_DOMAIN_REQUESTS_EXPLICIT_QOS_ENABLED)),
                Long.parseLong(
                        this.environment.getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_DOMAIN_REQUESTS_TIME_TO_LIVE)),
                Boolean.parseBoolean(this.environment
                        .getRequiredProperty(PROPERTY_NAME_JMS_OUTGOING_DOMAIN_REQUESTS_DELIVERY_PERSISTENT)));

        return new DomainRequestMessageJmsTemplateFactory(this.domainPooledConnectionFactory(), jmsTemplateSettings,
                this.domainInfoRepository.findAll());
    }

    // === INCOMING DOMAIN RESPONSES ===
    // beans used for receiving incoming domain response messages

    @Bean
    public DomainResponseMessageListenerContainerFactory domainResponseMessageListenerContainer() throws SSLException {
        final DomainResponseMessageListenerContainerFactory messageListenerContainer = new DomainResponseMessageListenerContainerFactory(
                this.domainInfoRepository.findAll(), this.protocolInfoRepository.findAll());
        messageListenerContainer.setConnectionFactory(this.domainPooledConnectionFactory());
        messageListenerContainer.setConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_DOMAIN_RESPONSES_CONCURRENT_CONSUMERS)));
        messageListenerContainer.setMaxConcurrentConsumers(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_INCOMING_DOMAIN_RESPONSES_MAX_CONCURRENT_CONSUMERS)));

        return messageListenerContainer;
    }

    // Custom time to live for get power usage history requests.

    @Bean
    public Long getPowerUsageHistoryRequestTimeToLive() {
        return Long.parseLong(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_GET_POWER_USAGE_HISTORY_REQUEST_TIME_TO_LIVE));
    }

    @Bean
    public String netmanagementOrganisation() {
        return this.environment.getRequiredProperty(PROPERTY_NAME_NETMANAGEMENT_ORGANISATION);
    }
}
