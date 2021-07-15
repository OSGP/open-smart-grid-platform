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
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceRequestMessageListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.processors.ConnectRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.processors.GetHealthStatusRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.processors.GetLightSensorStatusRequestMessageProcessor;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InboundRequestsTestConfiguration {

  @Bean(name = "protocolIec60870InboundOsgpCoreRequestsConnectionFactory")
  public ConnectionFactory iec60870RequestsConnectionFactory() {
    return mock(PooledConnectionFactory.class);
  }

  @Bean(name = "protocolIec60870InboundOsgpCoreRequestsMessageListener")
  public DeviceRequestMessageListener iec60870RequestsMessageListener() {
    return new DeviceRequestMessageListener();
  }

  @Bean(name = "protocolIec60870InboundOsgpCoreRequestsMessageProcessorMap")
  public MessageProcessorMap iec60870RequestMessageProcessorMap() {
    return new BaseMessageProcessorMap(
        "protocolIec60870InboundOsgpCoreRequestsMessageProcessorMap");
  }

  @Bean
  public ConnectRequestMessageProcessor connectRequestMessageProcessor() {
    return new ConnectRequestMessageProcessor();
  }

  @Bean
  public GetHealthStatusRequestMessageProcessor getHealthStatusRequestMessageProcessor() {
    return new GetHealthStatusRequestMessageProcessor();
  }

  @Bean
  public GetLightSensorStatusRequestMessageProcessor getLightSensorStatusRequestMessageProcessor() {
    return new GetLightSensorStatusRequestMessageProcessor();
  }
}
