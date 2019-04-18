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

import org.openmuc.j60870.Connection;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping.Iec60870Mapper;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.services.Iec60870MeasurementReportMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.AsduToMeasurementReportMapper;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionCache;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.Iec60870ClientConnectionCache;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.MeasurementReportMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.asduhandlers.ShortFloatWithTime56MeasurementASduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceMessageLoggingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceRequestMessageListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.DeviceResponseMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.LogItemRequestMessageSender;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.processors.GetHealthStatusRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.Iec60870Client;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.services.Iec60870DeviceConnectionService;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.services.Iec60870DeviceService;
import org.opensmartgridplatform.iec60870.Iec60870ASduHandlerRegistry;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfiguration;
import org.opensmartgridplatform.shared.application.config.jms.JmsConfigurationFactory;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderTimestampService;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

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
    public Iec60870DeviceService iec60870DeviceService() {
        return new Iec60870DeviceService();
    }

    @Bean
    public DeviceMessageLoggingService deviceMessageLoggingService() {
        return mock(DeviceMessageLoggingService.class);
    }

    @Bean
    public LogItemRequestMessageSender logItemRequestMessageSender() {
        return mock(LogItemRequestMessageSender.class);
    }

    @Bean
    public JmsTemplate logItemRequestsJmsTemplate() {
        return mock(JmsTemplate.class);
    }

    @Bean
    public CorrelationIdProviderService correlationIdProviderService() {
        return new CorrelationIdProviderTimestampService();
    }

    @Bean
    public ClientConnectionCache iec60870ClientConnectionCache() {
        return spy(Iec60870ClientConnectionCache.class);
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
    public Iec60870Client iec60870Client() {
        return mock(Iec60870Client.class);
    }

    @Bean
    public Iec60870DeviceConnectionService iec60870DeviceConnectionService() {
        return new Iec60870DeviceConnectionService();
    }

    @Bean
    public DeviceRequestMessageListener iec60870RequestsMessageListener() {
        return new DeviceRequestMessageListener();
    }

    @Bean
    public MessageProcessorMap iec60870RequestMessageProcessorMap() {
        return new BaseMessageProcessorMap("iec60870RequestMessageProcessorMap");
    }

    @Bean
    public GetHealthStatusRequestMessageProcessor getHealthStatusRequestMessageProcessor() {
        return new GetHealthStatusRequestMessageProcessor();
    }

    @Bean
    public JmsConfigurationFactory iec60870JmsConfigurationFactory() {
        return mock(JmsConfigurationFactory.class);
    }

    @Bean
    public JmsConfiguration iec60870ResponseJmsConfiguration(final JmsConfigurationFactory jmsConfigurationFactory) {
        return mock(JmsConfiguration.class);
    }

    @Bean
    public JmsTemplate iec60870ResponsesJmsTemplate(final JmsConfiguration iec60870ResponseJmsConfiguration) {
        return mock(JmsTemplate.class);
    }

    @Bean
    public DeviceResponseMessageSender iec60870ResponseMessageSender() {
        return mock(DeviceResponseMessageSender.class);
    }

    @Bean
    public Iec60870ASduHandlerRegistry iec60870ASduHandlerRegistry() {
        return new Iec60870ASduHandlerRegistry();
    }

    @Bean
    public ShortFloatWithTime56MeasurementASduHandler shortFloatWithTime56MeasurementASduHandler() {
        return new ShortFloatWithTime56MeasurementASduHandler();
    }

    @Bean
    public AsduToMeasurementReportMapper asduToMeasurementReportMapper() {
        return new Iec60870Mapper();
    }

    @Bean
    public MeasurementReportMessageSender measurementReportMessageSender() {
        return new Iec60870MeasurementReportMessageSender();
    }

}
