// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.application.config.messaging;

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

/** An application context Java configuration class. */
@Configuration
public class InboundWebServiceRequestsMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(InboundWebServiceRequestsMessagingConfig.class);

  private final JmsConfigurationFactory jmsConfigurationFactory;

  public InboundWebServiceRequestsMessagingConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(
            environment, defaultJmsConfiguration, "jms.inbound.ws.requests");
  }

  @Bean(
      destroyMethod = "stop",
      name = "domainDistributionAutomationInboundWebServiceConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info(
        "Initializing domainDistributionAutomationInboundWebServiceConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "domainDistributionAutomationInboundWebServiceRequestsMessageListenerContainer")
  public DefaultMessageListenerContainer messageListenerContainer(
      @Qualifier("domainDistributionAutomationInboundWebServiceRequestsMessageListener")
          final MessageListener messageListener) {
    LOGGER.info(
        "Initializing domainDistributionAutomationInboundWebServiceRequestsMessageListenerContainer bean.");
    return this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
  }

  @Bean("domainDistributionAutomationInboundWebServiceRequestsMessageProcessorMap")
  public MessageProcessorMap messageProcessorMap() {
    LOGGER.info(
        "Initializing domainDistributionAutomationInboundWebServiceRequestsMessageProcessorMap bean.");
    return new BaseMessageProcessorMap(
        "domainDistributionAutomationInboundWebServiceRequestsMessageProcessorMap");
  }
}
