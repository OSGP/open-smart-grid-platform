/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.admin.application.config;

import javax.net.ssl.SSLException;

import org.apache.activemq.pool.PooledConnectionFactory;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationNames;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.opensmartgridplatform.shared.application.config.messaging.JmsDefaultConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@PropertySource("classpath:osgp-adapter-domain-admin.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterDomainAdmin/config}", ignoreResourceNotFound = true)
public class OutgoingWebServiceResponsesMessagingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutgoingWebServiceResponsesMessagingConfig.class);

    private JmsConfigurationFactory jmsConfigurationFactory;

    public OutgoingWebServiceResponsesMessagingConfig(final Environment environment,
            final JmsDefaultConfig defaultMessagingConfig) throws SSLException {
        this.jmsConfigurationFactory = new JmsConfigurationFactory(environment, defaultMessagingConfig,
                JmsConfigurationNames.JMS_OUTGOING_WS_RESPONSES);
    }

    @Bean(destroyMethod = "stop", name = "domainAdminOutgoingWebServiceResponsesPooledConnectionFactory")
    public PooledConnectionFactory outgoingWebServiceResponsesPooledConnectionFactory() {
        LOGGER.info("Initializing pooled connection factory for outgoing web service responses.");
        return this.jmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "domainAdminOutgoingWebServiceResponsesJmsTemplate")
    public JmsTemplate outgoingWebServiceResponsesJmsTemplate() {
        return this.jmsConfigurationFactory.initJmsTemplate();
    }

}
