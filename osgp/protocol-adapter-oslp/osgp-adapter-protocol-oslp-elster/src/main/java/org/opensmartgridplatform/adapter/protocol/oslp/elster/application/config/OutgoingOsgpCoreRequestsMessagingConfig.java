/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config;

import javax.jms.ConnectionFactory;
import javax.net.ssl.SSLException;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OsgpRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OsgpResponseMessageListener;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
public class OutgoingOsgpCoreRequestsMessagingConfig extends AbstractConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutgoingOsgpCoreRequestsMessagingConfig.class);

    private JmsConfigurationFactory outgoingOsgpCoreRequestsJmsConfigurationFactory;
    private JmsConfigurationFactory incomingOsgpCoreResponsesJmsConfigurationFactory;

    public OutgoingOsgpCoreRequestsMessagingConfig(final Environment environment,
            final DefaultJmsConfiguration defaultJmsConfiguration) throws SSLException {
        this.outgoingOsgpCoreRequestsJmsConfigurationFactory = new JmsConfigurationFactory(environment,
                defaultJmsConfiguration, "jms.outgoing.osgp.core.requests");
        this.incomingOsgpCoreResponsesJmsConfigurationFactory = new JmsConfigurationFactory(environment,
                defaultJmsConfiguration, "jms.incoming.osgp.core.responses");
    }

    // Outgoing OSGP Core requests

    @Bean(destroyMethod = "stop", name = "protocolOslpOutgoingOsgpCoreRequestsConnectionFactory")
    public ConnectionFactory outgoingOsgpCoreRequestsConnectionFactory() {
        LOGGER.info("Initializing outgoingRequestsConnectionFactory bean.");
        return this.outgoingOsgpCoreRequestsJmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "protocolOslpOutgoingOsgpCoreRequestsJmsTemplate")
    public JmsTemplate outgoingOspgCoreRequestsJmsTemplate() {
        LOGGER.info("Initializing outgoingOsgpRequestsJmsTemplate bean.");
        return this.outgoingOsgpCoreRequestsJmsConfigurationFactory.initJmsTemplate();
    }

    @Bean
    public OsgpRequestMessageSender outgoingOsgpCoreRequestMessageSender() {
        LOGGER.info("Initializing outgoingOsgpCoreRequestMessageSender bean.");
        return new OsgpRequestMessageSender();
    }

    // Incoming OSGP Core responses

    @Bean
    public OsgpResponseMessageListener incomingOsgpCoreResponseMessageListener() {
        LOGGER.info("Initializing osgpResponseMessageListener bean.");
        return new OsgpResponseMessageListener();
    }

    @Bean(destroyMethod = "stop", name = "protocolOslpIncomingOsgpCoreResponsesConnectionFactory")
    public ConnectionFactory incomingOsgpCoreResponsesConnectionFactory() {
        LOGGER.info("Initializing incomingOsgpResponsesConnectionFactory bean.");
        return this.incomingOsgpCoreResponsesJmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "protocolOslpIncomingOsgCoreResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer incomingOsgpResponsesMessageListenerContainer() {
        LOGGER.info("Initializing incomingOsgpResponsesMessageListenerContainer");
        return this.incomingOsgpCoreResponsesJmsConfigurationFactory
                .initMessageListenerContainer(this.incomingOsgpCoreResponseMessageListener());
    }
}
