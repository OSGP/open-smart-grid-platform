/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.config.messaging;

import javax.jms.ConnectionFactory;
import javax.net.ssl.SSLException;

import org.opensmartgridplatform.adapter.domain.publiclighting.infra.jms.ws.WebServiceRequestMessageListener;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
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
 * Configuration class for inbound web service requests.
 */
@Configuration
public class InboundWebServiceRequestsMessagingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(InboundWebServiceRequestsMessagingConfig.class);

    private JmsConfigurationFactory jmsConfigurationFactory;

    public InboundWebServiceRequestsMessagingConfig(final Environment environment,
            final DefaultJmsConfiguration defaultJmsConfiguration) throws SSLException {
        this.jmsConfigurationFactory = new JmsConfigurationFactory(environment, defaultJmsConfiguration,
                JmsConfigurationNames.JMS_INCOMING_WS_REQUESTS);
    }

    @Bean(destroyMethod = "stop", name = "domainPublicLightingInboundWebServiceRequestsConnectionFactory")
    public ConnectionFactory connectionFactory() {
        LOGGER.info("Initializing domainPublicLightingInboundWebServiceRequestsConnectionFactory bean.");
        return this.jmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "domainPublicLightingInboundWebServiceRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer messageListenerContainer(
            @Qualifier("domainPublicLightingInboundWebServiceRequestsMessageListener") final WebServiceRequestMessageListener messageListener) {
        LOGGER.info("Initializing domainPublicLightingInboundWebServiceRequestsMessageListenerContainer bean.");
        return this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
    }

    @Bean
    @Qualifier("domainPublicLightingInboundWebServiceRequestsMessageProcessorMap")
    public MessageProcessorMap messageProcessorMap() {
        return new BaseMessageProcessorMap("domainPublicLightingInboundWebServiceRequestsMessageProcessorMap");
    }

}
