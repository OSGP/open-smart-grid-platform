/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.config;

import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceMessageLoggingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceRequestMessageListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.LogItemRequestMessageSender;
import org.opensmartgridplatform.shared.application.config.AbstractMessagingConfig;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationFactory;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@PropertySource("classpath:osgp-adapter-protocol-iec60870.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolIec60870/config}", ignoreResourceNotFound = true)
public class Iec60870MessagingConfig extends AbstractMessagingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870MessagingConfig.class);

    @Bean
    public MessageProcessorMap iec60870RequestMessageProcessorMap() {
        return new BaseMessageProcessorMap("iec60870RequestMessageProcessorMap");
    }

    // === JMS SETTINGS IEC60870 REQUESTS ===
    @Bean
    public DeviceRequestMessageListener iec60870RequestsMessageListener() {
        return new DeviceRequestMessageListener();
    }

    @Bean
    public JmsConfiguration iec60870RequestJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeReceiveConfiguration("jms.iec60870.requests",
                this.iec60870RequestsMessageListener());
    }

    @Bean
    public DefaultMessageListenerContainer iec60870RequestsMessageListenerContainer(
            final JmsConfiguration iec60870RequestJmsConfiguration) {
        final DefaultMessageListenerContainer messageListenerContainer = iec60870RequestJmsConfiguration
                .getMessageListenerContainer();

        // Setting ErrorHandler to prevent logging at WARN level
        // when JMSException is thrown: Execution of JMS message
        // listener failed, and no ErrorHandler has been set.
        messageListenerContainer.setErrorHandler(
                t -> LOGGER.debug("iec60870RequestsMessageListenerContainer.ErrorHandler.handleError()", t));
        return messageListenerContainer;
    }

    @Bean
    public int maxRedeliveriesForIec60870Requests(final JmsConfiguration iec60870RequestJmsConfiguration) {
        return iec60870RequestJmsConfiguration.getRedeliveryPolicy().getMaximumRedeliveries();
    }

    // === JMS SETTINGS: IEC60870 RESPONSES ===

    @Bean
    public JmsConfiguration iec60870ResponseJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.iec60870.responses");
    }

    @Bean
    public JmsTemplate iec60870ResponsesJmsTemplate(final JmsConfiguration iec60870ResponseJmsConfiguration) {
        return iec60870ResponseJmsConfiguration.getJmsTemplate();
    }

    @Bean
    public DeviceResponseMessageSender iec60870ResponseMessageSender() {
        return new DeviceResponseMessageSender();
    }

    // === JMS SETTINGS: IEC60870 LOG ITEM REQUESTS ===

    @Bean
    public JmsConfiguration logItemRequestJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.iec60870.log.item.requests");
    }

    @Bean
    public JmsTemplate logItemRequestsJmsTemplate(final JmsConfiguration logItemRequestJmsConfiguration) {
        return logItemRequestJmsConfiguration.getJmsTemplate();
    }

    @Bean
    public LogItemRequestMessageSender logItemRequestMessageSender() {
        return new LogItemRequestMessageSender();
    }

    // === DEVICE MESSAGE LOGGING ===

    @Bean
    public DeviceMessageLoggingService deviceMessageLoggingService() {
        return new DeviceMessageLoggingService(this.logItemRequestMessageSender());
    }
}
