// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.application.config.messaging;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.net.ssl.SSLException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.OsgpResponseMessageListener;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/** Configuration class for inbound responses from OSGP Core. */
@Configuration
public class InboundOsgpCoreResponsesMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(InboundOsgpCoreResponsesMessagingConfig.class);

  private JmsConfigurationFactory jmsConfigurationFactory;

  public InboundOsgpCoreResponsesMessagingConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(environment, defaultJmsConfiguration, "jms.osgp.responses");
  }

  @Bean
  public OsgpResponseMessageListener osgpResponseMessageListener() {
    return new OsgpResponseMessageListener();
  }

  @Bean(destroyMethod = "stop", name = "protocolIec61850InboundOsgpCoreResponsesConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing protocoIec61850InboundOsgpCoreResponsesConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "protocolIec61850InboundOsgpCoreResponsesMessageListenerContainer")
  public DefaultMessageListenerContainer messageListenerContainer(
      @Qualifier("protocolIec61850InboundOsgpCoreResponsesMessageListener")
          final MessageListener messageListener) {
    LOGGER.info(
        "Initializing protocolIec61850InboundOsgpCoreResponsesMessageListenerContainer bean.");
    return this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
  }
}
