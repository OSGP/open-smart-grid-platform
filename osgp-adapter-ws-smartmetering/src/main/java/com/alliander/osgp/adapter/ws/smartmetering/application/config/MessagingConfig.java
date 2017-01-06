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
@PropertySources({ @PropertySource(value = "classpath:osgp-adapter-ws-smartmetering.properties"),
    @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
    @PropertySource(value = "file:${osgp/AdapterWsSmartMetering/config}", ignoreResourceNotFound = true), })
public class MessagingConfig extends AbstractMessagingConfig {

    private static final String PROPERTY_NAME_JMS_SMART_METERING_REQUEST_QUEUE = "jms.smartmetering.requests.queue";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_QUEUE = "jms.smartmetering.responses.queue";
    private static final String PROPERTY_NAME_JMS_SMART_METERING_LOGING_QUEUE = "jms.smartmetering.logging.queue";

    @Autowired
    public SmartMeteringResponseMessageListener smartMeteringResponseMessageListener;

    @Override
    protected String getRequestQueueName() {
        return this.environment.getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_REQUEST_QUEUE);
    }

    @Override
    protected String getResponsesQueueName() {
        return this.environment.getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_RESPONSES_QUEUE);
    }

    @Override
    protected String getLoggingQueueName() {
        return this.environment.getRequiredProperty(PROPERTY_NAME_JMS_SMART_METERING_LOGING_QUEUE);
    }

    @Bean
    public JmsTemplate loggingJmsTemplate() {
        return super.jmsLoggingTemplate();
    }

    @Bean(name = "wsSmartMeteringOutgoingRequestsJmsTemplate")
    public JmsTemplate smartMeteringRequestsJmsTemplate() {
        return super.jmsRequestTemplate();
    }

    @Bean(name = "wsSmartMeteringResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer smartMeteringResponseMessageListenerContainer() {
        return super.defaultResponsesMessageListenerContainer(this.smartMeteringResponseMessageListener);
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
