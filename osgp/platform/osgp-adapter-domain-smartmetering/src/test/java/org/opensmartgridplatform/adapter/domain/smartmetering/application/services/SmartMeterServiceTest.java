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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
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
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;

@ExtendWith(MockitoExtension.class)
class SmartMeterServiceTest {

  @Mock private SmartMeterRepository smartMeterRepository;

  @Mock private ProtocolInfoRepository protocolInfoRepository;

  @Mock private ManufacturerRepository manufacturerRepository;

  @Mock private DeviceModelRepository deviceModelRepository;

  @Mock private OrganisationRepository organisationRepository;

  @Mock private DeviceAuthorizationRepository deviceAuthorizationRepository;

  @Captor private ArgumentCaptor<SmartMeter> saveCaptor;

  @InjectMocks private SmartMeterService smartMeterService;

  @Test
  void testNonExistingSmartMeter() {

    final String deviceIdentification = "device-1";

    when(this.smartMeterRepository.findByDeviceIdentification(deviceIdentification))
        .thenReturn(new SmartMeter());

    final FunctionalException exception =
        Assertions.assertThrows(
            FunctionalException.class,
            () -> {
              this.smartMeterService.validateSmartMeterDoesNotExist(deviceIdentification);
            });
    assertThat(exception.getExceptionType()).isEqualTo(FunctionalExceptionType.EXISTING_DEVICE);
  }

  @Test
  void testStoreMeterWithUnknownProtocolInfo() {

    final String organisationIdentification = "org-1";
    final SmartMeteringDevice smartMeteringDevice = new SmartMeteringDevice();
    final DeviceModel deviceModel = new DeviceModel();
    final AddSmartMeterRequest addSmartMeterRequest =
        new AddSmartMeterRequest(smartMeteringDevice, deviceModel);
    final SmartMeter smartMeter = new SmartMeter();

    when(this.protocolInfoRepository.findByProtocolAndProtocolVersionAndProtocolVariant(
            any(), any(), any()))
        .thenReturn(null);

    final FunctionalException exception =
        Assertions.assertThrows(
            FunctionalException.class,
            () -> {
              this.smartMeterService.storeMeter(
                  organisationIdentification, addSmartMeterRequest, smartMeter);
            });
    assertThat(exception.getExceptionType())
        .isEqualTo(FunctionalExceptionType.UNKNOWN_PROTOCOL_NAME_OR_VERSION_OR_VARIANT);
  }

  @Test
  void testStoreMeter() throws FunctionalException {

    final String organisationIdentification = "org-1";
    final SmartMeteringDevice smartMeteringDevice = new SmartMeteringDevice();
    final DeviceModel deviceModel = new DeviceModel();
    final AddSmartMeterRequest addSmartMeterRequest =
        new AddSmartMeterRequest(smartMeteringDevice, deviceModel);
    final SmartMeter smartMeter = new SmartMeter();

    final Manufacturer manufacturer = new Manufacturer();

    final ProtocolInfo protocolInfo = mock(ProtocolInfo.class);

    when(this.protocolInfoRepository.findByProtocolAndProtocolVersionAndProtocolVariant(
            any(), any(), any()))
        .thenReturn(protocolInfo);
    when(this.manufacturerRepository.findByCode(any())).thenReturn(manufacturer);
    when(this.deviceModelRepository.findByManufacturerAndModelCode(any(), any()))
        .thenReturn(new org.opensmartgridplatform.domain.core.entities.DeviceModel());
    when(this.smartMeterRepository.save(any())).thenReturn(smartMeter);

    this.smartMeterService.storeMeter(organisationIdentification, addSmartMeterRequest, smartMeter);

    verify(this.protocolInfoRepository)
        .findByProtocolAndProtocolVersionAndProtocolVariant(any(), any(), any());
    verify(this.manufacturerRepository).findByCode(any());
    verify(this.deviceModelRepository).findByManufacturerAndModelCode(any(), any());
    verify(this.deviceAuthorizationRepository).save(any());
    verify(this.organisationRepository)
        .findByOrganisationIdentification(organisationIdentification);
    verify(this.smartMeterRepository).save(any());
  }
}
