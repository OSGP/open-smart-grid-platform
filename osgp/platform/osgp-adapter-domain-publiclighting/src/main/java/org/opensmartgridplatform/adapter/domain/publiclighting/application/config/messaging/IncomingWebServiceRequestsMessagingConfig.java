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
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationNames;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
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
 * Configuration class for incoming web service requests.
 */
@Configuration
public class IncomingWebServiceRequestsMessagingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(IncomingWebServiceRequestsMessagingConfig.class);

    private JmsConfigurationFactory jmsConfigurationFactory;

    public IncomingWebServiceRequestsMessagingConfig(final Environment environment,
            final DefaultJmsConfiguration defaultJmsConfiguration) throws SSLException {
        this.jmsConfigurationFactory = new JmsConfigurationFactory(environment, defaultJmsConfiguration,
                JmsConfigurationNames.JMS_INCOMING_WS_REQUESTS);
    }

    @Bean(destroyMethod = "stop", name = "domainPublicLightingIncomingWebServiceRequestsConnectionFactory")
    public ConnectionFactory connectionFactory() {
        LOGGER.info("Initializing domainPublicLightingIncomingWebServiceRequestsConnectionFactory bean.");
        return this.jmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "domainPublicLightingIncomingWebServiceRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer messageListenerContainer(
            @Qualifier("domainPublicLightingIncomingWebServiceRequestsMessageListener") final WebServiceRequestMessageListener messageListener) {
        LOGGER.info("Initializing domainPublicLightingIncomingWebServiceRequestsMessageListenerContainer bean.");
        return this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
    }

    @Bean
    @Qualifier("domainPublicLightingIncomingWebServiceRequestsMessageProcessorMap")
    public MessageProcessorMap messageProcessorMap() {
        return new BaseMessageProcessorMap("domainPublicLightingIncomingWebServiceRequestsMessageProcessorMap");
    }

}
