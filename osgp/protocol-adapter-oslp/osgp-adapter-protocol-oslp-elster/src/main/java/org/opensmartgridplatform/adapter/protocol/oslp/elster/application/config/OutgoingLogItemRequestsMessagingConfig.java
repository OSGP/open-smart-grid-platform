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

import org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging.OslpLogItemRequestMessageSender;
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
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * An application context Java configuration class.
 */
@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:osgp-adapter-protocol-oslp-elster.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolOslpElster/config}", ignoreResourceNotFound = true)
public class OutgoingLogItemRequestsMessagingConfig extends AbstractConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutgoingLogItemRequestsMessagingConfig.class);

    private JmsConfigurationFactory outgoingLogItemRequestsJmsConfigurationFactory;

    public OutgoingLogItemRequestsMessagingConfig(final Environment environment,
            final DefaultJmsConfiguration defaultJmsConfiguration) throws SSLException {
        this.outgoingLogItemRequestsJmsConfigurationFactory = new JmsConfigurationFactory(environment,
                defaultJmsConfiguration, "jms.outgoing.log.item.requests");
    }

    // Outgoing log item requests

    @Bean(destroyMethod = "stop", name = "protocolOslpOutgoingLogItemRequestsConnectionFactory")
    public ConnectionFactory outgoingLogItemRequestsConnectionFactory() {
        LOGGER.info("Initializing outgoingLogItemRequestsConnectionFactory bean.");
        return this.outgoingLogItemRequestsJmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "protocolOslpOutgoingLogItemRequestsJmsTemplate")
    public JmsTemplate outgoingLogItemRequestsJmsTemplate() {
        LOGGER.info("Initializing outgoingLogItemRequestsJmsTemplate bean.");
        return this.outgoingLogItemRequestsJmsConfigurationFactory.initJmsTemplate();
    }

    @Bean
    public OslpLogItemRequestMessageSender outgoingLogItemRequestMessageSender() {
        LOGGER.info("Initializing outgoingLogItemRequestMessageSender bean.");
        return new OslpLogItemRequestMessageSender();
    }

}
