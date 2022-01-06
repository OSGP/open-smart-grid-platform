/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.config.messaging;

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

/** Configuration class for inbound responses from domain adapter. */
@Configuration
public class InboundDomainResponsesMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(InboundDomainResponsesMessagingConfig.class);

  private final JmsConfigurationFactory jmsConfigurationFactory;

  public InboundDomainResponsesMessagingConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(
            environment, defaultJmsConfiguration, JmsConfigurationNames.JMS_COMMON_RESPONSES);
  }

  @Bean(destroyMethod = "stop", name = "wsCoreInboundDomainResponsesConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing wsCoreInboundDomainResponsesConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "wsCoreInboundDomainResponsesMessageListenerContainer")
  public DefaultMessageListenerContainer messageListenerContainer(
      @Qualifier("wsCoreInboundDomainResponsesMessageListener")
          final MessageListener messageListener) {
    LOGGER.info("Initializing wsCoreInboundDomainResponseMessageListenerContainer bean.");
    return this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
  }

  @Bean
  @Qualifier("wsCoreInboundDomainResponsesMessageProcessorMap")
  public MessageProcessorMap coreResponseMessageProcessorMap() {
    return new BaseMessageProcessorMap("domainResponseMessageProcessorMap");
  }
}
