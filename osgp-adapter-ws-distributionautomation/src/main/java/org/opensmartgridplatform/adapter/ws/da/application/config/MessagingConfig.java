/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.da.application.config;

import org.opensmartgridplatform.adapter.ws.da.infra.jms.DistributionAutomationRequestMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import org.opensmartgridplatform.adapter.ws.infra.jms.LoggingMessageSender;
import org.opensmartgridplatform.adapter.ws.da.infra.jms.DistributionAutomationResponseMessageListener;
import org.opensmartgridplatform.shared.application.config.AbstractMessagingConfig;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationFactory;

@Configuration
@PropertySources({ @PropertySource("classpath:osgp-adapter-ws-distributionautomation.properties"),
        @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
        @PropertySource(value = "file:${osgp/AdapterWsDistributionAutomation/config}", ignoreResourceNotFound = true), })
public class MessagingConfig extends AbstractMessagingConfig {

    @Autowired
    public DistributionAutomationResponseMessageListener distributionautomationResponseMessageListener;

    // === JMS SETTINGS: DistributionAutomation REQUESTS ===

    @Bean
    public JmsConfiguration requestJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.distributionautomation.requests");
    }

    @Bean(name = "wsDistributionAutomationOutgoingRequestsJmsTemplate")
    public JmsTemplate distributionautomationRequestsJmsTemplate(final JmsConfiguration requestJmsConfiguration) {
        return requestJmsConfiguration.getJmsTemplate();
    }

    @Bean(name = "wsDistributionAutomationOutgoingRequestsMessageSender")
    public DistributionAutomationRequestMessageSender distributionautomationRequestMessageSender() {
        return new DistributionAutomationRequestMessageSender();
    }

    // === JMS SETTINGS: DistributionAutomation RESPONSES ===

    @Bean
    public JmsConfiguration responseJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.distributionautomation.responses",
                this.distributionautomationResponseMessageListener);
    }

    @Bean(name = "wsDistributionAutomationIncomingResponsesJmsTemplate")
    public JmsTemplate distributionautomationResponsesJmsTemplate(final JmsConfiguration responseJmsConfiguration) {
        return responseJmsConfiguration.getJmsTemplate();
    }

    @Bean(name = "wsDistributionAutomationResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer distributionautomationResponseMessageListenerContainer(
            final JmsConfiguration responseJmsConfiguration) {
        return responseJmsConfiguration.getMessageListenerContainer();

    }

    @Bean
    public DistributionAutomationResponseMessageListener distributionautomationResponseMessageListener() {
        return new DistributionAutomationResponseMessageListener();
    }

    // === JMS SETTINGS: DISTRIBUTION AUTOMATION LOGGING ===

    @Bean
    public JmsConfiguration loggingJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.distributionautomation.logging");
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
