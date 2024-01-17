// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.application.config.messaging;

import jakarta.jms.ConnectionFactory;
import javax.net.ssl.SSLException;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

/** Configuration class for outbound log item requests to OSGP Logging. */
@Configuration
public class OutboundLogItemRequestsMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(OutboundLogItemRequestsMessagingConfig.class);

  private final JmsConfigurationFactory jmsConfigurationFactory;

  public OutboundLogItemRequestsMessagingConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(
            environment, defaultJmsConfiguration, "jms.iec60870.log.item.requests");
  }

  @Bean(destroyMethod = "stop", name = "protocolIec60870OutboundLogItemRequestsConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing protocolIec60870OutboundLogItemRequestsConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "protocolIec60870OutboundLogItemRequestsJmsTemplate")
  public JmsTemplate jmsTemplate() {
    return this.jmsConfigurationFactory.initJmsTemplate();
  }
}
