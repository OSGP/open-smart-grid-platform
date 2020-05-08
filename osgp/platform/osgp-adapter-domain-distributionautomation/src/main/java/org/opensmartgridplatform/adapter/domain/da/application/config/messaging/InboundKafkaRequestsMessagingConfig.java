/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.application.config.messaging;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.net.ssl.SSLException;

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
 * An application context Java configuration class.
 */
@Configuration
public class InboundKafkaRequestsMessagingConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(InboundKafkaRequestsMessagingConfig.class);

    private final JmsConfigurationFactory jmsConfigurationFactory;

    public InboundKafkaRequestsMessagingConfig(final Environment environment,
            final DefaultJmsConfiguration defaultJmsConfiguration) throws SSLException {
        this.jmsConfigurationFactory = new JmsConfigurationFactory(environment, defaultJmsConfiguration,
                "jms.inbound.kafka.requests");
    }

    @Bean(destroyMethod = "stop", name = "domainDistributionAutomationInboundKafkaConnectionFactory")
    public ConnectionFactory connectionFactory() {
        LOGGER.info("Initializing domainDistributionAutomationInboundKafkaConnectionFactory bean.");
        return this.jmsConfigurationFactory.getPooledConnectionFactory();
    }

    @Bean(name = "domainDistributionAutomationInboundKafkaRequestsMessageListenerContainer")
    public DefaultMessageListenerContainer messageListenerContainer(
            @Qualifier("domainDistributionAutomationInboundKafkaRequestsMessageListener") final MessageListener messageListener) {
        LOGGER.info("Initializing domainDistributionAutomationInboundKafkaRequestsMessageListenerContainer bean.");
        return this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
    }

    @Bean("domainDistributionAutomationInboundKafkaRequestsMessageProcessorMap")
    public MessageProcessorMap messageProcessorMap() {
        LOGGER.info("Initializing domainDistributionAutomationInboundKafkaRequestsMessageProcessorMap bean.");
        return new BaseMessageProcessorMap("domainDistributionAutomationInboundKafkaRequestsMessageProcessorMap");
    }

}
