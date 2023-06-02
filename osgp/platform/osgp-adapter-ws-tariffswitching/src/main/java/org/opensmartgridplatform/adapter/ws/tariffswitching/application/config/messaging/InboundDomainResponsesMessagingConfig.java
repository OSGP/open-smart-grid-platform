//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.tariffswitching.application.config.messaging;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

/** Configuration class for inbound responses from domain adapter. */
@Configuration
public class InboundDomainResponsesMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(InboundDomainResponsesMessagingConfig.class);

  @Value("${jms.tariffswitching.responses.receive.timeout:100}")
  private long receiveTimeout;

  private JmsConfigurationFactory jmsConfigurationFactory;

  public InboundDomainResponsesMessagingConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(
            environment,
            defaultJmsConfiguration,
            JmsConfigurationNames.JMS_TARIFFSWITCHING_RESPONSES);
  }

  @Bean(destroyMethod = "stop", name = "wsTariffSwitchingInboundDomainResponsesConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing wsTariffSwitchingInboundDomainResponsesConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "wsTariffSwitchingInboundDomainResponsesJmsTemplate")
  public JmsTemplate jmsTemplate() {
    LOGGER.info(
        "Initializing wsTariffSwitchingInboundDomainResponsesJmsTemplate bean with receive timeout {}.",
        this.receiveTimeout);
    final JmsTemplate jmsTemplate = this.jmsConfigurationFactory.initJmsTemplate();
    jmsTemplate.setReceiveTimeout(this.receiveTimeout);
    return jmsTemplate;
  }

  @Bean(name = "wsTariffSwitchingResponsesMessageListenerContainer")
  public DefaultMessageListenerContainer tariffSwitchingResponseMessageListenerContainer(
      @Qualifier("wsTariffSwitchingInboundDomainResponsesMessageListener")
          final MessageListener messageListener) {
    final DefaultMessageListenerContainer container =
        this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
    // Only consume messages defined by the message selector string.
    // All other messages will be retrieved using
    // {@link TariffSwitchingResponseMessageFinder}
    container.setMessageSelector("JMSType = 'SET_TARIFF_SCHEDULE'");
    return container;
  }

  @Bean
  @Qualifier("wsTariffSwitchingInboundDomainResponsesMessageProcessorMap")
  public MessageProcessorMap messageProcessorMap() {
    return new BaseMessageProcessorMap("inboundDomainResponsesMessageProcessorMap");
  }
}
