// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.microgrids.application.config.messaging;

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

/** Configuration class for inbound requests from Web Service Adapter. */
@Configuration
public class InboundWebServiceRequestsMessagingConfig {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(InboundWebServiceRequestsMessagingConfig.class);

  private static final String PROPERTY_PREFIX = "jms.microgrids.inbound.ws.requests";

  private final JmsConfigurationFactory jmsConfigurationFactory;

  public InboundWebServiceRequestsMessagingConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(environment, defaultJmsConfiguration, PROPERTY_PREFIX);
  }

  @Bean(destroyMethod = "stop", name = "domainMicrogridsInboundWebServiceRequestsConnectionFactory")
  public ConnectionFactory connectionFactory() {
    LOGGER.info("Initializing domainMicrogridsInboundWebServiceRequestsConnectionFactory bean.");
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean(name = "domainMicrogridsInboundWebServiceRequestsMessageListenerContainer")
  public DefaultMessageListenerContainer messageListenerContainer(
      @Qualifier("domainMicrogridsInboundWebServiceRequestsMessageListener")
          final MessageListener messageListener) {
    LOGGER.info(
        "Initializing domainMicrogridsInboundWebServiceRequestsMessageListenerContainer bean.");
    return this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
  }

  @Bean(name = "domainMicrogridsInboundWebServiceRequestsMessageProcessorMap")
  public MessageProcessorMap messageProcessorMap() {
    LOGGER.info("Initializing domainMicrogridsInboundWebServiceRequestsMessageProcessorMap bean.");
    return new BaseMessageProcessorMap(
        "domainMicrogridsInboundWebServiceRequestsMessageProcessorMap");
  }
}
