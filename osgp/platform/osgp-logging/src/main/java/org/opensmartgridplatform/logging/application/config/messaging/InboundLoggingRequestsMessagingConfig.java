// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.logging.application.config.messaging;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageListener;
import javax.net.ssl.SSLException;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/** Configuration class for inbound logging requests from OSGP WS adapters. */
@Configuration
public class InboundLoggingRequestsMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(InboundLoggingRequestsMessagingConfig.class);

  private final JmsConfigurationFactory jmsConfigurationFactory;

  public InboundLoggingRequestsMessagingConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(environment, defaultJmsConfiguration, "jms.logging");
  }

  @Bean(destroyMethod = "stop", name = "OsgpLoggingInboundLoggingRequestsConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing OsgpLoggingInboundLoggingRequestsConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "OsgpLoggingInboundLoggingRequestsMessageListenerContainer")
  public DefaultMessageListenerContainer messageListenerContainer(
      @Qualifier("OsgpLoggingInboundLoggingRequestsMessageListener")
          final MessageListener messageListener) {
    LOGGER.info("Initializing OsgpLoggingInboundLoggingRequestsMessageListenerContainer bean.");
    return this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
  }
}
