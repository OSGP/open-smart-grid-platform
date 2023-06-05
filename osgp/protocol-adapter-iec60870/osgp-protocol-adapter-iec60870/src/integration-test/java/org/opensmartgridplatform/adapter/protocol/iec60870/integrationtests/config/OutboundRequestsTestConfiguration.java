// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.config;

import static org.mockito.Mockito.mock;

import javax.jms.ConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.OsgpRequestMessageSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

@Configuration
public class OutboundRequestsTestConfiguration {

  @Bean(name = "protocolIec60870OutboundOsgpCoreRequestsConnectionFactory")
  public ConnectionFactory iec60870RequestsConnectionFactory() {
    return mock(PooledConnectionFactory.class);
  }

  @Bean(name = "protocolIec60870OutboundOsgpCoreRequestsMessageSender")
  public OsgpRequestMessageSender osgpRequestMessageSender() {
    return mock(OsgpRequestMessageSender.class);
  }

  @Bean(name = "protocolIec60870OutboundOsgpCoreRequestsJmsTemplate")
  public JmsTemplate iec60870RequestsJmsTemplate() {
    return mock(JmsTemplate.class);
  }
}
