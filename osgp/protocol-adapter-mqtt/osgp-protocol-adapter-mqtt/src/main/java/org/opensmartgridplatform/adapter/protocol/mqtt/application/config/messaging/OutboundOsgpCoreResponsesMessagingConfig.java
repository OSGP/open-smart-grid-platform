// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.mqtt.application.config.messaging;

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

/** Configuration class for outbound responses to OSGP Core. */
@Configuration
public class OutboundOsgpCoreResponsesMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(OutboundOsgpCoreResponsesMessagingConfig.class);

  private final JmsConfigurationFactory jmsConfigurationFactory;

  public OutboundOsgpCoreResponsesMessagingConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(environment, defaultJmsConfiguration, "jms.mqtt.responses");
  }

  @Bean(destroyMethod = "stop", name = "protocolMqttOutboundOsgpCoreResponsesConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing protocolMqttOutboundOsgpCoreResponsesConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "protocolMqttOutboundOsgpCoreResponsesJmsTemplate")
  public JmsTemplate jmsTemplate() {
    LOGGER.info("Initializing protocolMqttOutboundOsgpCoreResponsesJmsTemplate bean.");
    return this.jmsConfigurationFactory.initJmsTemplate();
  }
}
