/*
 * Copyright 2021 Alliander N.V.
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.config.messaging;

import javax.jms.ConnectionFactory;
import javax.net.ssl.SSLException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.SimpleMessageListener;
import org.opensmartgridplatform.shared.application.config.messaging.DefaultJmsConfiguration;
import org.opensmartgridplatform.shared.application.config.messaging.JmsConfigurationFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

@Configuration
public class PriorityMessageConfig {

  private final JmsConfigurationFactory jmsConfigurationFactory;

  public PriorityMessageConfig(
      final Environment environment, final DefaultJmsConfiguration defaultJmsConfiguration)
      throws SSLException {
    this.jmsConfigurationFactory =
        new JmsConfigurationFactory(environment, defaultJmsConfiguration, "jms.priority.test");
  }

  @Bean(destroyMethod = "stop", name = "OutboundPriorityConnectionFactory")
  public ConnectionFactory connectionFactory() {
    return this.jmsConfigurationFactory.getPooledConnectionFactory();
  }

  @Bean
  public JmsTemplate priorityJmsTemplate() {
    return this.jmsConfigurationFactory.initJmsTemplate();
  }

  @Bean(name = "priorityMessageListenerContainer")
  public DefaultMessageListenerContainer priorityMessageListenerContainer(
      final SimpleMessageListener messageListener) {
    return this.jmsConfigurationFactory.initMessageListenerContainer(messageListener);
  }
}
