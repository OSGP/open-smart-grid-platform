// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.logging.application.config.messaging;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.net.ssl.SSLException;
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

/** Configuration class for inbound protocol log item requests from protocol adapters. */
@Configuration
public class InboundProtocolLogItemRequestsMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(InboundProtocolLogItemRequestsMessagingConfig.class);

  private JmsConfigurationFactory jmsConfigurationFactory;

  public InboundProtocolLogItemRequestsMessagingConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(
            environment,
            defaultJmsConfiguration,
            JmsConfigurationNames.JMS_PROTOCOL_LOG_ITEM_REQUESTS);
  }

  @Bean(destroyMethod = "stop", name = "OsgpLoggingInboundProtocolLogItemRequestsConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing OsgpLoggingInboundProtocolLogItemRequestsConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "OsgpLoggingInboundProtocolLogItemRequestsMessageListenerContainer")
  public DefaultMessageListenerContainer messageListenerContainer(
      @Qualifier(value = "OsgpLoggingInboundProtocolLogItemRequestsMessageListener")
          final MessageListener messageListener) {
    LOGGER.info(
        "Initializing OsgpLoggingInboundProtocolLogItemRequestsMessageListenerContainer bean.");
    return this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
  }
}
