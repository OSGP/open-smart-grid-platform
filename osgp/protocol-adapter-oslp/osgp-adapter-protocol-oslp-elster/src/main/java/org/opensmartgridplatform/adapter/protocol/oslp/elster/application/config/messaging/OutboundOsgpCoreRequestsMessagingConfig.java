//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.config.messaging;

import javax.jms.ConnectionFactory;
import javax.net.ssl.SSLException;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

/** Configuration class for outbound requests to OSGP Core. */
@Configuration
public class OutboundOsgpCoreRequestsMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(OutboundOsgpCoreRequestsMessagingConfig.class);

  private JmsConfigurationFactory jmsConfigurationFactory;

  public OutboundOsgpCoreRequestsMessagingConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(
            environment, defaultJmsConfiguration, "jms.outgoing.osgp.core.requests");
  }

  @Bean(destroyMethod = "stop", name = "protocolOslpOutboundOsgpCoreRequestsConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing protocolOslpOutboundRequestsConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "protocolOslpOutboundOsgpCoreRequestsJmsTemplate")
  public JmsTemplate jmsTemplate() {
    LOGGER.info("Initializing protocolOslpOutboundOsgpCoreRequestsJmsTemplate bean.");
    return this.jmsConfigurationFactory.initJmsTemplate();
  }
}
