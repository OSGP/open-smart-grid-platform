/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.application.config;

import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.DeviceRequestMessageListener;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.Iec61850LogItemRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.OsgpRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.OsgpResponseMessageListener;
import org.opensmartgridplatform.adapter.protocol.iec61850.services.DeviceMessageLoggingService;
import org.opensmartgridplatform.shared.application.config.AbstractMessagingConfig;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationFactory;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * An application context Java configuration class.
 */
@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:osgp-adapter-protocol-iec61850.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolIec61850/config}", ignoreResourceNotFound = true)
public class MessagingConfig extends AbstractMessagingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingConfig.class);

    // === JMS SETTINGS IEC61850 REQUESTS ===
    @Bean
    public DeviceRequestMessageListener iec61850RequestsMessageListener() {
        return new DeviceRequestMessageListener();
    }

    @Bean
    public JmsConfiguration iec61850RequestJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeReceiveConfiguration("jms.iec61850.requests",
                this.iec61850RequestsMessageListener());
    }

    @Bean
    public DefaultMessageListenerContainer iec61850RequestsMessageListenerContainer(
            final JmsConfiguration iec61850RequestJmsConfiguration) {
        final DefaultMessageListenerContainer messageListenerContainer = iec61850RequestJmsConfiguration
                .getMessageListenerContainer();
        // Setting ErrorHandler to prevent logging at WARN level
        // when JMSException is thrown: Execution of JMS message
        // listener failed, and no ErrorHandler has been set.
        messageListenerContainer.setErrorHandler(
                t -> LOGGER.debug("iec61850RequestsMessageListenerContainer.ErrorHandler.handleError()", t));
        return messageListenerContainer;
    }

    @Bean
    public int maxRedeliveriesForIec61850Requests(final JmsConfiguration iec61850RequestJmsConfiguration) {
        return iec61850RequestJmsConfiguration.getRedeliveryPolicy().getMaximumRedeliveries();
    }

    @Bean
    @Qualifier("iec61850DeviceRequestMessageProcessorMap")
    public MessageProcessorMap microgridsResponseMessageProcessorMap() {
        return new BaseMessageProcessorMap("DeviceRequestMessageProcessorMap");
    }

    // === JMS SETTINGS: IEC61850 RESPONSES ===

    @Bean
    public JmsConfiguration iec61850ResponseJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.iec61850.responses");
    }

    @Bean
    public JmsTemplate iec61850ResponsesJmsTemplate(final JmsConfiguration iec61850ResponseJmsConfiguration) {
        return iec61850ResponseJmsConfiguration.getJmsTemplate();
    }

    @Bean
    public DeviceResponseMessageSender iec61850ResponseMessageSender() {
        return new DeviceResponseMessageSender();
    }

    // === JMS SETTINGS: IEC61850 LOG ITEM REQUESTS ===

    @Bean
    public JmsConfiguration iec61850LogItemRequestJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.iec61850.log.item.requests");
    }

    @Bean
    public JmsTemplate iec61850LogItemRequestsJmsTemplate(
            final JmsConfiguration iec61850LogItemRequestJmsConfiguration) {
        return iec61850LogItemRequestJmsConfiguration.getJmsTemplate();
    }

    @Bean
    public Iec61850LogItemRequestMessageSender iec61850LogItemRequestMessageSender() {
        return new Iec61850LogItemRequestMessageSender();
    }

    // === OSGP REQUESTS ===

    @Bean
    public JmsConfiguration osgpRequestJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.osgp.requests");
    }

    @Bean
    public JmsTemplate osgpRequestsJmsTemplate(final JmsConfiguration osgpRequestJmsConfiguration) {
        return osgpRequestJmsConfiguration.getJmsTemplate();
    }

    @Bean
    public OsgpRequestMessageSender osgpRequestMessageSender() {
        return new OsgpRequestMessageSender();
    }

    // === OSGP RESPONSES ===

    @Bean
    public OsgpResponseMessageListener osgpResponseMessageListener() {
        return new OsgpResponseMessageListener();
    }

    @Bean
    public JmsConfiguration osgpResponseJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeReceiveConfiguration("jms.osgp.responses",
                this.osgpResponseMessageListener());
    }

    @Bean
    public DefaultMessageListenerContainer osgpResponsesMessageListenerContainer(
            final JmsConfiguration osgpResponseJmsConfiguration) {
        return osgpResponseJmsConfiguration.getMessageListenerContainer();
    }

    // === DEVICE MESSAGE LOGGING ===

    @Bean
    public DeviceMessageLoggingService deviceMessageLoggingService() {
        return new DeviceMessageLoggingService(this.iec61850LogItemRequestMessageSender());
    }
}
