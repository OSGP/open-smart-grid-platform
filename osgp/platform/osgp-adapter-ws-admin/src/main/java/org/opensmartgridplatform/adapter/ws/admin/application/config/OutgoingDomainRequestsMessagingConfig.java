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

import org.opensmartgridplatform.adapter.ws.admin.infra.jms.AdminRequestMessageSender;
import org.opensmartgridplatform.adapter.ws.admin.infra.jms.AdminResponseMessageFinder;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationNames;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@PropertySource("classpath:osgp-adapter-ws-admin.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterWsAdmin/config}", ignoreResourceNotFound = true)
public class OutgoingDomainRequestsMessagingConfig extends AbstractConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutgoingDomainRequestsMessagingConfig.class);

    private JmsConfigurationFactory outgoingDomainRequestsJmsConfigurationFactory;
    private JmsConfigurationFactory incomingDomainResponsesJmsConfigurationFactory;

    @Value("${jms.admin.responses.receive.timeout:100}")
    private long receiveTimeout;

    public OutgoingDomainRequestsMessagingConfig(final Environment environment,
            final DefaultJmsConfiguration defaultJmsConfiguration) throws SSLException {
        this.outgoingDomainRequestsJmsConfigurationFactory = new JmsConfigurationFactory(environment,
                defaultJmsConfiguration, JmsConfigurationNames.JMS_ADMIN_REQUESTS);
        this.incomingDomainResponsesJmsConfigurationFactory = new JmsConfigurationFactory(environment,
                defaultJmsConfiguration, JmsConfigurationNames.JMS_ADMIN_RESPONSES);
    }

    // Outgoing domain requests

    @Bean(destroyMethod = "stop", name = "wsAdminOutgoingDomainRequestsConnectionFactory")
    public ConnectionFactory outgoingDomainRequestsConnectionFactory() {
        LOGGER.info("Initializing outgoingDomainRequestsConnectionFactory bean.");
        return this.outgoingDomainRequestsJmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "wsAdminOutgoingDomainRequestsJmsTemplate")
    public JmsTemplate outgoingDomainRequestsJmsTemplate() {
        LOGGER.info("Initializing outgoingDomainRequestsJmsTemplate bean.");
        return this.outgoingDomainRequestsJmsConfigurationFactory.initJmsTemplate();
    }

    @Bean
    public AdminRequestMessageSender adminRequestMessageSender() {
        return new AdminRequestMessageSender();
    }

    // Incoming domain responses

    @Bean(destroyMethod = "stop", name = "wsAdminIncomingDomainResponsesConnectionFactory")
    public ConnectionFactory incomingDomainResponsesConnectionFactory() {
        LOGGER.info("Initializing incomingDomainResponsesConnectionFactory bean.");
        return this.incomingDomainResponsesJmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "wsAdminIncomingDomainResponsesJmsTemplate")
    public JmsTemplate incomingDomainResponsesJmsTemplate() {
        LOGGER.info("Initializing incomingDomainResponsesJmsTemplate bean with receive timeout {}.",
                this.receiveTimeout);
        final JmsTemplate jmsTemplate = this.incomingDomainResponsesJmsConfigurationFactory.initJmsTemplate();
        jmsTemplate.setReceiveTimeout(this.receiveTimeout);
        return jmsTemplate;
    }

    @Bean
    public AdminResponseMessageFinder adminResponseMessageFinder() {
        return new AdminResponseMessageFinder();
    }
}
