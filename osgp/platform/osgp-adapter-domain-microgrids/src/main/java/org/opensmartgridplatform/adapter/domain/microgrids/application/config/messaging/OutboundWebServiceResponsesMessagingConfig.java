// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.microgrids.application.config.messaging;

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

/** Configuration class for outbound responses to Web Service Adapter. */
@Configuration
public class OutboundWebServiceResponsesMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(OutboundWebServiceResponsesMessagingConfig.class);

  private static final String PROPERTY_PREFIX = "jms.microgrids.outbound.ws.responses";

  private final JmsConfigurationFactory jmsConfigurationFactory;

  public OutboundWebServiceResponsesMessagingConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(environment, defaultJmsConfiguration, PROPERTY_PREFIX);
  }

  @Bean(
      destroyMethod = "stop",
      name = "domainMicrogridsOutboundWebServiceResponsesConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing domainMicrogridsOutboundWebServiceResponsesConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "domainMicrogridsOutboundWebServiceResponsesJmsTemplate")
  public JmsTemplate jmsTemplate() {
    LOGGER.info("Initializing domainMicrogridsOutboundWebServiceResponsesJmsTemplate bean.");

    return this.jmsConfigurationFactory.initJmsTemplate();
  }
}
