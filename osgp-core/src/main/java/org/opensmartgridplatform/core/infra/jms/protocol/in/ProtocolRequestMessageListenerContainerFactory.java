/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.protocol.in;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.activemq.command.ActiveMQQueue;
import org.opensmartgridplatform.core.domain.model.domain.DomainRequestService;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

public class ProtocolRequestMessageListenerContainerFactory extends DefaultMessageListenerContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolRequestMessageListenerContainerFactory.class);

    @Autowired
    private DomainRequestService domainRequestService;

    private List<ProtocolInfo> protocolInfos;

    private List<DomainInfo> domainInfos;

    private MessageProcessorMap protocolRequestMessageProcessorMap;

    private Map<String, DefaultMessageListenerContainer> containers = new HashMap<>();

    public ProtocolRequestMessageListenerContainerFactory(final List<ProtocolInfo> protocolInfos,
            final List<DomainInfo> domainInfos, final MessageProcessorMap protocolRequestMessageProcessorMap) {
        this.protocolInfos = protocolInfos;
        this.domainInfos = domainInfos;
        this.protocolRequestMessageProcessorMap = protocolRequestMessageProcessorMap;
    }

    public DefaultMessageListenerContainer getMessageListenerContainer(final String key) {
        return this.containers.get(key);
    }

    @Override
    public void afterPropertiesSet() {
        for (final ProtocolInfo protocolInfo : this.protocolInfos) {
            LOGGER.info("Creating ProtocolRequestMessageListenerContainer {}", protocolInfo.getKey());
            this.containers
                    .put(protocolInfo.getKey(), this.createProtocolRequestMessageListenerContainer(protocolInfo));
        }

        for (final Entry<String, DefaultMessageListenerContainer> entry : this.containers.entrySet()) {
            LOGGER.debug("Method 'afterPropertiesSet' called for ProtocolRequestMessageListenerContainer {}",
                    entry.getKey());
            entry.getValue().afterPropertiesSet();
        }
    }

    @Override
    public void initialize() {
        for (final Entry<String, DefaultMessageListenerContainer> entry : this.containers.entrySet()) {
            LOGGER.debug("Method 'intitialize' called for ProtocolRequestMessageListenerContainer {}", entry.getKey());
            entry.getValue().initialize();
        }
    }

    @Override
    public void start() {
        for (final Entry<String, DefaultMessageListenerContainer> entry : this.containers.entrySet()) {
            LOGGER.debug("Method 'start' called for ProtocolRequestMessageListenerContainer {}", entry.getKey());
            entry.getValue().start();
        }
    }

    @Override
    public void destroy() {
        for (final Entry<String, DefaultMessageListenerContainer> entry : this.containers.entrySet()) {
            LOGGER.debug("Method 'destroy' called for ProtocolRequestMessageListenerContainer {}", entry.getKey());
            entry.getValue().destroy();
        }
    }

    private DefaultMessageListenerContainer createProtocolRequestMessageListenerContainer(
            final ProtocolInfo protocolInfo) {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(this.getConnectionFactory());
        messageListenerContainer.setDestination(new ActiveMQQueue(protocolInfo.getIncomingProtocolRequestsQueue()));
        messageListenerContainer.setConcurrentConsumers(this.getConcurrentConsumers());
        messageListenerContainer.setMaxConcurrentConsumers(this.getMaxConcurrentConsumers());

        messageListenerContainer.setMessageListener(new ProtocolRequestMessageListener(this.domainRequestService,
                this.domainInfos, this.protocolRequestMessageProcessorMap));
        messageListenerContainer.setSessionTransacted(true);

        return messageListenerContainer;
    }
}
