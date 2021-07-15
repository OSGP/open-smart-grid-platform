/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.core.application.config.messaging;

import javax.jms.ConnectionFactory;
import javax.net.ssl.SSLException;
import org.opensmartgridplatform.adapter.domain.core.infra.jms.OsgpCoreRequestMessageListener;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/** Configuration class for inbound requests from OSGP Core */
@Configuration
public class InboundOsgpCoreRequestsMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(InboundOsgpCoreRequestsMessagingConfig.class);

  private JmsConfigurationFactory jmsConfigurationFactory;

  public InboundOsgpCoreRequestsMessagingConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(
            environment,
            defaultJmsConfiguration,
            JmsConfigurationNames.JMS_OSGP_CORE_REQUESTS_INCOMING);
  }

  @Bean(destroyMethod = "stop", name = "domainCoreInboundOsgpCoreRequestsConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing domainCoreInboundOsgpCoreRequestsConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "domainCoreInboundOsgpCoreRequestsMessageListenerContainer")
  public DefaultMessageListenerContainer messageListenerContainer(
      @Qualifier("domainCoreInboundOsgpCoreRequestsMessageListener")
          final OsgpCoreRequestMessageListener messageListener) {
    LOGGER.info("Initializing domainCoreInboundOsgpCoreRequestsMessageListenerContainer bean.");
    return this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
  }
}
