// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.config;

import static org.mockito.Mockito.mock;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceResponseMessageSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class OutboundResponsesTestConfiguration {

  @Bean(name = "protocolIec60870OutboundOsgpCoreResponsesConnectionFactory")
  public ConnectionFactory iec60870ResponsesConnectionFactory() {
    return mock(PooledConnectionFactory.class);
  }

  @Bean(name = "protocolIec60870OutboundOsgpCoreResponsesMessageSender")
  public DeviceResponseMessageSender iec60870ResponseMessageSender() {
    return mock(DeviceResponseMessageSender.class);
  }

  @Bean(name = "protocolIec60870OutboundOsgpCoreResponsesJmsTemplate")
  public JmsTemplate iec60870ResponsesJmsTemplate() {
    return mock(JmsTemplate.class);
  }
}
