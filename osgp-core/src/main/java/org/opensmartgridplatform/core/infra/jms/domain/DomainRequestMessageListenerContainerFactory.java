/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.activemq.command.ActiveMQQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import org.opensmartgridplatform.core.application.services.DeviceRequestMessageService;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.repositories.ScheduledTaskRepository;

public class DomainRequestMessageListenerContainerFactory extends DefaultMessageListenerContainer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainRequestMessageListenerContainerFactory.class);

    @Autowired
    private DeviceRequestMessageService deviceRequestMessageService;

    @Autowired
    private ScheduledTaskRepository scheduledTaskRepository;

    private final List<DomainInfo> domainInfos;

    private final Map<String, DefaultMessageListenerContainer> containers = new HashMap<>();

    public DomainRequestMessageListenerContainerFactory(final List<DomainInfo> domainInfos) {
        this.domainInfos = domainInfos;
    }

    public DefaultMessageListenerContainer getMessageListenerContainer(final String key) {
        return this.containers.get(key);
    }

    @Override
    public void afterPropertiesSet() {
        for (final DomainInfo domainInfo : this.domainInfos) {
            final String key = domainInfo.getKey();
            LOGGER.info("Creating DomainRequestMessageListenerContainer {}", key);
            this.containers.put(key, this.createDomainRequestMessageListenerContainer(domainInfo));
        }

        for (final Entry<String, DefaultMessageListenerContainer> entry : this.containers.entrySet()) {
            LOGGER.info("Method 'afterPropertiesSet' called for DomainRequestMessageListenerContainer {}",
                    entry.getKey());
            entry.getValue().afterPropertiesSet();
        }
    }

    @Override
    public void initialize() {
        for (final Entry<String, DefaultMessageListenerContainer> entry : this.containers.entrySet()) {
            LOGGER.info("Method 'intitialize' called for DomainRequestMessageListenerContainer{}", entry.getKey());
            entry.getValue().initialize();
        }
    }

    @Override
    public void start() {
        for (final Entry<String, DefaultMessageListenerContainer> entry : this.containers.entrySet()) {
            LOGGER.info("Method 'start' called for DomainRequestMessageListenerContainer {}, {}", entry.getKey(), entry
                    .getValue().getDestination().toString());
            entry.getValue().start();
        }
    }

    @Override
    public void destroy() {
        for (final Entry<String, DefaultMessageListenerContainer> entry : this.containers.entrySet()) {
            LOGGER.info("Method 'destroy' called for DomainRequestMessageListenerContainer {}", entry.getKey());
            entry.getValue().destroy();
        }
    }

    private DefaultMessageListenerContainer createDomainRequestMessageListenerContainer(final DomainInfo domainInfo) {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(this.getConnectionFactory());
        messageListenerContainer.setDestination(new ActiveMQQueue(domainInfo.getIncomingDomainRequestsQueue()));
        messageListenerContainer.setConcurrentConsumers(this.getConcurrentConsumers());
        messageListenerContainer.setMaxConcurrentConsumers(this.getMaxConcurrentConsumers());
        messageListenerContainer.setMessageListener(new DomainRequestMessageListener(domainInfo,
                this.deviceRequestMessageService, this.scheduledTaskRepository));
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }
}
