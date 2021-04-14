/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.application.services;

import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceCommunicationInformationService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DeviceCommunicationInformationService.class);

  @Autowired private DeviceRepository deviceRepository;

  /**
   * Update the device record with information about the device connection. Based on the response
   * message, set the time stamps and counter for the device.
   *
   * @param message The {@link ProtocolResponseMessage} containing the response message.
   */
  @Transactional
  public void updateDeviceConnectionInformation(final ProtocolResponseMessage message) {

    final String deviceIdentification = message.getDeviceIdentification();
    Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
    final ResponseMessageResultType result = message.getResult();

    if (ResponseMessageResultType.OK == result) {
      device.updateConnectionDetailsToSuccess();
    } else if (ResponseMessageResultType.NOT_OK == result) {
      device.updateConnectionDetailsToFailure();
    } else {
      LOGGER.warn(
          "Unexpected result type: {}, connection information not updated for device: {}",
          result,
          deviceIdentification);
      return;
    }

    device = this.deviceRepository.save(device);
    LOGGER.info(
        "Updated connection information for device: {}, last successful connection timestamp: {}, last failed connection timestamp: {}, connection failure count: {} based on result type: {}",
        deviceIdentification,
        device.getLastSuccessfulConnectionTimestamp(),
        device.getLastFailedConnectionTimestamp(),
        device.getFailedConnectionCount(),
        result.name());
  }
}
