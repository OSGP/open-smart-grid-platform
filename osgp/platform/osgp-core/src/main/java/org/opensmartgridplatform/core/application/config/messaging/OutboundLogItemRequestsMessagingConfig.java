// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.application.config.messaging;

import javax.net.ssl.SSLException;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class OutboundLogItemRequestsMessagingConfig {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(OutboundLogItemRequestsMessagingConfig.class);

  private final JmsConfigurationFactory jmsConfigurationFactory;

  public OutboundLogItemRequestsMessagingConfig(
      final Environment environment, final JmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(
            environment,
            defaultJmsConfiguration,
            JmsConfigurationNames.JMS_LOGGING_OUTGOING_LOG_ITEM_REQUESTS);
  }

  @Bean(destroyMethod = "stop", name = "coreLogItemRequestsPooledConnectionFactory")
  public PooledConnectionFactory coreLogItemRequestsPooledConnectionFactory() {
    LOGGER.info("Initializing pooled connection factory for outgoing OSGP logging requests.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean()
  public JmsTemplate coreLogItemRequestsJmsTemplate() {
    return this.jmsConfigurationFactory.initJmsTemplate();
  }
}
