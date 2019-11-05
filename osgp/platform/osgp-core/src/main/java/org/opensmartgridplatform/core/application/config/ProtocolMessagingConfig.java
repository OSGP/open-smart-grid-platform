/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.application.config;

import java.util.ArrayList;
import java.util.List;

import org.opensmartgridplatform.core.infra.jms.protocol.incoming.ProtocolRequestMessageListenerContainerFactory;
import org.opensmartgridplatform.core.infra.jms.protocol.incoming.ProtocolResponseMessageListenerContainerFactory;
import org.opensmartgridplatform.core.infra.jms.protocol.outgoing.ProtocolRequestMessageJmsTemplateFactory;
import org.opensmartgridplatform.core.infra.jms.protocol.outgoing.ProtocolResponseMessageJmsTemplateFactory;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.ProtocolInfoRepository;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-core.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/Core/config}", ignoreResourceNotFound = true)
public class ProtocolMessagingConfig extends AbstractConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolMessagingConfig.class);

    // JMS Settings
    private static final String PROPERTY_NAME_JMS_ACTIVEMQ_MESSAGEGROUP_CACHESIZE = "jms.protocol.activemq.messagegroup.cachesize";
    private static final String PROPERTY_NAME_MAX_RETRY_COUNT = "max.retry.count";

    // JMS Settings: SSL settings for the protocol requests and responses
    @Value("${jms.protocol.activemq.broker.client.key.store:/etc/osp/activemq/client.ks}")
    private String clientKeyStore;

    @Value("${jms.protocol.activemq.broker.client.key.store.pwd:password}")
    private String clientKeyStorePwd;

    @Value("${jms.protocol.activemq.broker.client.trust.store:/etc/osp/activemq/client.ts}")
    private String trustKeyStore;

    @Value("${jms.protocol.activemq.broker.client.trust.store.pwd:password}")
    private String trustKeyStorePwd;

    private static final int DEFAULT_MESSAGE_GROUP_CACHE_SIZE = 1024;

    private List<DomainInfo> domainInfos;
    private List<ProtocolInfo> protocolInfos;

    public ProtocolMessagingConfig(final DomainInfoRepository domainInfoRepository,
            final ProtocolInfoRepository protocolInfoRepository) {
        this.domainInfos = new ArrayList<>(domainInfoRepository.findAll());
        this.protocolInfos = new ArrayList<>(protocolInfoRepository.findAll());
    }

    @Bean
    @Qualifier("osgpCoreIncomingProtocolRequestMessageProcessorMap")
    public MessageProcessorMap protocolRequestMessageProcessorMap() {
        return new BaseMessageProcessorMap("ProtocolRequestMessageProcessorMap");
    }

    @Bean
    public Integer messageGroupCacheSize() {
        LOGGER.debug("Creating bean: messageGroupCacheSize");
        try {
            final int cacheSize = Integer
                    .parseInt(this.environment.getProperty(PROPERTY_NAME_JMS_ACTIVEMQ_MESSAGEGROUP_CACHESIZE));
            if (cacheSize <= 0) {
                throw new NumberFormatException(String.valueOf(cacheSize));
            }
            return cacheSize;
        } catch (final NumberFormatException e) {
            LOGGER.warn("Invalid message group cache size, using default value {}", DEFAULT_MESSAGE_GROUP_CACHE_SIZE,
                    e);
            return DEFAULT_MESSAGE_GROUP_CACHE_SIZE;
        }
    }

    // === OUTGOING PROTOCOL REQUESTS ===
    // beans used for sending protocol request messages

    @Bean
    public ProtocolRequestMessageJmsTemplateFactory protocolRequestsJmsTemplate() {
        return new ProtocolRequestMessageJmsTemplateFactory(this.environment, this.protocolInfos);
    }

    // === INCOMING PROTOCOL RESPONSES ===
    // beans used for receiving protocol response messages

    @Bean
    public ProtocolResponseMessageListenerContainerFactory protocolResponseMessageListenerContainer() {
        return new ProtocolResponseMessageListenerContainerFactory(this.environment, this.protocolInfos);
    }

    // === INCOMING PROTOCOL REQUESTS ===
    // beans used for receiving incoming protocol request messages

    @Bean
    public ProtocolRequestMessageListenerContainerFactory protocolRequestMessageListenerContainer(
            @Qualifier("osgpCoreIncomingProtocolRequestMessageProcessorMap") final MessageProcessorMap messageProcessorMap) {
        return new ProtocolRequestMessageListenerContainerFactory(this.environment, this.protocolInfos,
                this.domainInfos, messageProcessorMap);
    }

    // === OUTGOING PROTOCOL RESPONSES ===
    // beans used for sending protocol response messages

    @Bean
    public ProtocolResponseMessageJmsTemplateFactory protocolResponseJmsTemplateFactory() {
        return new ProtocolResponseMessageJmsTemplateFactory(this.environment, this.protocolInfos);
    }

    // The Max count to retry a failed response

    @Bean
    public int getMaxRetryCount() {
        return Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_MAX_RETRY_COUNT));
    }
}
