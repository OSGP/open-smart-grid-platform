/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import javax.jms.ConnectionFactory;

import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.openmuc.j60870.Connection;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping.Iec60870Mapper;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.services.Iec60870AsduConverterService;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.services.Iec60870LoggingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.services.Iec60870MeasurementReportingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.LogItemFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.ResponseMetadataFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.AsduConverterService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.Client;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientAsduHandlerRegistryImpl;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionCache;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionCacheImpl;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionServiceImpl;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LoggingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.MeasurementReportingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.asduhandlers.InterrogationAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.asduhandlers.ShortFloatWithTime56MeasurementAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceRequestMessageListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.LogItemRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.processors.GetHealthStatusRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.Iec60870Client;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderTimestampService;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import ma.glasnost.orika.MapperFacade;

@Configuration
public class TestConfiguration {

    @Bean
    public int connectionTimeout() {
        return 10;
    }

    @Bean
    public int responseTimeout() {
        return 5;
    }

    @Bean
    public int maxRedeliveriesForIec60870Requests() {
        return 0;
    }

    @Bean
    public boolean isCloseOnConnectionFailure() {
        return false;
    }

    @Bean
    public LoggingService deviceMessageLoggingService() {
        return new Iec60870LoggingService();
    }

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
    public CorrelationIdProviderService correlationIdProviderService() {
        return new CorrelationIdProviderTimestampService();
    }

    @Bean
    public ClientConnectionCache iec60870ClientConnectionCache() {
        return spy(ClientConnectionCacheImpl.class);
    }

    @Bean
    public ClientConnectionService iec60870ClientConnectionService() {
        return new ClientConnectionServiceImpl();
    }

    @Bean
    public Connection connection() {
        return mock(Connection.class);
    }

    @Bean
    public Iec60870DeviceRepository iec60870DeviceRepository() {
        return mock(Iec60870DeviceRepository.class);
    }

    @Bean
    public Client iec60870Client() {
        return mock(Iec60870Client.class);
    }

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
        return new BaseMessageProcessorMap("protocolIec60870InboundOsgpCoreRequestsMessageProcessorMap");
    }

    @Bean
    public GetHealthStatusRequestMessageProcessor getHealthStatusRequestMessageProcessor() {
        return new GetHealthStatusRequestMessageProcessor();
    }

    // @Bean
    // public JmsConfigurationFactory iec60870JmsConfigurationFactory() {
    // return mock(JmsConfigurationFactory.class);
    // }

    // @Bean
    // public JmsConfiguration iec60870ResponseJmsConfiguration(final
    // JmsConfigurationFactory jmsConfigurationFactory) {
    // return mock(JmsConfiguration.class);
    // }
    //
    // @Bean
    // public JmsTemplate iec60870ResponsesJmsTemplate(final JmsConfiguration
    // iec60870ResponseJmsConfiguration) {
    // return mock(JmsTemplate.class);
    // }

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

    @Bean
    public ClientAsduHandlerRegistryImpl iec60870ClientASduHandlerRegistry() {
        return new ClientAsduHandlerRegistryImpl();
    }

    @Bean
    public ClientAsduHandler shortFloatWithTime56MeasurementASduHandler() {
        return new ShortFloatWithTime56MeasurementAsduHandler();
    }

    @Bean
    public ClientAsduHandler interrogationCommandASduHandler() {
        return new InterrogationAsduHandler();
    }

    @Bean
    public MapperFacade iec60870Mapper() {
        return new Iec60870Mapper();
    }

    @Bean
    public AsduConverterService asduToMeasurementReportMapper() {
        return new Iec60870AsduConverterService();
    }

    @Bean
    public MeasurementReportingService measurementReportMessageSender() {
        return new Iec60870MeasurementReportingService();
    }

    @Bean
    public ResponseMetadataFactory responseMetadataFactory() {
        return new ResponseMetadataFactory();
    }

    @Bean
    public LogItemFactory logItemFactory() {
        return new LogItemFactory();
    }

}
