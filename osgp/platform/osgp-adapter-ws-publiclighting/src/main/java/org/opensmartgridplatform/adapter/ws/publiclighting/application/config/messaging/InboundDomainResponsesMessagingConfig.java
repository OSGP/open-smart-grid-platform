/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.publiclighting.application.config.messaging;

import javax.jms.ConnectionFactory;
import javax.net.ssl.SSLException;
import org.opensmartgridplatform.adapter.ws.publiclighting.infra.jms.PublicLightingResponseMessageListener;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationNames;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/** Configuration class for inbound responses from domain adapter. */
@Configuration
public class InboundDomainResponsesMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(InboundDomainResponsesMessagingConfig.class);

  private JmsConfigurationFactory jmsConfigurationFactory;

  @Value("${jms.publiclighting.responses.receive.timeout:100}")
  private long receiveTimeout;

  public InboundDomainResponsesMessagingConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(
            environment,
            defaultJmsConfiguration,
            JmsConfigurationNames.JMS_PUBLICLIGHTING_RESPONSES);
  }

  @Bean(destroyMethod = "stop", name = "wsPublicLightingInboundDomainResponsesConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing wsPublicLightingInboundDomainResponsesConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "wsPublicLightingInboundDomainResponsesJmsTemplate")
  public JmsTemplate jmsTemplate() {
    LOGGER.info(
        "Initializing wsPublicLightingInboundDomainResponsesJmsTemplate with receive timeout {}.",
        this.receiveTimeout);
    final JmsTemplate jmsTemplate = this.jmsConfigurationFactory.initJmsTemplate();
    jmsTemplate.setReceiveTimeout(this.receiveTimeout);
    return jmsTemplate;
  }

  @Bean(name = "wsPublicLightingInboundDomainResponsesMessageListenerContainer")
  public DefaultMessageListenerContainer messageListenerContainer(
      @Qualifier("wsPublicLightingInboundDomainResponsesMessageListener")
          final PublicLightingResponseMessageListener responseMessageListener) {
    LOGGER.info(
        "Initializing wsPublicLightingInboundDomainResponsesMessageListenerContainer bean.");
    final DefaultMessageListenerContainer container =
        this.jmsConfigurationFactory.initMessageListenerContainer(responseMessageListener);
    // Only consume messages defined by the message selector string.
    // All other messages will be retrieved using
    // {@link PublicLightingResponseMessageFinder}
    container.setMessageSelector("JMSType = 'SET_LIGHT_SCHEDULE'");
    return container;
  }

  @Bean(name = "wsPublicLightingInboundDomainResponsesMessageProcessorMap")
  public MessageProcessorMap messageProcessorMap() {
    return new BaseMessageProcessorMap("wsPublicLightingInboundDomainResponsesMessageProcessorMap");
  }
}
