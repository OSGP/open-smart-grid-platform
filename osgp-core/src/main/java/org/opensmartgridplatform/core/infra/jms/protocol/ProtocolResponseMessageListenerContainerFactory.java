/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.protocol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import org.opensmartgridplatform.core.application.services.DeviceResponseMessageService;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;

public class ProtocolResponseMessageListenerContainerFactory extends DefaultMessageListenerContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolResponseMessageListenerContainerFactory.class);

    @Autowired
    private DeviceResponseMessageService deviceResponseMessageService;

    private List<ProtocolInfo> protocolInfos;

    private Map<String, DefaultMessageListenerContainer> containers = new HashMap<>();

    public ProtocolResponseMessageListenerContainerFactory(final List<ProtocolInfo> protocolInfos) {
        this.protocolInfos = protocolInfos;
    }

    public DefaultMessageListenerContainer getMessageListenerContainer(final String key) {
        return this.containers.get(key);
    }

    @Override
    public void afterPropertiesSet() {
        for (final ProtocolInfo protocolInfo : this.protocolInfos) {
            LOGGER.info("Creating ProtocolResponseMessageListenerContainer {}", protocolInfo.getKey());
            this.containers.put(protocolInfo.getKey(),
                    this.createProtocolResponseMessageListenerContainer(protocolInfo));
        }

        for (final Entry<String, DefaultMessageListenerContainer> entry : this.containers.entrySet()) {
            LOGGER.debug("Method 'afterPropertiesSet' called for ProtocolResponseMessageListenerContainer {}",
                    entry.getKey());
            entry.getValue().afterPropertiesSet();
        }
    }

    @Override
    public void initialize() {
        for (final Entry<String, DefaultMessageListenerContainer> entry : this.containers.entrySet()) {
            LOGGER.debug("Method 'intitialize' called for ProtocolResponseMessageListenerContainer {}", entry.getKey());
            entry.getValue().initialize();
        }
    }

    @Override
    public void start() {
        for (final Entry<String, DefaultMessageListenerContainer> entry : this.containers.entrySet()) {
            LOGGER.debug("Method 'start' called for ProtocolResponseMessageListenerContainer {}", entry.getKey());
            entry.getValue().start();
        }
    }

    @Override
    public void destroy() {
        for (final Entry<String, DefaultMessageListenerContainer> entry : this.containers.entrySet()) {
            LOGGER.debug("Method 'destroy' called for ProtocolResponseMessageListenerContainer {}", entry.getKey());
            entry.getValue().destroy();
        }
    }

    private DefaultMessageListenerContainer createProtocolResponseMessageListenerContainer(
            final ProtocolInfo protocolInfo) {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(this.getConnectionFactory());
        messageListenerContainer.setDestination(new ActiveMQQueue(protocolInfo.getIncomingProtocolResponsesQueue()));
        messageListenerContainer.setConcurrentConsumers(this.getConcurrentConsumers());
        messageListenerContainer.setMaxConcurrentConsumers(this.getMaxConcurrentConsumers());

        messageListenerContainer.setMessageListener(new ProtocolResponseMessageListener(
                this.deviceResponseMessageService));
        messageListenerContainer.setSessionTransacted(true);

        return messageListenerContainer;
    }
}
