// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.admin.application.config.messaging;

import jakarta.jms.ConnectionFactory;
import javax.net.ssl.SSLException;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

/** Configuration class for outbound responses to OSGP Core */
@Configuration
public class OutboundOsgpCoreResponsesMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(OutboundOsgpCoreResponsesMessagingConfig.class);

  private final JmsConfigurationFactory jmsConfigurationFactory;

  public OutboundOsgpCoreResponsesMessagingConfig(
      final Environment environment, final JmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(
            environment,
            defaultJmsConfiguration,
            JmsConfigurationNames.JMS_OUTGOING_OSGP_CORE_RESPONSES);
  }

  @Bean(destroyMethod = "stop", name = "domainAdminOutboundOsgpCoreResponsesConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing domainAdminOutboundOsgpCoreResponsesConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "domainAdminOutboundOsgpCoreResponsesJmsTemplate")
  public JmsTemplate jmsTemplate() {
    LOGGER.info("Initializing domainAdminOutboundOsgpCoreResponsesJmsTemplate bean.");
    return this.jmsConfigurationFactory.initJmsTemplate();
  }
}
