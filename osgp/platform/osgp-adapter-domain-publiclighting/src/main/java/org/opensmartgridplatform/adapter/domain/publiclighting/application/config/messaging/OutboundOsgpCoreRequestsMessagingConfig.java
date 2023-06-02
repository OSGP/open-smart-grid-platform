//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.publiclighting.application.config.messaging;

import javax.jms.ConnectionFactory;
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
            environment,
            defaultJmsConfiguration,
            JmsConfigurationNames.JMS_OUTGOING_OSGP_CORE_REQUESTS);
  }

  @Bean(
      destroyMethod = "stop",
      name = "domainPublicLightingOutboundOsgpCoreRequestsConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing domainPublicLightingOutboundOsgpCoreRequestsConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "domainPublicLightingOutboundOsgpCoreRequestsJmsTemplate")
  public JmsTemplate jmsTemplate() {
    LOGGER.info("Initializing domainPublicLightingOutboundOsgpCoreRequestsJmsTemplate bean.");
    return this.jmsConfigurationFactory.initJmsTemplate();
  }
}
