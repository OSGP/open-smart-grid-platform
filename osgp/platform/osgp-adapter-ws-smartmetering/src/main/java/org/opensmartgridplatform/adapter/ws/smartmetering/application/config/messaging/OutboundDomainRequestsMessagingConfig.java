// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.config.messaging;

import jakarta.jms.ConnectionFactory;
import javax.net.ssl.SSLException;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

/** Configuration class for outbound requests to domain adapter. */
@Configuration
public class OutboundDomainRequestsMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(OutboundDomainRequestsMessagingConfig.class);

  private final JmsConfigurationFactory jmsConfigurationFactory;

  public OutboundDomainRequestsMessagingConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(
            environment, defaultJmsConfiguration, JmsConfigurationNames.JMS_SMARTMETERING_REQUESTS);
  }

  @Bean(destroyMethod = "stop", name = "wsSmartMeteringOutboundDomainRequestsConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing wsSmartMeteringOutboundDomainRequestsConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "wsSmartMeteringOutboundDomainRequestsJmsTemplate")
  public JmsTemplate jmsTemplate() {
    LOGGER.info("Initializing wsSmartMeteringOutboundDomainRequestsJmsTemplate bean.");
    return this.jmsConfigurationFactory.initJmsTemplate();
  }
}
