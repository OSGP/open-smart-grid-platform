// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.admin.application.config.messaging;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.net.ssl.SSLException;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfiguration;
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

/** Configuration class for inbound responses from OSGP Core */
@Configuration
public class InboundOsgpCoreResponsesMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(InboundOsgpCoreResponsesMessagingConfig.class);

  private JmsConfigurationFactory jmsConfigurationFactory;

  public InboundOsgpCoreResponsesMessagingConfig(
      final Environment environment, final JmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(
            environment,
            defaultJmsConfiguration,
            JmsConfigurationNames.JMS_INCOMING_OSGP_CORE_RESPONSES);
  }

  @Bean(destroyMethod = "stop", name = "domainAdminInboundOsgpCoreResponsesConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing domainAdminInboundOsgpCoreResponsesConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "domainAdminInboundOsgpCoreResponsesMessageListenerContainer")
  public DefaultMessageListenerContainer messageListenerContainer(
      @Qualifier("domainAdminInboundOsgpCoreResponsesMessageListener")
          final MessageListener messageListener) {
    return this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
  }

  @Bean("domainAdminInboundOsgpCoreResponsesMessageProcessorMap")
  public MessageProcessorMap messageProcessorMap() {
    return new BaseMessageProcessorMap("domainAdminInboundOsgpCoreResponsesMessageProcessorMap");
  }
}
