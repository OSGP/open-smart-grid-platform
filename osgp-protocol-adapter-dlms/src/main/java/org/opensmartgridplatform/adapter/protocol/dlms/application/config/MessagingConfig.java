/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.config;

import javax.jms.MessageListener;

import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsLogItemRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.requests.to.core.OsgpRequestMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.opensmartgridplatform.shared.application.config.AbstractMessagingConfig;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationFactory;

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

    @Autowired
    @Qualifier("osgpResponsesMessageListener")
    private MessageListener osgpResponsesMessageListener;

    public MessagingConfig() {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    // === JMS SETTINGS ===

    // Configuration beans for incoming dlms requests
    @Bean
    public JmsConfiguration dlmsRequestJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeReceiveConfiguration("jms.dlms.requests",
                this.dlmsRequestsMessageListener);
    }

    @Bean
    public DefaultMessageListenerContainer dlmsRequestsMessageListenerContainer(
            final JmsConfiguration dlmsRequestJmsConfiguration) {
        return dlmsRequestJmsConfiguration.getMessageListenerContainer();
    }

    // Configuration beans for outgoing dlms responses
    @Bean
    public JmsConfiguration dlmsResponseJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.dlms.responses");
    }

    @Bean
    public JmsTemplate dlmsResponsesJmsTemplate(final JmsConfiguration dlmsResponseJmsConfiguration) {
        return dlmsResponseJmsConfiguration.getJmsTemplate();
    }

    @Bean
    public DeviceResponseMessageSender dlmsResponseMessageSender() {
        return new DeviceResponseMessageSender();
    }

    // Configuration beans for outgoing dlms log items requests
    @Bean
    public JmsConfiguration dlmsLogItemRequestJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.dlms.log.item.requests");
    }

    @Bean
    public JmsTemplate dlmsLogItemRequestsJmsTemplate(final JmsConfiguration dlmsLogItemRequestJmsConfiguration) {
        return dlmsLogItemRequestJmsConfiguration.getJmsTemplate();
    }

    @Bean
    public DlmsLogItemRequestMessageSender dlmsLogItemRequestMessageSender() {
        return new DlmsLogItemRequestMessageSender();
    }

    // Configuration beans for incoming osgp responses
    @Bean
    public JmsConfiguration osgpResponseJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeReceiveConfiguration("jms.osgp.responses",
                this.osgpResponsesMessageListener);
    }

    @Bean
    public DefaultMessageListenerContainer osgpResponsesMessageListenerContainer(
            final JmsConfiguration osgpResponseJmsConfiguration) {
        return osgpResponseJmsConfiguration.getMessageListenerContainer();
    }

    // Configuration beans for outgoing osgp requests
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
}
