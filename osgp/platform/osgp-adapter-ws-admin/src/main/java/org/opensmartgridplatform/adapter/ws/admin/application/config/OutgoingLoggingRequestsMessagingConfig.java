/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.admin.application.config;

import javax.jms.ConnectionFactory;
import javax.net.ssl.SSLException;

import org.opensmartgridplatform.adapter.ws.infra.jms.LoggingMessageSender;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationNames;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-admin.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsAdmin/config}", ignoreResourceNotFound = true)
public class OutgoingLoggingRequestsMessagingConfig extends AbstractConfig {

    private JmsConfigurationFactory outgoingLoggingRequestsJmsConfigurationFactory;

    public OutgoingLoggingRequestsMessagingConfig(final Environment environment,
            final DefaultJmsConfiguration defaultJmsConfiguration) throws SSLException {
        this.outgoingLoggingRequestsJmsConfigurationFactory = new JmsConfigurationFactory(environment,
                defaultJmsConfiguration, JmsConfigurationNames.JMS_COMMON_LOGGING);
    }

    // Outgoing logging requests

    @Bean(destroyMethod = "stop", name = "wsAdminOutgoingLoggingRequestsConnectionFactory")
    public ConnectionFactory outgoingLoggingRequestsConnectionFactory() {
        return this.outgoingLoggingRequestsJmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "loggingJmsTemplate")
    public JmsTemplate outgoingLoggingRequestsJmsTemplate() {
        return this.outgoingLoggingRequestsJmsConfigurationFactory.initJmsTemplate();
    }

    @Bean
    public LoggingMessageSender loggingMessageSender() {
        return new LoggingMessageSender();
    }
}
