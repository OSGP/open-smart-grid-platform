/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
