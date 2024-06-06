// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.admin.application.config.messaging;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.MessageListener;
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

/** Configuration class for inbound requests from web service adapter */
@Configuration
public class InboundWebServiceRequestsMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(InboundWebServiceRequestsMessagingConfig.class);

  private JmsConfigurationFactory jmsConfigurationFactory;

  public InboundWebServiceRequestsMessagingConfig(
      final Environment environment, final JmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(
            environment, defaultJmsConfiguration, JmsConfigurationNames.JMS_INCOMING_WS_REQUESTS);
  }

  @Bean(destroyMethod = "stop", name = "domainAdminInboundWebServiceRequestsConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing domainAdminInboundWebServiceRequestsConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "domainAdminInboundWebServiceRequestsMessageListenerContainer")
  public DefaultMessageListenerContainer messageListenerContainer(
      @Qualifier("domainAdminInboundWebServiceRequestsMessageListener")
          final MessageListener messageListener) {
    return this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
  }

  @Bean(name = "domainAdminInboundWebServiceRequestsMessageProcessorMap")
  public MessageProcessorMap messageProcessorMap() {
    return new BaseMessageProcessorMap("domainAdminInboundWebServiceRequestsMessageProcessorMap");
  }
}
