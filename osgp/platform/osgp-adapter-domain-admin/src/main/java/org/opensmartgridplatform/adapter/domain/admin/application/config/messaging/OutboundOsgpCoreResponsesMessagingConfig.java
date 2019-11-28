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

/**
 * Configuration class for outbound responses to OSGP Core
 */
@Configuration
public class OutboundOsgpCoreResponsesMessagingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboundOsgpCoreResponsesMessagingConfig.class);

    private JmsConfigurationFactory jmsConfigurationFactory;

    public OutboundOsgpCoreResponsesMessagingConfig(final Environment environment,
            final JmsConfiguration defaultJmsConfiguration) throws SSLException {
        this.jmsConfigurationFactory = new JmsConfigurationFactory(environment, defaultJmsConfiguration,
                JmsConfigurationNames.JMS_OUTGOING_OSGP_CORE_RESPONSES);
    }

    @Bean(destroyMethod = "stop", name = "domainAdminOutgoingOsgpCoreResponsesPooledConnectionFactory")
    public PooledConnectionFactory outgoingOsgpCoreResponsesPooledConnectionFactory() {
        LOGGER.info("Initializing pooled connection factory for outgoing OSGP core responses.");
        return this.jmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "domainAdminOutgoingOsgpCoreResponsesJmsTemplate")
    public JmsTemplate outgoingOsgpCoreResponsesJmsTemplate() {
        return this.jmsConfigurationFactory.initJmsTemplate();
    }

}
