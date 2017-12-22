/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.microgrids.application.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.alliander.osgp.adapter.ws.infra.jms.LoggingMessageSender;
import com.alliander.osgp.adapter.ws.microgrids.infra.jms.MicrogridsRequestMessageSender;
import com.alliander.osgp.adapter.ws.microgrids.infra.jms.MicrogridsResponseMessageListener;
import com.alliander.osgp.shared.application.config.AbstractMessagingConfig;
import com.alliander.osgp.shared.application.config.jms.JmsConfiguration;
import com.alliander.osgp.shared.application.config.jms.JmsConfigurationFactory;
import com.alliander.osgp.shared.application.config.jms.JmsConfigurationNames;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-microgrids.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsMicrogrids/config}", ignoreResourceNotFound = true)
public class MessagingConfig extends AbstractMessagingConfig {

    @Autowired
    private MicrogridsResponseMessageListener microgridsResponseMessageListener;

    // === JMS SETTINGS: Microgrids REQUESTS ===

    @Bean
    public JmsConfiguration requestJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration(JmsConfigurationNames.JMS_MICROGRIDS_REQUESTS);
    }

    @Bean(name = "wsMicrogridsOutgoingRequestsJmsTemplate")
    public JmsTemplate microgridsRequestsJmsTemplate(final JmsConfiguration requestJmsConfiguration) {
        return requestJmsConfiguration.getJmsTemplate();
    }

    @Bean(name = "wsMicrogridsOutgoingRequestsMessageSender")
    public MicrogridsRequestMessageSender microgridsRequestMessageSender() {
        return new MicrogridsRequestMessageSender();
    }

    // === JMS SETTINGS: Microgrids RESPONSES ===

    @Bean
    public JmsConfiguration responseJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeReceiveConfiguration(JmsConfigurationNames.JMS_MICROGRIDS_RESPONSES,
                this.microgridsResponseMessageListener);
    }

    @Bean(name = "wsMicrogridsResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer microgridsResponseMessageListenerContainer(
            final JmsConfiguration responseJmsConfiguration) {
        return responseJmsConfiguration.getMessageListenerContainer();

    }

    @Bean
    public MicrogridsResponseMessageListener microgridsResponseMessageListener() {
        return new MicrogridsResponseMessageListener();
    }

    // === JMS SETTINGS: MICROGRIDS LOGGING ===

    @Bean
    public JmsConfiguration loggingJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration(JmsConfigurationNames.JMS_MICROGRIDS_LOGGING);
    }

    @Bean
    public JmsTemplate loggingJmsTemplate(final JmsConfiguration loggingJmsConfiguration) {
        return loggingJmsConfiguration.getJmsTemplate();
    }

    @Bean
    public LoggingMessageSender loggingMessageSender() {
        return new LoggingMessageSender();
    }

}
