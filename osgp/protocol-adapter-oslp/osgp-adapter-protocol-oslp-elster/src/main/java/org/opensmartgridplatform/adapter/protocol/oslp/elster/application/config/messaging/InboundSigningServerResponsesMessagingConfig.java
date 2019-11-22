/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config.messaging;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.net.ssl.SSLException;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * Configuration class for inbound responses from Signing Server.
 */
@Configuration
public class InboundSigningServerResponsesMessagingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(InboundSigningServerResponsesMessagingConfig.class);

    private static final String PROPERTY_NAME_JMS_SIGNING_SERVER_RESPONSES_QUEUE = "jms.incoming.signing.server.responses.queue";

    private JmsConfigurationFactory jmsConfigurationFactory;
    private Environment environment;

    public InboundSigningServerResponsesMessagingConfig(final Environment environment,
            final DefaultJmsConfiguration defaultJmsConfiguration) throws SSLException {
        this.environment = environment;
        this.jmsConfigurationFactory = new JmsConfigurationFactory(environment, defaultJmsConfiguration,
                "jms.incoming.signing.server.responses");
    }

    @Bean(destroyMethod = "stop", name = "protocolOslpInboundSigningServerResponsesConnectionFactory")
    public ConnectionFactory connectionFactory() {
        LOGGER.info("Initializing protocolOslpInboundSigningServerResponsesJmsConfigurationFactory bean.");
        return this.jmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "protocolOslpInboundSigningServerResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer messageListenerContainer(
            @Qualifier("signingServerResponsesMessageListener") final MessageListener messageListener) {
        LOGGER.info("Initializing protocolOslpInboundSigningServerResponsesMessageListenerContainer bean.");
        return this.jmsConfigurationFactory.initMessageListenerContainer(messageListener, this.replyToQueue());
    }

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
}
