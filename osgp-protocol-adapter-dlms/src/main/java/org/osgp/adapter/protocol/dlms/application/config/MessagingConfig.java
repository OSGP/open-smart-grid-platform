/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.config;

import javax.jms.MessageListener;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsLogItemRequestMessageSender;
import org.osgp.adapter.protocol.dlms.infra.messaging.OsgpRequestMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alliander.osgp.shared.application.config.AbstractMessagingConfig;
import com.alliander.osgp.shared.application.config.jms.JmsConfigurationFactory;

/**
 * An application context Java configuration class.
 */
@Configuration
@EnableTransactionManagement()
@PropertySources({ @PropertySource("classpath:osgp-adapter-protocol-dlms.properties"),
        @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
        @PropertySource(value = "file:${osgp/AdapterProtocolDlms/config}", ignoreResourceNotFound = true), })
public class MessagingConfig extends AbstractMessagingConfig {

    @Autowired
    @Qualifier("dlmsRequestsMessageListener")
    private MessageListener dlmsRequestsMessageListener;

    public MessagingConfig() {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    // === JMS SETTINGS ===

    @Bean
    public DefaultMessageListenerContainer dlmsRequestsMessageListenerContainer(
            JmsConfigurationFactory jmsConfigurationFactory) {

        return jmsConfigurationFactory.initializeConfiguration("jms.dlms.requests", this.dlmsRequestsMessageListener)
                .getMessageListenerContainer();
    }

    @Bean
    public JmsTemplate dlmsResponsesJmsTemplate(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.dlms.responses").getJmsTemplate();
    }

    @Bean
    public DeviceResponseMessageSender dlmsResponseMessageSender() {
        return new DeviceResponseMessageSender();
    }

    @Bean
    public JmsTemplate dlmsLogItemRequestsJmsTemplate(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.dlms.log.item.requests").getJmsTemplate();
    }

    @Bean
    public DlmsLogItemRequestMessageSender dlmsLogItemRequestMessageSender() {
        return new DlmsLogItemRequestMessageSender();
    }

    @Bean
    public DefaultMessageListenerContainer osgpResponsesMessageListenerContainer(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.osgp.responses").getMessageListenerContainer();
    }

    @Bean
    public JmsTemplate osgpRequestsJmsTemplate(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.osgp.requests").getJmsTemplate();
    }

    @Bean
    public OsgpRequestMessageSender osgpRequestMessageSender() {
        return new OsgpRequestMessageSender();
    }
}
