/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.admin.application.config.messaging;

import javax.jms.ConnectionFactory;
import javax.net.ssl.SSLException;

import org.opensmartgridplatform.adapter.ws.infra.jms.LoggingMessageSender;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationNames;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

/**
 * Configuration class for outgoing logging requests
 */
@Configuration
public class OutgoingLoggingRequestsMessagingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutgoingLoggingRequestsMessagingConfig.class);

    private JmsConfigurationFactory outgoingLoggingRequestsJmsConfigurationFactory;

    public OutgoingLoggingRequestsMessagingConfig(final Environment environment,
            final DefaultJmsConfiguration defaultJmsConfiguration) throws SSLException {
        this.outgoingLoggingRequestsJmsConfigurationFactory = new JmsConfigurationFactory(environment,
                defaultJmsConfiguration, JmsConfigurationNames.JMS_COMMON_LOGGING);
    }

    @Bean(destroyMethod = "stop", name = "wsAdminOutgoingLoggingRequestsConnectionFactory")
    public ConnectionFactory outgoingLoggingRequestsConnectionFactory() {
        LOGGER.info("Initializing outgoingLoggingRequestsConnectionFactory bean.");
        return this.outgoingLoggingRequestsJmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "loggingJmsTemplate")
    public JmsTemplate outgoingLoggingRequestsJmsTemplate() {
        LOGGER.info("Initializaing outgoingLoggingRequestsJmsTemplate bean.");
        return this.outgoingLoggingRequestsJmsConfigurationFactory.initJmsTemplate();
    }

    @Bean
    public LoggingMessageSender loggingMessageSender() {
        return new LoggingMessageSender();
    }
}
