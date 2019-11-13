/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.admin.application.config.messaging;

import javax.net.ssl.SSLException;

import org.apache.activemq.pool.PooledConnectionFactory;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationNames;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class OutboundOsgpCoreRequestsMessagingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboundOsgpCoreRequestsMessagingConfig.class);

    private JmsConfigurationFactory jmsConfigurationFactory;

    public OutboundOsgpCoreRequestsMessagingConfig(final Environment environment,
            final JmsConfiguration defaultJmsConfiguration) throws SSLException {
        this.jmsConfigurationFactory = new JmsConfigurationFactory(environment, defaultJmsConfiguration,
                JmsConfigurationNames.JMS_OUTGOING_OSGP_CORE_REQUESTS);
    }

    @Bean(destroyMethod = "stop", name = "domainAdminOutgoingOsgpCoreRequestsPooledConnectionFactory")
    public PooledConnectionFactory outgoingOsgpCoreRequestsPooledConnectionFactory() {
        LOGGER.info("Initializing pooled connection factory for outgoing OSGP core requests.");
        return this.jmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "domainAdminOutgoingOsgpCoreRequestsJmsTemplate")
    public JmsTemplate outgoingOsgpCoreRequestsJmsTemplate() {
        return this.jmsConfigurationFactory.initJmsTemplate();
    }

}
