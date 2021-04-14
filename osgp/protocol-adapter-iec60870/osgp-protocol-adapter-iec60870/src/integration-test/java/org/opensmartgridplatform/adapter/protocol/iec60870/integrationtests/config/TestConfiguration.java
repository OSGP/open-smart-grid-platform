/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests.config;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import ma.glasnost.orika.MapperFacade;
import org.openmuc.j60870.Connection;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.mapping.Iec60870Mapper;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.services.DistributionAutomationDeviceResponseService;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.services.Iec60870AsduConverterService;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.services.Iec60870ConnectResponseService;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.services.Iec60870LightMeasurementService;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.services.Iec60870LoggingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.services.Iec60870MeasurementReportingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.services.LightMeasurementRtuDeviceResponseService;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.services.LightSensorDeviceResponseService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories.ResponseMetadataFactory;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.AsduConverterService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.Client;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientAsduHandlerRegistry;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionCache;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ConnectResponseService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.DeviceResponseService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.DeviceResponseServiceRegistry;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.GeneralInterrogationService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LightMeasurementService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LoggingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.MeasurementReportingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.PendingRequestsQueue;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.asduhandlers.InterrogationAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.asduhandlers.ShortFloatWithTime56MeasurementAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.asduhandlers.SinglePointInformationWithTimeTagAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.asduhandlers.SinglePointWithQualityAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.Iec60870Client;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderTimestampService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

  @Bean
  public CorrelationIdProviderService correlationIdProviderService() {
    return new CorrelationIdProviderTimestampService();
  }

  @Bean
  public ClientConnectionCache iec60870ClientConnectionCache() {
    return spy(ClientConnectionCache.class);
  }

  @Bean
  public Iec60870DeviceRepository iec60870DeviceRepository() {
    return mock(Iec60870DeviceRepository.class);
  }

  @Bean
  public ClientConnectionService iec60870ClientConnectionService() {
    return new ClientConnectionService();
  }

  @Bean
  public ConnectResponseService connectResponseService() {
    return new Iec60870ConnectResponseService();
  }

  @Bean
  public Connection connection() {
    return mock(Connection.class);
  }

  @Bean
  public Client iec60870Client() {
    return mock(Iec60870Client.class);
  }

  @Bean
  public MapperFacade iec60870Mapper() {
    return new Iec60870Mapper();
  }

  @Bean
  public ResponseMetadataFactory responseMetadataFactory() {
    return new ResponseMetadataFactory();
  }

  @Bean
  public ClientAsduHandlerRegistry clientAsduHandlerRegistry() {
    return new ClientAsduHandlerRegistry();
  }

  @Bean
  public ClientAsduHandler interrogationCommandAsduHandler() {
    return new InterrogationAsduHandler();
  }

  @Bean
  public ClientAsduHandler shortFloatWithTime56MeasurementAsduHandler() {
    return new ShortFloatWithTime56MeasurementAsduHandler();
  }

  @Bean
  public ClientAsduHandler singlePointWithQualityAsduHandler() {
    return new SinglePointWithQualityAsduHandler();
  }

  @Bean
  public ClientAsduHandler singlePointInformationWithTimeTagAsduHandler() {
    return new SinglePointInformationWithTimeTagAsduHandler();
  }

  @Bean
  public MeasurementReportingService measurementReportMessageSender() {
    return new Iec60870MeasurementReportingService();
  }

  @Bean
  public AsduConverterService asduToMeasurementReportMapper() {
    return new Iec60870AsduConverterService();
  }

  @Bean
  public LightMeasurementService lightMeasurementService() {
    return new Iec60870LightMeasurementService();
  }

  @Bean
  public DeviceResponseServiceRegistry deviceResponseServiceMap() {
    return new DeviceResponseServiceRegistry();
  }

  @Bean
  public DeviceResponseService lightMeasurementDeviceResponseService() {
    return new LightSensorDeviceResponseService();
  }

  @Bean
  public DeviceResponseService lightMeasurementGatewayDeviceResponseService() {
    return new LightMeasurementRtuDeviceResponseService();
  }

  @Bean
  public DeviceResponseService distributionAutomationDeviceResponseService() {
    return new DistributionAutomationDeviceResponseService();
  }

  @Bean
  public GeneralInterrogationService generalInterrogationService() {
    return new GeneralInterrogationService();
  }

  @Bean
  public PendingRequestsQueue pendingRequestsQueue() {
    return new PendingRequestsQueue();
  }
}
