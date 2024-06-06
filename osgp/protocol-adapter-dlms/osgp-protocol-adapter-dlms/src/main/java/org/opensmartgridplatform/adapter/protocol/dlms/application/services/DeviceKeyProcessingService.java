// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.time.Duration;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.DeviceKeyProcessAlreadyRunningException;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional(value = "transactionManager")
@Component
@Slf4j
public class DeviceKeyProcessingService {

  @Value("#{T(java.time.Duration).parse('${device.key.processing.timeout:PT5M}')}")
  private Duration deviceKeyProcessingTimeout;

  private final DlmsDeviceRepository dlmsDeviceRepository;

  @Autowired
  public DeviceKeyProcessingService(final DlmsDeviceRepository dlmsDeviceRepository) {
    this.dlmsDeviceRepository = dlmsDeviceRepository;
  }

  public void startProcessing(final String deviceIdentification)
      throws DeviceKeyProcessAlreadyRunningException, FunctionalException {

    log.info("Attempt to start key changing process for {}", deviceIdentification);

    final DlmsDevice dlmsDevice =
        this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
    if (dlmsDevice == null) {
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_DEVICE,
          ComponentType.PROTOCOL_DLMS,
          new ProtocolAdapterException(
              "Unable to start key changing process with unknown device: " + deviceIdentification));
    }

    final Instant oldestStartTimeNotConsiderTimedOut =
        Instant.now().minus(this.deviceKeyProcessingTimeout);
    final int updatedRecords =
        this.dlmsDeviceRepository.setProcessingStartTime(
            dlmsDevice.getDeviceIdentification(), oldestStartTimeNotConsiderTimedOut);
    if (updatedRecords == 0) {
      throw new DeviceKeyProcessAlreadyRunningException();
    }

    log.info("Started key changing process for {}", deviceIdentification);
  }

  public void stopProcessing(final String deviceIdentification) {
    final DlmsDevice dlmsDevice =
        this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
    dlmsDevice.setKeyProcessingStartTime(null);
    this.dlmsDeviceRepository.saveAndFlush(dlmsDevice);

    log.info("Stopped key changing process for {}", deviceIdentification);
  }

  public Duration getDeviceKeyProcessingTimeout() {
    return this.deviceKeyProcessingTimeout;
  }
}
