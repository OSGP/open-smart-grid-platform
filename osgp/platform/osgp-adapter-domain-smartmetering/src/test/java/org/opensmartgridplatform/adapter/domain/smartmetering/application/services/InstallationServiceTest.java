/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreRequestMessageSender;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceModelRepository;
import org.opensmartgridplatform.domain.core.repositories.ManufacturerRepository;
import org.opensmartgridplatform.domain.core.repositories.OrganisationRepository;
import org.opensmartgridplatform.domain.core.repositories.ProtocolInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.SmartMeterRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceModel;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AddSmartMeterRequest;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class InstallationServiceTest {

  private static final String DEVICE_IDENTIFICATION = "test-device-identification";
  private static final String PROTOCOL_NAME = "test-protocol-name";
  private static final String PROTOCOL_VERSION = "test-protocol-version";

  @InjectMocks private SmartMeterService smartMeterService;
  @InjectMocks private InstallationService instance;

  @Mock private SmartMeterRepository smartMeteringDeviceRepository;
  @Mock private MapperFactory mapperFactory;
  @Mock private MapperFacade mapperFacade;
  @Mock private ProtocolInfoRepository protocolInfoRepository;
  @Mock private ManufacturerRepository manufacturerRepository;
  @Mock private DeviceModelRepository deviceModelRepository;
  @Mock private OrganisationRepository organisationRepository;
  @Mock private DeviceAuthorizationRepository deviceAuthorizationRepository;
  @Mock private OsgpCoreRequestMessageSender osgpCoreRequestMessageSender;

  @Mock private MessageMetadata deviceMessageMetadata;
  @Mock private AddSmartMeterRequest addSmartMeterRequest;
  @Mock private SmartMeteringDevice smartMeteringDevice;
  @Mock private SmartMeter smartMeter;
  @Mock private ProtocolInfo protocolInfo;
  @Mock private DeviceModel deviceModel;

  @BeforeEach
  public void setUp() {
    when(this.addSmartMeterRequest.getDevice()).thenReturn(this.smartMeteringDevice);
    when(this.deviceMessageMetadata.getDeviceIdentification()).thenReturn(DEVICE_IDENTIFICATION);
    when(this.addSmartMeterRequest.getDeviceModel()).thenReturn(this.deviceModel);
    when(this.mapperFactory.getMapperFacade()).thenReturn(this.mapperFacade);
    when(this.mapperFacade.map(this.smartMeteringDevice, SmartMeter.class))
        .thenReturn(this.smartMeter);
    when(this.smartMeteringDeviceRepository.save(this.smartMeter)).thenReturn(this.smartMeter);
    ReflectionTestUtils.setField(this.instance, "smartMeterService", this.smartMeterService);
  }

  @Test
  public void addMeterProtocolInfoSaved() throws FunctionalException {

    // SETUP
    when(this.smartMeteringDeviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(null);
    when(this.smartMeteringDevice.getProtocolName()).thenReturn(PROTOCOL_NAME);
    when(this.smartMeteringDevice.getProtocolVersion()).thenReturn(PROTOCOL_VERSION);
    when(this.protocolInfoRepository.findByProtocolAndProtocolVersion(
            this.smartMeteringDevice.getProtocolInfoLookupName(), PROTOCOL_VERSION))
        .thenReturn(this.protocolInfo);

    // CALL
    this.instance.addMeter(this.deviceMessageMetadata, this.addSmartMeterRequest);

    // VERIFY
    verify(this.smartMeter).updateProtocol(this.protocolInfo);
    verify(this.smartMeteringDeviceRepository).save(this.smartMeter);
  }

  @Test
  public void addMeterProtocolInfoNotFound() {

    // SETUP
    when(this.smartMeteringDeviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(null);
    when(this.smartMeteringDevice.getProtocolName()).thenReturn(PROTOCOL_NAME);
    when(this.smartMeteringDevice.getProtocolVersion()).thenReturn(PROTOCOL_VERSION);
    when(this.protocolInfoRepository.findByProtocolAndProtocolVersion(
            PROTOCOL_NAME, PROTOCOL_VERSION))
        .thenReturn(null);

    // CALL
    try {
      this.instance.addMeter(this.deviceMessageMetadata, this.addSmartMeterRequest);
    } catch (final FunctionalException e) {
      assertThat(e.getExceptionType())
          .isEqualTo(FunctionalExceptionType.UNKNOWN_PROTOCOL_NAME_OR_VERSION);
      assertThat(e.getComponentType()).isEqualTo(ComponentType.DOMAIN_SMART_METERING);
    }
  }

  @Test
  public void addMeterDeviceExists() {

    // SETUP
    when(this.smartMeteringDeviceRepository.findByDeviceIdentification(DEVICE_IDENTIFICATION))
        .thenReturn(this.smartMeter);

    // CALL
    try {
      this.instance.addMeter(this.deviceMessageMetadata, this.addSmartMeterRequest);
    } catch (final FunctionalException e) {
      assertThat(e.getExceptionType()).isEqualTo(FunctionalExceptionType.EXISTING_DEVICE);
      assertThat(e.getComponentType()).isEqualTo(ComponentType.DOMAIN_SMART_METERING);
    }
  }
}
