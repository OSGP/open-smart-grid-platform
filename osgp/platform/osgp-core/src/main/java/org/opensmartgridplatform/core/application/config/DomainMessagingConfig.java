/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.application.config;

import java.util.List;

import org.opensmartgridplatform.core.infra.jms.domain.DefaultDomainJmsConfiguration;
import org.opensmartgridplatform.core.infra.jms.domain.incoming.DomainRequestMessageListenerContainerFactory;
import org.opensmartgridplatform.core.infra.jms.domain.incoming.DomainResponseMessageListenerContainerFactory;
import org.opensmartgridplatform.core.infra.jms.domain.outgoing.DomainRequestMessageJmsTemplateFactory;
import org.opensmartgridplatform.core.infra.jms.domain.outgoing.DomainResponseMessageJmsTemplateFactory;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.ProtocolInfoRepository;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-core.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/Core/config}", ignoreResourceNotFound = true)
public class DomainMessagingConfig extends AbstractConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainMessagingConfig.class);

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

    private List<DomainInfo> domainInfos;
    private List<ProtocolInfo> protocolInfos;

    public DomainMessagingConfig(final DomainInfoRepository domainInfoRepository,
            final ProtocolInfoRepository protocolInfoRepository) {

        this.domainInfos = domainInfoRepository.findAll();
        this.protocolInfos = protocolInfoRepository.findAll();
    }

    @Bean
    public JmsConfiguration defaultDomainJmsConfiguration() {
        return new DefaultDomainJmsConfiguration() {
        };
    }

    // === OUTGOING DOMAIN RESPONSES ===
    // beans used for sending domain response messages

    @Bean
    public DomainResponseMessageJmsTemplateFactory domainResponseJmsTemplateFactory() {
        LOGGER.debug("Creating bean: domainResponseJmsTemplateFactory");

        return new DomainResponseMessageJmsTemplateFactory(this.environment, this.domainInfos);
    }

    // === INCOMING DOMAIN REQUESTS ===
    // beans used for receiving domain request messages

    @Bean
    public DomainRequestMessageListenerContainerFactory domainRequestMessageListenerContainerFactory() {
        LOGGER.debug("Creating bean: domainResponseMessageListenerContainerFactory");

        return new DomainRequestMessageListenerContainerFactory(this.environment, this.domainInfos);
    }

    // === OUTGOING DOMAIN REQUESTS ==
    // beans used for sending domain request messages

    @Bean
    public DomainRequestMessageJmsTemplateFactory domainRequestMessageJmsTemplateFactory() {

        return new DomainRequestMessageJmsTemplateFactory(this.environment, this.domainInfos);
    }

    // === INCOMING DOMAIN RESPONSES ===
    // beans used for receiving domain response messages

    @Bean
    public DomainResponseMessageListenerContainerFactory domainResponseMessageListenerContainer() {
        return new DomainResponseMessageListenerContainerFactory(this.environment, this.domainInfos,
                this.protocolInfos);
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
