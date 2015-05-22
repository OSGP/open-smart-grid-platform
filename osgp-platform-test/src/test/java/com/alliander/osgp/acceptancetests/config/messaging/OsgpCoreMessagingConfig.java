/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.acceptancetests.config.messaging;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.alliander.osgp.acceptancetests.DomainInfoTestUtils;
import com.alliander.osgp.acceptancetests.ProtocolInfoTestUtils;
import com.alliander.osgp.core.domain.model.domain.DomainRequestService;
import com.alliander.osgp.core.domain.model.domain.DomainResponseService;
import com.alliander.osgp.core.domain.model.protocol.ProtocolRequestService;
import com.alliander.osgp.core.domain.model.protocol.ProtocolResponseService;
import com.alliander.osgp.core.infra.jms.JmsTemplateSettings;
import com.alliander.osgp.core.infra.jms.domain.DomainRequestMessageListenerContainerFactory;
import com.alliander.osgp.core.infra.jms.domain.DomainResponseMessageJmsTemplateFactory;
import com.alliander.osgp.core.infra.jms.domain.DomainResponseMessageSender;
import com.alliander.osgp.core.infra.jms.domain.in.DomainRequestMessageJmsTemplateFactory;
import com.alliander.osgp.core.infra.jms.domain.in.DomainRequestMessageSender;
import com.alliander.osgp.core.infra.jms.domain.in.DomainResponseMessageListenerContainerFactory;
import com.alliander.osgp.core.infra.jms.protocol.ProtocolRequestMessageJmsTemplateFactory;
import com.alliander.osgp.core.infra.jms.protocol.ProtocolRequestMessageSender;
import com.alliander.osgp.core.infra.jms.protocol.ProtocolResponseMessageListenerContainerFactory;
import com.alliander.osgp.core.infra.jms.protocol.in.ProtocolRequestMessageListenerContainerFactory;
import com.alliander.osgp.core.infra.jms.protocol.in.ProtocolRequestMessageProcessorMap;
import com.alliander.osgp.core.infra.jms.protocol.in.ProtocolResponseMessageJmsTemplateFactory;
import com.alliander.osgp.core.infra.jms.protocol.in.ProtocolResponseMessageSender;
import com.alliander.osgp.core.infra.jms.protocol.logging.ProtocolLogItemRequestMessageListener;

public class OsgpCoreMessagingConfig {

    @Bean
    ProtocolRequestService protocolRequestMessageSender() {
        return new ProtocolRequestMessageSender();
    }

    @Bean
    DomainResponseService domainResponseMessageSender() {
        return new DomainResponseMessageSender();
    }

    @Bean
    ProtocolResponseService protocolResponseMessageSender() {
        return new ProtocolResponseMessageSender();
    }

    @Bean
    DomainRequestService domainRequestMessageSender() {
        return new DomainRequestMessageSender();
    }

    // === DOMAIN RESPONSES ===
    // beans used for sending domain response messages

    @Bean
    public DomainResponseMessageJmsTemplateFactory domainResponseJmsTemplateFactory() {
        final JmsTemplateSettings jmsTemplateSettings = new JmsTemplateSettings(MessagingConfig.EXPLICIT_QOS_ENABLED,
                MessagingConfig.TIME_TO_LIVE, MessagingConfig.DELIVERY_PERSISTENT);
        return new DomainResponseMessageJmsTemplateFactory(MessagingConfig.pooledConnectionFactory(),
                jmsTemplateSettings, DomainInfoTestUtils.getDomainInfos());
    }

    // === DOMAIN REQUESTS ===
    // beans used for receiving domain request messages

    @Bean
    public DomainRequestMessageListenerContainerFactory domainRequestMessageListenerContainerFactory() {
        final DomainRequestMessageListenerContainerFactory messageListenerContainer = new DomainRequestMessageListenerContainerFactory(
                DomainInfoTestUtils.getDomainInfos());
        messageListenerContainer.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        messageListenerContainer.setConcurrentConsumers(MessagingConfig.CONCURRENT_CONSUMERS);
        messageListenerContainer.setMaxConcurrentConsumers(MessagingConfig.MAX_CONCURRENT_CONSUMERS);

        return messageListenerContainer;
    }

    // === PROTOCOL OUTGOING REQUESTS ===
    // beans used for sending protocol request messages

    @Bean
    public ProtocolRequestMessageJmsTemplateFactory protocolRequestsJmsTemplate() {
        final JmsTemplateSettings jmsTemplateSettings = new JmsTemplateSettings(MessagingConfig.EXPLICIT_QOS_ENABLED,
                MessagingConfig.TIME_TO_LIVE, MessagingConfig.DELIVERY_PERSISTENT);

        return new ProtocolRequestMessageJmsTemplateFactory(MessagingConfig.pooledConnectionFactory(),
                jmsTemplateSettings, ProtocolInfoTestUtils.getProtocolInfos());
    }

    // === PROTOCOL OUTGOING RESPONSES ===
    // beans used for receiving protocol response messages

    @Bean
    public ProtocolResponseMessageListenerContainerFactory protocolResponseMessageListenerContainer() {
        final ProtocolResponseMessageListenerContainerFactory messageListenerContainer = new ProtocolResponseMessageListenerContainerFactory(
                ProtocolInfoTestUtils.getProtocolInfos());
        messageListenerContainer.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        messageListenerContainer.setConcurrentConsumers(MessagingConfig.CONCURRENT_CONSUMERS);
        messageListenerContainer.setMaxConcurrentConsumers(MessagingConfig.MAX_CONCURRENT_CONSUMERS);

        return messageListenerContainer;
    }

    // === PROTOCOL LOG ITEM REQUESTS ===
    // beans used for receiving protocol log item request messages

    @Bean
    public ActiveMQDestination protocolLogItemRequestsQueue() {
        return new ActiveMQQueue(MessagingConfig.OSLP_LOG_ITEM_REQUESTS_QUEUE);
    }

    @Bean
    public DefaultMessageListenerContainer protocolLogItemRequestsMessageListenerContainer() {
        final DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        messageListenerContainer.setDestination(this.protocolLogItemRequestsQueue());
        messageListenerContainer.setConcurrentConsumers(MessagingConfig.CONCURRENT_CONSUMERS);
        messageListenerContainer.setMaxConcurrentConsumers(MessagingConfig.MAX_CONCURRENT_CONSUMERS);
        messageListenerContainer.setMessageListener(this.protocolLogItemRequestMessageListener());
        messageListenerContainer.setSessionTransacted(true);
        return messageListenerContainer;
    }

    @Bean
    public ProtocolLogItemRequestMessageListener protocolLogItemRequestMessageListener() {
        return new ProtocolLogItemRequestMessageListener();
    }

    // === PROTOCOL INCOMING REQUESTS ===
    // beans used for receiving incoming protocol request messages

    @Bean
    public ProtocolRequestMessageListenerContainerFactory protocolRequestMessageListenerContainer() {
        final ProtocolRequestMessageListenerContainerFactory messageListenerContainer = new ProtocolRequestMessageListenerContainerFactory(
                ProtocolInfoTestUtils.getProtocolInfos(), DomainInfoTestUtils.getDomainInfos(),
                this.protocolRequestMessageProcessorMap);
        messageListenerContainer.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        messageListenerContainer.setConcurrentConsumers(MessagingConfig.CONCURRENT_CONSUMERS);
        messageListenerContainer.setMaxConcurrentConsumers(MessagingConfig.MAX_CONCURRENT_CONSUMERS);

        return messageListenerContainer;
    }

    @Autowired
    @Qualifier("osgpCoreIncomingProtocolRequestMessageProcessorMap")
    private ProtocolRequestMessageProcessorMap protocolRequestMessageProcessorMap;

    // === PROTOCOL INCOMING RESPONSES ===
    // beans used for sending incoming protocol response messages

    @Bean
    public ProtocolResponseMessageJmsTemplateFactory protocolResponseJmsTemplateFactory() {
        final JmsTemplateSettings jmsTemplateSettings = new JmsTemplateSettings(MessagingConfig.EXPLICIT_QOS_ENABLED,
                MessagingConfig.TIME_TO_LIVE, MessagingConfig.DELIVERY_PERSISTENT);

        return new ProtocolResponseMessageJmsTemplateFactory(MessagingConfig.pooledConnectionFactory(),
                jmsTemplateSettings, ProtocolInfoTestUtils.getProtocolInfos());
    }

    // === DOMAIN INCOMING REQUESTS ==
    // beans used for sending incoming domain request messages

    @Bean
    public DomainRequestMessageJmsTemplateFactory domainRequestMessageJmsTemplateFactory() {
        final JmsTemplateSettings jmsTemplateSettings = new JmsTemplateSettings(MessagingConfig.EXPLICIT_QOS_ENABLED,
                MessagingConfig.TIME_TO_LIVE, MessagingConfig.DELIVERY_PERSISTENT);

        return new DomainRequestMessageJmsTemplateFactory(MessagingConfig.pooledConnectionFactory(),
                jmsTemplateSettings, DomainInfoTestUtils.getDomainInfos());
    }

    // === DOMAIN INCOMING RESPONSES ===
    // beans used for receiving incoming domain response messages

    @Bean
    public DomainResponseMessageListenerContainerFactory domainResponseMessageListenerContainer() {
        final DomainResponseMessageListenerContainerFactory messageListenerContainer = new DomainResponseMessageListenerContainerFactory(
                DomainInfoTestUtils.getDomainInfos(), ProtocolInfoTestUtils.getProtocolInfos());
        messageListenerContainer.setConnectionFactory(MessagingConfig.pooledConnectionFactory());
        messageListenerContainer.setConcurrentConsumers(MessagingConfig.CONCURRENT_CONSUMERS);
        messageListenerContainer.setMaxConcurrentConsumers(MessagingConfig.MAX_CONCURRENT_CONSUMERS);

        return messageListenerContainer;
    }
}
