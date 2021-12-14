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
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DeviceKeyProcessing;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DeviceKeyProcessingRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.DeviceKeyProcessAlreadyRunningException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DeviceKeyProcessingService {

  @Value("#{T(java.time.Duration).parse('${device.key.processing.timeout:PT5M}')}")
  private Duration deviceKeyProcessingTimeout;

  private final DeviceKeyProcessingRepository deviceKeyProcessingRepository;

  @Autowired
  public DeviceKeyProcessingService(
      final DeviceKeyProcessingRepository deviceKeyProcessingRepository) {
    this.deviceKeyProcessingRepository = deviceKeyProcessingRepository;
  }

  public void startProcessing(final String deviceIdentification)
      throws DeviceKeyProcessAlreadyRunningException {
    final DeviceKeyProcessing deviceKeyProcessing = new DeviceKeyProcessing();
    deviceKeyProcessing.setDeviceIdentification(deviceIdentification);
    deviceKeyProcessing.setStartTime(Instant.now());
    try {
      this.deviceKeyProcessingRepository.saveAndFlush(deviceKeyProcessing);
    } catch (final Exception e) {
      final int updates =
          this.deviceKeyProcessingRepository.updateStartTime(
              deviceIdentification, Instant.now().minus(this.deviceKeyProcessingTimeout));
      if (updates == 0) {
        throw new DeviceKeyProcessAlreadyRunningException();
      }
    }
  }

  public void stopProcessing(final String deviceIdentification) {
    this.deviceKeyProcessingRepository.deleteById(deviceIdentification);
  }

  public Duration getDeviceKeyProcessingTimeout() {
    return this.deviceKeyProcessingTimeout;
  }
}
