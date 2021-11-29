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
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DeviceKeyProcessing;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DeviceKeyProcessingRepository;
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

  public boolean isProcessing(final String deviceIdentification) {
    boolean isProcessing = false;
    final DeviceKeyProcessing deviceKeyProcessing =
        this.deviceKeyProcessingRepository.findByDeviceIdentification(deviceIdentification);
    if (deviceKeyProcessing != null) {
      final Duration durationOfKeyProcessing =
          Duration.between(deviceKeyProcessing.getStartTime().toInstant(), Instant.now());
      isProcessing = durationOfKeyProcessing.compareTo(this.deviceKeyProcessingTimeout) <= 0;
    }
    return isProcessing;
  }

  public void startProcessing(final String deviceIdentification) {
    DeviceKeyProcessing deviceKeyProcessing =
        this.deviceKeyProcessingRepository.findByDeviceIdentification(deviceIdentification);
    if (deviceKeyProcessing == null) {
      deviceKeyProcessing = new DeviceKeyProcessing();
      deviceKeyProcessing.setDeviceIdentification(deviceIdentification);
    }
    deviceKeyProcessing.setStartTime(new Date());
    this.deviceKeyProcessingRepository.save(deviceKeyProcessing);
  }

  public void stopProcessing(final String deviceIdentification) {
    this.deviceKeyProcessingRepository.deleteByDeviceIdentification(deviceIdentification);
  }

  public Duration getDeviceKeyProcessingTimeout() {
    return this.deviceKeyProcessingTimeout;
  }
}
