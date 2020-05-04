/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.config.messaging;

import javax.jms.ConnectionFactory;

import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

/**
 * Configuration class for outbound log item requests.
 */
@Configuration
public class OutboundLogItemRequestsMessagingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboundLogItemRequestsMessagingConfig.class);

    @Bean(destroyMethod = "stop", name = "protocolDlmsOutboundLogItemRequestsConnectionFactory")
    public ConnectionFactory connectionFactory(JmsConfigurationFactory jmsConfigurationFactory) {
        LOGGER.info("Initializing protocolDlmsOutboundLogItemRequestsConnectionFactory bean.");
        return jmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "protocolDlmsOutboundLogItemRequestsJmsTemplate")
    public JmsTemplate jmsTemplate(JmsConfigurationFactory jmsConfigurationFactory) {
        LOGGER.info("Initializing protocolDlmsOutboundLogItemRequestsJmsTemplate bean.");
        return jmsConfigurationFactory.initJmsTemplate();
    }
}
