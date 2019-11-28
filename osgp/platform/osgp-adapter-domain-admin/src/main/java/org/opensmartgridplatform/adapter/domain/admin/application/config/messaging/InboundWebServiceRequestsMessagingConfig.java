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
import org.opensmartgridplatform.adapter.domain.admin.infra.jms.ws.WebServiceRequestMessageListener;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationNames;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
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
 * Configuration class for inbound requests from web service adapter
 */
@Configuration
public class InboundWebServiceRequestsMessagingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(InboundWebServiceRequestsMessagingConfig.class);

    private JmsConfigurationFactory jmsConfigurationFactory;

    public InboundWebServiceRequestsMessagingConfig(final Environment environment,
            final JmsConfiguration defaultJmsConfiguration) throws SSLException {
        this.jmsConfigurationFactory = new JmsConfigurationFactory(environment, defaultJmsConfiguration,
                JmsConfigurationNames.JMS_INCOMING_WS_REQUESTS);
    }

    @Bean(destroyMethod = "stop", name = "incomingWebServiceRequestsPooledConnectionFactory")
    public PooledConnectionFactory incomingWebServiceRequestsPooledConnectionFactory() {
        LOGGER.info("Initializing pooled connection factory for incoming web service requests.");
        return this.jmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "domainAdminIncomingWebServiceRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer incomingWebServiceRequestsMessageListenerContainer(
            @Qualifier("domainAdminIncomingWebServiceRequestMessageListener") final WebServiceRequestMessageListener incomingWebServiceRequestMessageListener) {
        return this.jmsConfigurationFactory.initMessageListenerContainer(incomingWebServiceRequestMessageListener);
    }

    @Bean
    @Qualifier("domainAdminWebServiceRequestMessageProcessorMap")
    public MessageProcessorMap incomingWebServiceRequestMessageProcessorMap() {
        return new BaseMessageProcessorMap("WebServiceRequestMessageProcessorMap");
    }
}
