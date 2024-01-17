// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.application.config.messaging;

import jakarta.jms.ConnectionFactory;
import javax.net.ssl.SSLException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.DeviceRequestMessageListener;
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

  private final JmsConfigurationFactory jmsConfigurationFactory;

  public InboundOsgpCoreRequestsMessagingConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(environment, defaultJmsConfiguration, "jms.iec61850.requests");
  }

  @Bean(destroyMethod = "stop", name = "protocolIec61850InboundOsgpCoreRequestsConnectionFactory")
  public ConnectionFactory connectionFactory() {
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "protocolIec61850InboundOsgpCoreRequestsMessageListenerContainer")
  public DefaultMessageListenerContainer messageListenerContainer(
      @Qualifier("protocolIec61850InboundOsgpCoreRequestsMessageListener")
          final DeviceRequestMessageListener messageListener) {
    final DefaultMessageListenerContainer messageListenerContainer =
        this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
    // Setting ErrorHandler to prevent logging at WARN level
    // when JMSException is thrown: Execution of JMS message
    // listener failed, and no ErrorHandler has been set.
    messageListenerContainer.setErrorHandler(
        t ->
            LOGGER.debug("iec61850RequestsMessageListenerContainer.ErrorHandler.handleError()", t));
    return messageListenerContainer;
  }

  @Bean
  public int maxRedeliveriesForIec61850Requests() {
    return this.jmsConfigurationFactory.getRedeliveryPolicy().getMaximumRedeliveries();
  }

  @Bean
  @Qualifier("iec61850DeviceRequestMessageProcessorMap")
  public MessageProcessorMap microgridsResponseMessageProcessorMap() {
    return new BaseMessageProcessorMap("DeviceRequestMessageProcessorMap");
  }
}
