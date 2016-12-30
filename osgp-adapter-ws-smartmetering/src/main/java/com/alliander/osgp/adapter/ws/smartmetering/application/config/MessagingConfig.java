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

@Configuration
@PropertySources({ @PropertySource("classpath:osgp-adapter-ws-smartmetering.properties"),
    @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
    @PropertySource(value = "file:${osgp/AdapterWsSmartMetering/config}", ignoreResourceNotFound = true), })
public class MessagingConfig extends AbstractMessagingConfig {

    private static final String PROPERTY_NAME_JMS_SMART_METERING_PREFIX = "jms.smartmetering.";

    @Autowired
    public SmartMeteringResponseMessageListener smartMeteringResponseMessageListener;

    @Override
    protected String getJmsPropertyPrefix() {
        return PROPERTY_NAME_JMS_SMART_METERING_PREFIX;
    }

    @Bean
    public JmsTemplate loggingJmsTemplate() {
        return super.jmsTemplate(PROPERTY_NAME_LOGGING, this.loggingQueue());
    }

    @Bean(name = "wsSmartMeteringOutgoingRequestsJmsTemplate")
    public JmsTemplate smartMeteringRequestsJmsTemplate() {
        return super.jmsTemplate(PROPERTY_NAME_REQUESTS, this.requestsQueue());
    }

    @Bean(name = "wsSmartMeteringResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer smartMeteringResponseMessageListenerContainer() {
        return super.messageListenerContainer(PROPERTY_NAME_RESPONSES, this.responsesQueue(),
                this.smartMeteringResponseMessageListener);
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
