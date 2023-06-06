// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.services;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceMessageLog;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.Iec61850LogItemRequestMessage;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.messaging.Iec61850LogItemRequestMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service(value = "protocolIec61850DeviceMessageLoggingService")
public class DeviceMessageLoggingService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceMessageLoggingService.class);

  @Autowired
  @Qualifier("protocolIec61850OutboundLogItemRequestsMessageSender")
  private Iec61850LogItemRequestMessageSender iec61850LogItemRequestMessageSender;

  public void logMessage(
      final DeviceRequest deviceRequest,
      final boolean incoming,
      final boolean valid,
      final String message,
      final int size) {

    final String deviceIdentification = deviceRequest.getDeviceIdentification();
    final String organisationIdentification = deviceRequest.getOrganisationIdentification();
    final String command = deviceRequest.getClass().getSimpleName();

    final Iec61850LogItemRequestMessage iec61850LogItemRequestMessage =
        new Iec61850LogItemRequestMessage(
            deviceIdentification,
            organisationIdentification,
            incoming,
            valid,
            command + " - " + message,
            size);

    LOGGER.info("Sending iec61850LogItemRequestMessage for device: {}", deviceIdentification);
    this.iec61850LogItemRequestMessageSender.send(iec61850LogItemRequestMessage);
  }

  public void logMessage(
      final DeviceMessageLog deviceMessageLog,
      final String deviceIdentification,
      final String organisationIdentification,
      final boolean incoming) {

    final Iec61850LogItemRequestMessage iec61850LogItemRequestMessage =
        new Iec61850LogItemRequestMessage(
            deviceIdentification,
            organisationIdentification,
            incoming,
            true,
            deviceMessageLog.getMessage(),
            0);

    LOGGER.info("Sending iec61850LogItemRequestMessage for device: {}", deviceIdentification);
    this.iec61850LogItemRequestMessageSender.send(iec61850LogItemRequestMessage);
  }
}
