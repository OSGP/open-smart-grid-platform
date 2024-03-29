// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.da.application.config.messaging;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageListener;
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

/** Configuration class for inbound requests from Domain Adapter. */
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
            environment, defaultJmsConfiguration, "jms.inbound.domain.responses");
  }

  @Bean(
      destroyMethod = "stop",
      name = "wsDistributionAutomationInboundDomainResponsesConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info(
        "Initializing wsDistributionAutomationInboundDomainResponsesConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "wsDistributionAutomationInboundDomainResponsesMessageListenerContainer")
  public DefaultMessageListenerContainer messageListenerContainer(
      @Qualifier("wsDistributionAutomationInboundDomainResponsesMessageListener")
          final MessageListener messageListener) {
    LOGGER.info(
        "Initializing wsDistributionAutomationInboundDomainResponsesMessageListenerContainer bean.");
    return this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
  }

  @Bean("wsDistributionAutomationInboundDomainResponsesMessageProcessorMap")
  public MessageProcessorMap messageProcessorMap() {
    LOGGER.info(
        "Initializing wsDistributionAutomationInboundDomainResponsesMessageProcessorMap bean.");
    return new BaseMessageProcessorMap(
        "wsDistributionAutomationInboundDomainResponsesMessageProcessorMap");
  }
}
