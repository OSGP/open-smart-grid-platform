/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import javax.annotation.Resource;
import javax.jms.MessageListener;

import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
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
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OsgpRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OsgpResponseMessageListener;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpLogItemRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.SigningServerRequestMessageSender;
import org.opensmartgridplatform.shared.application.config.AbstractMessagingConfig;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationFactory;

/**
 * An application context Java configuration class.
 */
@Configuration
@EnableTransactionManagement()
@PropertySources({ @PropertySource("classpath:osgp-adapter-protocol-oslp-elster.properties"),
        @PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true),
        @PropertySource(value = "file:${osgp/AdapterProtocolOslpElster/config}", ignoreResourceNotFound = true), })
public class MessagingConfig extends AbstractMessagingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingConfig.class);

    // JMS Settings
    private static final String PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY = "jms.default.initial.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES = "jms.default.maximum.redeliveries";
    private static final String PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY = "jms.default.maximum.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY = "jms.default.redelivery.delay";
    private static final String PROPERTY_NAME_JMS_DEFAULT_BACK_OFF_MULTIPLIER = "jms.default.back.off.multiplier";
    private static final String PROPERTY_NAME_JMS_DEFAULT_USE_EXPONENTIAL_BACK_OFF = "jms.default.use.exponential.back.off";

    // JMS Settings: incoming signing server responses
    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_QUEUE = "jms.signing.server.responses.queue";

    @Resource
    private Environment environment;

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
        redeliveryPolicy.setInitialRedeliveryDelay(Long
                .parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_INITIAL_REDELIVERY_DELAY)));
        redeliveryPolicy.setMaximumRedeliveries(
                Integer.parseInt(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERIES)));
        redeliveryPolicy.setMaximumRedeliveryDelay(Long
                .parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_MAXIMUM_REDELIVERY_DELAY)));
        redeliveryPolicy.setRedeliveryDelay(
                Long.parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_REDELIVERY_DELAY)));
        redeliveryPolicy.setBackOffMultiplier(Double
                .parseDouble(this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_BACK_OFF_MULTIPLIER)));
        redeliveryPolicy.setUseExponentialBackOff(Boolean.parseBoolean(
                this.environment.getRequiredProperty(PROPERTY_NAME_JMS_DEFAULT_USE_EXPONENTIAL_BACK_OFF)));

        return redeliveryPolicy;
    }

    // === JMS SETTINGS OSLP ELSTER REQUESTS ===

    @Bean
    public JmsConfiguration oslpRequestJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeReceiveConfiguration("jms.oslp.elster.requests",
                this.oslpRequestsMessageListener);
    }

    @Bean
    public DefaultMessageListenerContainer oslpRequestsMessageListenerContainer(
            final JmsConfiguration oslpRequestJmsConfiguration) {
        return oslpRequestJmsConfiguration.getMessageListenerContainer();
    }

    // === JMS SETTINGS: OSLP ELSTER RESPONSES ===

    @Bean
    public JmsConfiguration oslpResponseJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeConfiguration("jms.oslp.elster.responses");
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
    public JmsConfiguration signingServerRequestJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory) {
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
        final String queueName = this.createUniqueQueueName(PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_QUEUE);
        LOGGER.info("------> replyToQueue: {}", queueName);
        return new ActiveMQQueue(queueName);
    }

    // Response queue name helper function.

    private String createUniqueQueueName(final String responseQueuePropertyName) {
        final String responsesQueuePrefix = this.environment.getRequiredProperty(responseQueuePropertyName);
        String postFix;
        try {
            postFix = InetAddress.getLocalHost().getHostName();
        } catch (final UnknownHostException e) {
            LOGGER.info("Hostname not available: " + e.getMessage(), e);
            postFix = UUID.randomUUID().toString();
        }
        return responsesQueuePrefix + "-" + postFix;
    }

    @Bean
    public JmsConfiguration signingServerResponsesJmsConfiguration(
            final JmsConfigurationFactory jmsConfigurationFactory) {
        return jmsConfigurationFactory.initializeReceiveConfiguration("jms.signing.server.responses",
                this.signingServerResponsesMessageListener, this.replyToQueue());
    }

    @Bean(name = "signingResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer signingResponsesMessageListenerContainer(
            final JmsConfiguration signingServerResponsesJmsConfiguration) {
        return signingServerResponsesJmsConfiguration.getMessageListenerContainer();
    }
}
