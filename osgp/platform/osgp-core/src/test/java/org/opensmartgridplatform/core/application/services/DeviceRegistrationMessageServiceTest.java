/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.application.services;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;

@ExtendWith(MockitoExtension.class)
public class DeviceRegistrationMessageServiceTest {

  @Mock private DeviceRepository deviceRepository;

  @Mock private DeviceNetworkAddressCleanupService deviceNetworkAddressCleanupService;

  @InjectMocks private DeviceRegistrationMessageService deviceRegistrationMessageService;

  @Test
  public void duplicateAddressesAreClearedWhenUpdatingRegistrationData() throws Exception {
    final String deviceIdentification = "test-device";
    final String ipAddress = "127.0.0.1";
    final String deviceType = "DeviceType";
    final boolean hasSchedule = false;
    when(this.deviceRepository.findByDeviceIdentification(deviceIdentification))
        .thenReturn(new Device(deviceIdentification));

    this.deviceRegistrationMessageService.updateRegistrationData(
        deviceIdentification, ipAddress, deviceType, hasSchedule);

    verify(this.deviceNetworkAddressCleanupService)
        .clearDuplicateAddresses(deviceIdentification, ipAddress);
  }
}
