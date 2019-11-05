/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.net.ssl.SSLException;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * An application context Java configuration class.
 */
@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:osgp-adapter-protocol-oslp-elster.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolOslpElster/config}", ignoreResourceNotFound = true)
public class IncomingOsgpCoreRequestsMessagingConfig extends AbstractConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(IncomingOsgpCoreRequestsMessagingConfig.class);

    private JmsConfigurationFactory incomingOsgpCoreRequestsJmsConfigurationFactory;
    private JmsConfigurationFactory outgoingOsgpCoreResponsesJmsConfigurationFactory;

    public IncomingOsgpCoreRequestsMessagingConfig(final Environment environment,
            final DefaultJmsConfiguration defaultJmsConfiguration) throws SSLException {
        this.incomingOsgpCoreRequestsJmsConfigurationFactory = new JmsConfigurationFactory(environment,
                defaultJmsConfiguration, "jms.incoming.osgp.core.requests");
        this.outgoingOsgpCoreResponsesJmsConfigurationFactory = new JmsConfigurationFactory(environment,
                defaultJmsConfiguration, "jms.outgoing.osgp.core.responses");
    }

    // Incoming OSGP Core requests

    @Bean(destroyMethod = "stop", name = "protocolOslpIncomingOsgpCoreRequestsConnectionFactory")
    public ConnectionFactory incomingOsgpCoreRequestsConnectionFactory() {
        LOGGER.info("Initializing incomingOsgpCoreRequestsConnectionFactory bean.");
        return this.incomingOsgpCoreRequestsJmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "protocolOslpIncomingOsgpCoreRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer incomingOsgpCoreRequestsMessageListenerContainer(
            @Qualifier("oslpRequestsMessageListener") final MessageListener oslpRequestsMessageListener) {
        LOGGER.info("Initializing incomingOsgpCoreRequestsMessageListenerContainer bean.");
        return this.incomingOsgpCoreRequestsJmsConfigurationFactory
                .initMessageListenerContainer(oslpRequestsMessageListener);
    }

    // Outgoing OSGP Core responses

    @Bean(destroyMethod = "stop", name = "protocolOslpOutgoingOsgpCoreResponsesConnectionFactory")
    public ConnectionFactory outgoingOsgpCoreResponsesConnectionFactory() {
        LOGGER.info("Initializing outgoingOsgpCoreResponsesConnectionFactory bean.");
        return this.outgoingOsgpCoreResponsesJmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "protocolOslpOutgoingOsgpCoreResponsesJmsTemplate")
    public JmsTemplate oslpResponsesJmsTemplate() {
        LOGGER.info("Initializing outgoingOsgpCoreResponsesJmsTemplate bean.");
        return this.outgoingOsgpCoreResponsesJmsConfigurationFactory.initJmsTemplate();
    }

    @Bean
    @DependsOn("oslpSigningService")
    public DeviceResponseMessageSender oslpResponseMessageSender() {
        return new DeviceResponseMessageSender();
    }

}
