/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.domain.in;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import org.opensmartgridplatform.core.domain.model.protocol.ProtocolResponseService;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;

public class DomainResponseMessageListenerContainerFactory extends DefaultMessageListenerContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainResponseMessageListenerContainerFactory.class);

    @Autowired
    private ProtocolResponseService protocolResponseService;

    private List<DomainInfo> domainInfos;

    private List<ProtocolInfo> protocolInfos;

    private Map<String, DefaultMessageListenerContainer> containers = new HashMap<>();

    public DomainResponseMessageListenerContainerFactory(final List<DomainInfo> domainInfos,
            final List<ProtocolInfo> protocolInfos) {
        this.domainInfos = domainInfos;
        this.protocolInfos = protocolInfos;
    }

    public DefaultMessageListenerContainer getMessageListenerContainer(final String key) {
        return this.containers.get(key);
    }

    @Override
    public void afterPropertiesSet() {
        for (final DomainInfo domainInfo : this.domainInfos) {
            // Check if the queue name is present.
            if (domainInfo.getIncomingDomainResponsesQueue() != null) {
                LOGGER.info("Creating DomainResponseMessageListenerContainer {}", domainInfo.getKey());
                this.containers.put(domainInfo.getKey(), this.createDomainResponseMessageListenerContainer(domainInfo));
            }
        }

        for (final Entry<String, DefaultMessageListenerContainer> entry : this.containers.entrySet()) {
            LOGGER.debug("Method 'afterPropertiesSet' called for DomainResponseMessageListenerContainer {}",
                    entry.getKey());
            entry.getValue().afterPropertiesSet();
        }
    }

    @Override
    public void initialize() {
        for (final Entry<String, DefaultMessageListenerContainer> entry : this.containers.entrySet()) {
            LOGGER.debug("Method 'intitialize' called for DomainResponseMessageListenerContainer {}", entry.getKey());
            entry.getValue().initialize();
        }
    }

    @Override
    public void start() {
        for (final Entry<String, DefaultMessageListenerContainer> entry : this.containers.entrySet()) {
            LOGGER.debug("Method 'start' called for DomainResponseMessageListenerContainer {}", entry.getKey());
            entry.getValue().start();
        }
    }

    @Override
    public void destroy() {
        for (final Entry<String, DefaultMessageListenerContainer> entry : this.containers.entrySet()) {
            LOGGER.debug("Method 'destroy' called for DomainResponseMessageListenerContainer {}", entry.getKey());
            entry.getValue().destroy();
        }
    }

    private DefaultMessageListenerContainer createDomainResponseMessageListenerContainer(final DomainInfo domainInfo) {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(this.getConnectionFactory());
        messageListenerContainer.setDestination(new ActiveMQQueue(domainInfo.getIncomingDomainResponsesQueue()));
        messageListenerContainer.setConcurrentConsumers(this.getConcurrentConsumers());
        messageListenerContainer.setMaxConcurrentConsumers(this.getMaxConcurrentConsumers());

        messageListenerContainer.setMessageListener(new DomainResponseMessageListener(this.protocolResponseService,
                this.protocolInfos));
        messageListenerContainer.setSessionTransacted(true);

        return messageListenerContainer;
    }
}
