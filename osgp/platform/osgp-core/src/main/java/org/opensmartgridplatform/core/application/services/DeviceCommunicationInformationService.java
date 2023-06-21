// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.application.services;

import java.time.ZonedDateTime;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
  public void updateDeviceConnectionInformation(final ProtocolResponseMessage message) {
    final String deviceIdentification = message.getDeviceIdentification();
    final ResponseMessageResultType result = message.getResult();

    if (ResponseMessageResultType.OK == result) {
      final int updatedRecords =
          this.deviceRepository.updateConnectionDetailsToSuccess(deviceIdentification);
      LOGGER.info(
          "Updated connection information for device: {}, last successful connection timestamp: {} based on result type: {} (updated {} records)",
          deviceIdentification,
          ZonedDateTime.now(),
          result.name(),
          updatedRecords);
    } else if (ResponseMessageResultType.NOT_OK == result) {
      final int updatedRecords =
          this.deviceRepository.updateConnectionDetailsToFailure(deviceIdentification);
      LOGGER.info(
          "Updated connection information for device: {}, last failed connection timestamp: {} based on result type: {} (updated {} records)",
          deviceIdentification,
          ZonedDateTime.now(),
          result.name(),
          updatedRecords);
    } else {
      LOGGER.warn(
          "Unexpected result type: {}, connection information not updated for device: {}",
          result,
          deviceIdentification);
    }
  }
}
