/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.application.config;

import javax.jms.MessageListener;

import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang3.RandomStringUtils;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.ErrorHandler;

import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceResponseMessageSender;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.OsgpRequestMessageSender;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.OsgpResponseMessageListener;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.OslpLogItemRequestMessageSender;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.SigningServerRequestMessageSender;
import com.alliander.osgp.shared.application.config.AbstractMessagingConfig;
import com.alliander.osgp.shared.application.config.jms.JmsConfiguration;
import com.alliander.osgp.shared.application.config.jms.JmsConfigurationFactory;

/**
 * An application context Java configuration class.
 */
@Configuration
@EnableTransactionManagement()
@PropertySources({ @PropertySource("classpath:osgp-adapter-protocol-oslp.properties"),
        @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
        @PropertySource(value = "file:${osgp/AdapterProtocolOslp/config}", ignoreResourceNotFound = true), })
public class MessagingConfig extends AbstractMessagingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingConfig.class);

    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.default.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_BACK_OFF_MULTIPLIER = "jms.default.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_DEFAULT_USE_EXPONENTIAL_BACK_OFF = "jms.default.use.exponential.back.off";

    // JMS Settings: incoming signing server responses
    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_QUEUE = "jms.signing.server.responses.queue";

    @Autowired
    @Qualifier("oslpRequestsMessageListener")
    private MessageListener oslpRequestsMessageListener;

    @Autowired
    @Qualifier("signingServerResponsesMessageListener")
    private MessageListener signingServerResponsesMessageListener;

    public MessagingConfig() {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    // === JMS SETTINGS ===

    @Override
    @Bean
    public RedeliveryPolicy defaultRedeliveryPolicy() {
        final RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(Integer.parseInt(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(Long.parseLong(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY)));
        redeliveryPolicy.setBackOffMultiplier(Double.parseDouble(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(this.environment
                .getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_USE_EXPONENTIAL_BACK_OFF)));

        return redeliveryPolicy;
    }

    // === JMS SETTINGS OSLP REQUESTS ===

    @Bean
    public JmsConfiguration oslpRequestJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeReceiveConfiguration("jms.oslp.requests",
                this.oslpRequestsMessageListener);
    }

    @Bean
    public DefaultMessageListenerContainer oslpRequestsMessageListenerContainer(
            final JmsConfiguration oslpRequestJmsConfiguration) {
        final DefaultMessageListenerContainer messageListenerContainer = oslpRequestJmsConfiguration
                .getMessageListenerContainer();
        messageListenerContainer.setErrorHandler(new ErrorHandler() {
            @Override
            public void handleError(final Throwable t) {
                // Implementing ErrorHandler to prevent logging at WARN level
                // when JMSException is thrown: Execution of JMS message
                // listener failed, and no ErrorHandler has been set.
                LOGGER.debug("oslpRequestsMessageListenerContainer.ErrorHandler.handleError()", t);
            }
        });
        return messageListenerContainer;
    }

    // === JMS SETTINGS: OSLP RESPONSES ===

    @Bean
    public JmsConfiguration oslpResponseJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.oslp.responses");
    }

    @Bean
    public JmsTemplate oslpResponsesJmsTemplate(final JmsConfiguration oslpResponseJmsConfiguration) {
        return oslpResponseJmsConfiguration.getJmsTemplate();
    }

    @Bean
    @DependsOn("oslpSigningService")
    public DeviceResponseMessageSender oslpResponseMessageSender() {
        return new DeviceResponseMessageSender();
    }

    // === JMS SETTINGS: OSLP LOG ITEM REQUESTS ===

    @Bean
    public JmsConfiguration oslpLogItemRequestJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.oslp.log.item.requests");
    }

    @Bean
    public JmsTemplate oslpLogItemRequestsJmsTemplate(final JmsConfiguration oslpLogItemRequestJmsConfiguration) {
        return oslpLogItemRequestJmsConfiguration.getJmsTemplate();
    }

    @Bean
    public OslpLogItemRequestMessageSender oslpLogItemRequestMessageSender() {
        return new OslpLogItemRequestMessageSender();
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

    // === JMS SETTINGS: SIGNING SERVER REQUESTS ===

    @Bean
    public JmsConfiguration signingServerRequestJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.signing.server.requests");
    }

    @Bean
    public JmsTemplate signingServerRequestsJmsTemplate(final JmsConfiguration signingServerRequestJmsConfiguration) {
        return signingServerRequestJmsConfiguration.getJmsTemplate();
    }

    @Bean
    public SigningServerRequestMessageSender signingServerRequestMessageSender() {
        return new SigningServerRequestMessageSender();
    }

    // === JMS SETTINGS SIGNING SERVER RESPONSES ===

    /**
     * Instead of a fixed name for the responses queue, the signing-server uses
     * a 'reply-to' responses queue. This 'reply-to' responses queue is
     * communicated to the signing-server by this Protocol-Adapter-OSLP instance
     * when a request message is sent to the signing-server. The signing-server
     * will send signed response messages to the 'reply-to' queue. This ensures
     * that the signed response messages for this Protocol-Adapter-OSLP instance
     * are sent back to this instance.
     */
    @Bean
    public ActiveMQDestination replyToQueue() {
        final String prefix = this.environment.getRequiredProperty(PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_QUEUE);
        final String randomPostFix = RandomStringUtils.random(10, false, true);
        final String queueName = prefix.concat("-").concat(randomPostFix);

        LOGGER.debug("------> replyToQueue: {}", queueName);

        return new ActiveMQQueue(queueName);
    }

    @Bean
    public JmsConfiguration signingServerResponsesJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeReceiveConfiguration("jms.signing.server.responses",
                this.signingServerResponsesMessageListener, this.replyToQueue());
    }

    @Bean(name = "signingResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer signingResponsesMessageListenerContainer(
            final JmsConfiguration signingServerResponsesJmsConfiguration) {
        return signingServerResponsesJmsConfiguration.getMessageListenerContainer();
    }
}
