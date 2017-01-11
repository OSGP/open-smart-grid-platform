/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.alliander.osgp.adapter.ws.infra.jms.LoggingMessageSender;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringResponseMessageListener;
import com.alliander.osgp.shared.application.config.AbstractMessagingConfig;
import com.alliander.osgp.shared.application.config.JmsConfigurationFactory;

@Configuration
@PropertySources({ @PropertySource(value = "classpath:osgp-adapter-ws-smartmetering.properties"),
        @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
        @PropertySource(value = "file:${osgp/AdapterWsSmartMetering/config}", ignoreResourceNotFound = true), })
public class MessagingConfig extends AbstractMessagingConfig {

    @Autowired
    public SmartMeteringResponseMessageListener smartMeteringResponseMessageListener;

    @Bean
    protected JmsConfigurationFactory.JmsRequestConfiguration requestJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.getInstance("jms.smartmetering.requests");
    }

    @Bean
    protected JmsConfigurationFactory.JmsResponseConfiguration responseJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory,
            final SmartMeteringResponseMessageListener smartMeteringResponseMessageListener) {
        return jmsConfigurationFactory.getInstance("jms.smartmetering.responses", smartMeteringResponseMessageListener);
    }

    @Bean
    protected JmsConfigurationFactory.JmsRequestConfiguration loggingJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.getInstance("jms.smartmetering.logging");
    }

    @Bean
    public JmsTemplate loggingJmsTemplate(final JmsConfigurationFactory.JmsRequestConfiguration loggingJmsConfiguration) {
        return loggingJmsConfiguration.getJmsTemplate();
    }

    @Bean(name = "wsSmartMeteringOutgoingRequestsJmsTemplate")
    public JmsTemplate smartMeteringRequestsJmsTemplate(
            final JmsConfigurationFactory.JmsRequestConfiguration requestJmsConfiguration) {
        return requestJmsConfiguration.getJmsTemplate();
    }

    @Bean(name = "wsSmartMeteringResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer smartMeteringResponseMessageListenerContainer(
            final JmsConfigurationFactory.JmsResponseConfiguration responseJmsConfiguration) {
        return responseJmsConfiguration.getMessageListenerContainer();
    }

    /**
     * @return
     */
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
