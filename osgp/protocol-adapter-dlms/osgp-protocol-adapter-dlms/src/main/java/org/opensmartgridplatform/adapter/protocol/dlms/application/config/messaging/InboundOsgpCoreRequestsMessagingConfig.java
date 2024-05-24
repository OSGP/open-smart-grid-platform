// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.config.messaging;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageListener;
import javax.net.ssl.SSLException;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/** Configuration class for inbound requests from OSGP Core. */
@Slf4j
@Configuration
public class InboundOsgpCoreRequestsMessagingConfig {

  private final JmsConfigurationFactory jmsConfigurationFactory;

  public InboundOsgpCoreRequestsMessagingConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {

    log.info(
        "Init InboundOsgpCoreRequestsMessagingConfig. queue = {}",
        environment.getProperty("jms.dlms.requests.queue"));

    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(environment, defaultJmsConfiguration, "jms.dlms.requests");
  }

  @Bean(destroyMethod = "stop", name = "protocolDlmsInboundOsgpCoreRequestsConnectionFactory")
  public ConnectionFactory connectionFactory() {
    log.info("Initializing protocolDlmsInboundOsgpCoreRequestsConnectionFactory bean.");

    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "protocolDlmsInboundOsgpCoreRequestsMessageListenerContainer")
  public DefaultMessageListenerContainer messageListenerContainer(
      @Qualifier("protocolDlmsInboundOsgpCoreRequestsMessageListener")
          final MessageListener messageListener) {

    final DefaultMessageListenerContainer container =
        this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);

    log.info(
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
    log.info("Initializing protocolDlmsDeviceRequestMessageSenderJmsTemplate bean.");
    return this.jmsConfigurationFactory.initJmsTemplate();
  }
}
