// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.config.messaging;

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
import org.springframework.jms.core.JmsTemplate;
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

    LOGGER.info(
        "Init InboundOsgpCoreRequestsMessagingConfig. queue = {}",
        environment.getProperty("jms.dlms.requests.queue"));

    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(environment, defaultJmsConfiguration, "jms.dlms.requests");
  }

  @Bean(destroyMethod = "stop", name = "protocolDlmsInboundOsgpCoreRequestsConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing protocolDlmsInboundOsgpCoreRequestsConnectionFactory bean.");

    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "protocolDlmsInboundOsgpCoreRequestsMessageListenerContainer")
  public DefaultMessageListenerContainer messageListenerContainer(
      @Qualifier("protocolDlmsInboundOsgpCoreRequestsMessageListener")
          final MessageListener messageListener) {

    final DefaultMessageListenerContainer container =
        this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);

    LOGGER.info(
        "Initializing protocolDlmsInboundOsgpCoreRequestsMessageListenerContainer bean at Destination {}",
        container.getDestination());
    return container;
  }

  @Bean("protocolDlmsInboundOsgpCoreRequestsMessageProcessorMap")
  public MessageProcessorMap messageProcessorMap() {
    return new BaseMessageProcessorMap("InboundOsgpCoreRequestsMessageProcessorMap");
  }

  @Bean(name = "protocolDlmsDeviceRequestMessageSenderJmsTemplate")
  public JmsTemplate jmsTemplate() {
    LOGGER.info("Initializing protocolDlmsDeviceRequestMessageSenderJmsTemplate bean.");
    return this.jmsConfigurationFactory.initJmsTemplate();
  }
}
