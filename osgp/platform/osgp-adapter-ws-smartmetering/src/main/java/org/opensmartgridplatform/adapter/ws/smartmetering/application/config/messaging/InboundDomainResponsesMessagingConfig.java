// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.config.messaging;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.net.ssl.SSLException;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationNames;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/** Configuration class for inbound responses from domain adapter */
@Configuration
public class InboundDomainResponsesMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(InboundDomainResponsesMessagingConfig.class);

  private final JmsConfigurationFactory jmsConfigurationFactory;

  public InboundDomainResponsesMessagingConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(
            environment,
            defaultJmsConfiguration,
            JmsConfigurationNames.JMS_SMARTMETERING_RESPONSES);
  }

  @Bean(destroyMethod = "stop", name = "wsSmartMeteringInboundDomainResponsesConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing wsSmartMeteringInboundDomainResponsesConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "wsSmartMeteringInboundDomainResponsesMessageListenerContainer")
  public DefaultMessageListenerContainer messageListenerContainer(
      @Qualifier("wsSmartMeteringInboundDomainResponsesMessageListener")
          final MessageListener messageListener) {
    LOGGER.info("Initializing wsSmartMeteringInboundDomainResponseMessageListenerContainer bean.");
    return this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
  }

  @Bean(name = "wsSmartMeteringInboundDomainResponsesMessageProcessorMap")
  public MessageProcessorMap smartMeteringResponseMessageProcessorMap() {
    return new BaseMessageProcessorMap("domainResponseMessageProcessorMap");
  }
}
