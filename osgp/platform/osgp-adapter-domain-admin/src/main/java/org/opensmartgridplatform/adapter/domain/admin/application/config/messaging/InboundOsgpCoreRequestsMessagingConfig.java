/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.admin.application.config.messaging;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.net.ssl.SSLException;

import org.opensmartgridplatform.shared.application.config.messaging.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationNames;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/**
 * Configuration class for inbound requests from OSGP Core
 */
@Configuration
public class InboundOsgpCoreRequestsMessagingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(InboundOsgpCoreRequestsMessagingConfig.class);

    private JmsConfigurationFactory jmsConfigurationFactory;

    public InboundOsgpCoreRequestsMessagingConfig(final Environment environment,
            final JmsConfiguration defaultJmsConfiguration) throws SSLException {
        this.jmsConfigurationFactory = new JmsConfigurationFactory(environment, defaultJmsConfiguration,
                JmsConfigurationNames.JMS_INCOMING_OSGP_CORE_REQUESTS);
    }

    @Bean(destroyMethod = "stop", name = "domainAdminInboundOsgpCoreRequestsConnectionFactory")
    public ConnectionFactory connectionFactory() {
        LOGGER.info("Initializing domainAdminInboundOsgpCoreRequestsConnectionFactory bean.");
        return this.jmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "domainAdminInboundOsgpCoreRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer messageListenerContainer(
            @Qualifier("domainAdminInboundOsgpCoreRequestsMessageListener") final MessageListener messageListener) {
        LOGGER.info("Initializing domainAdminInboundOsgpCoreRequestsMessageListenerContainer bean.");
        return this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
    }

    @Bean(name = "domainAdminInboundOsgpCoreRequestsMessageProcessorMap")
    public MessageProcessorMap messageProcessorMap() {
        return new BaseMessageProcessorMap("domainAdminInboundOsgpCoreRequestsMessageProcessorMap");
    }
}
