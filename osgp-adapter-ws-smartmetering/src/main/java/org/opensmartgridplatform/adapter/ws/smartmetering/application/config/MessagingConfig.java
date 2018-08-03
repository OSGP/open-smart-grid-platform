/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import org.opensmartgridplatform.adapter.ws.infra.jms.LoggingMessageSender;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import org.opensmartgridplatform.adapter.ws.smartmetering.infra.jms.SmartMeteringResponseMessageListener;
import org.opensmartgridplatform.shared.application.config.AbstractMessagingConfig;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationFactory;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationNames;

@Configuration
@PropertySource(value = "classpath:osgp-adapter-ws-smartmetering.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsSmartMetering/config}", ignoreResourceNotFound = true)
public class MessagingConfig extends AbstractMessagingConfig {

    @Autowired
    public SmartMeteringResponseMessageListener smartMeteringResponseMessageListener;

    @Bean
    public JmsConfiguration requestJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration(JmsConfigurationNames.JMS_SMARTMETERING_REQUESTS);
    }

    @Bean
    public JmsConfiguration responseJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory,
            final SmartMeteringResponseMessageListener smartMeteringResponseMessageListener) {
        return jmsConfigurationFactory.initializeConfiguration(JmsConfigurationNames.JMS_SMARTMETERING_RESPONSES,
                smartMeteringResponseMessageListener);
    }

    @Bean
    public JmsConfiguration loggingJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration(JmsConfigurationNames.JMS_SMARTMETERING_LOGGING);
    }

    @Bean
    public JmsTemplate loggingJmsTemplate(final JmsConfiguration loggingJmsConfiguration) {
        return loggingJmsConfiguration.getJmsTemplate();
    }

    @Bean(name = "wsSmartMeteringOutgoingRequestsJmsTemplate")
    public JmsTemplate smartMeteringRequestsJmsTemplate(final JmsConfiguration requestJmsConfiguration) {
        return requestJmsConfiguration.getJmsTemplate();
    }

    @Bean(name = "wsSmartMeteringResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer smartMeteringResponseMessageListenerContainer(
            final JmsConfiguration responseJmsConfiguration) {
        return responseJmsConfiguration.getMessageListenerContainer();
    }

    @Bean
    public SmartMeteringRequestMessageSender smartMeteringRequestMessageSender() {
        return new SmartMeteringRequestMessageSender();
    }

    @Bean
    public SmartMeteringResponseMessageListener smartMeteringResponseMessageListener() {
        return new SmartMeteringResponseMessageListener();
    }

    @Bean
    public LoggingMessageSender loggingMessageSender() {
        return new LoggingMessageSender();
    }

}
