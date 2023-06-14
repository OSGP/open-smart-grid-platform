// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.config;

import static org.mockito.Mockito.mock;

import javax.jms.ConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.LogItemFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.LogItemRequestMessageSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class LogItemTestConfiguration {

  @Bean(name = "protocolIec60870OutboundLogItemRequestsConnectionFactory")
  public ConnectionFactory logItemRequestsConnectionFactory() {
    return mock(PooledConnectionFactory.class);
  }

  @Bean(name = "protocolIec60870OutboundLogItemRequestsMessageSender")
  public LogItemRequestMessageSender logItemRequestMessageSender() {
    return mock(LogItemRequestMessageSender.class);
  }

  @Bean(name = "protocolIec60870OutboundLogItemRequestsJmsTemplate")
  public JmsTemplate logItemRequestsJmsTemplate() {
    return mock(JmsTemplate.class);
  }

  @Bean
  public LogItemFactory logItemFactory() {
    return new LogItemFactory();
  }
}
