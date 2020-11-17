/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.tariffswitching.application.config.messaging;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.net.ssl.SSLException;

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
 * Configuration class for inbound responses from OSGP Core.
 */
@Configuration
public class InboundOsgpCoreResponsesMessagingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(InboundOsgpCoreResponsesMessagingConfig.class);

    private JmsConfigurationFactory jmsConfigurationFactory;

    public InboundOsgpCoreResponsesMessagingConfig(final Environment environment,
            final DefaultJmsConfiguration defaultJmsConfiguration) throws SSLException {
        this.jmsConfigurationFactory = new JmsConfigurationFactory(environment, defaultJmsConfiguration,
                JmsConfigurationNames.JMS_INCOMING_OSGP_CORE_RESPONSES);
    }

    @Bean(destroyMethod = "stop", name = "domainTariffSwitchingInboundOsgpCoreResponsesConnectionFactory")
    public ConnectionFactory connectionFactory() {
        return this.jmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "domainTariffSwitchingInboundOsgpCoreResponsesMessageListenerContainer")
    public DefaultMessageListenerContainer messageListenerContainer(
            @Qualifier("domainTariffSwitchingInboundOsgpCoreResponsesMessageListener") final MessageListener messageListener) {
        LOGGER.info("Initializing domainTariffSwitchingInboundOsgpCoreResponsesMessageListenerContainer bean.");
        return this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
    }

    @Bean(name = "domainTariffSwitchingInboundOsgpCoreResponsesMessageProcessorMap")
    public MessageProcessorMap messageProcessorMap() {
        return new BaseMessageProcessorMap("InboundOsgpCoreResponsesMessageProcessorMap");
    }

}
