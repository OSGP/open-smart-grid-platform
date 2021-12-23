/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.DeviceKeyProcessAlreadyRunningException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DeviceKeyProcessingService {

  @Value("#{T(java.time.Duration).parse('${device.key.processing.timeout:PT5M}')}")
  private Duration deviceKeyProcessingTimeout;

  private final DlmsDeviceRepository dlmsDeviceRepository;

  @Autowired
  public DeviceKeyProcessingService(final DlmsDeviceRepository dlmsDeviceRepository) {
    this.dlmsDeviceRepository = dlmsDeviceRepository;
  }

  public void startProcessing(final String deviceIdentification)
      throws DeviceKeyProcessAlreadyRunningException {

    final DlmsDevice dlmsDevice =
        this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
    final Date keyProcessingStartTime = dlmsDevice.getKeyProcessingStartTime();
    final Instant timeoutTime = Instant.now().minus(this.deviceKeyProcessingTimeout);
    final boolean processIsAlreadyRunning =
        keyProcessingStartTime != null && timeoutTime.isBefore(keyProcessingStartTime.toInstant());
    if (processIsAlreadyRunning) {
      throw new DeviceKeyProcessAlreadyRunningException();
    } else {
      dlmsDevice.setKeyProcessingStartTime(new Date());
      this.dlmsDeviceRepository.saveAndFlush(dlmsDevice);
    }
  }

  public void stopProcessing(final String deviceIdentification) {
    final DlmsDevice dlmsDevice =
        this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
    dlmsDevice.setKeyProcessingStartTime(null);
    this.dlmsDeviceRepository.saveAndFlush(dlmsDevice);
  }

  public Duration getDeviceKeyProcessingTimeout() {
    return this.deviceKeyProcessingTimeout;
  }
}
