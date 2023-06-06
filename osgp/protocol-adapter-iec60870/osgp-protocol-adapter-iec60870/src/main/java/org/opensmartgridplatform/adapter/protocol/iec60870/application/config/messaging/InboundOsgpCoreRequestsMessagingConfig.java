// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.application.config.messaging;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.net.ssl.SSLException;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/** Configuration class for inbound requests from OSGP Core. */
@Configuration
public class InboundOsgpCoreRequestsMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(InboundOsgpCoreRequestsMessagingConfig.class);

  private JmsConfigurationFactory jmsConfigurationFactory;

  public InboundOsgpCoreRequestsMessagingConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(environment, defaultJmsConfiguration, "jms.iec60870.requests");
  }

  @Bean(destroyMethod = "stop", name = "protocolIec60870InboundOsgpCoreRequestsConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing protocolIec60870InboundOsgpCoreRequestsConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "protocolIec60870InboundOsgpCoreRequestsMessageListenerContainer")
  public DefaultMessageListenerContainer messageListenerContainer(
      @Qualifier("protocolIec60870InboundOsgpCoreRequestsMessageListener")
          final MessageListener messageListener) {
    final DefaultMessageListenerContainer messageListenerContainer =
        this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);

    // Setting ErrorHandler to prevent logging at WARN level
    // when JMSException is thrown: Execution of JMS message
    // listener failed, and no ErrorHandler has been set.
    messageListenerContainer.setErrorHandler(
        t ->
            LOGGER.debug("iec60870RequestsMessageListenerContainer.ErrorHandler.handleError()", t));
    return messageListenerContainer;
  }

  @Bean(name = "protocolIec60870InboundOsgpCoreRequestsMessageProcessorMap")
  public MessageProcessorMap messageProcessorMap() {
    return new BaseMessageProcessorMap(
        "protocolIec60870InboundOsgpCoreRequestsMessageProcessorMap");
  }

  @Bean
  public int maxRedeliveriesForIec60870Requests() {
    return this.jmsConfigurationFactory.getRedeliveryPolicy().getMaximumRedeliveries();
  }
}
