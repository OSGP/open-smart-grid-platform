/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceMessageStatus;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponse;

public class EmptyDeviceResponse extends DeviceResponse {

  private final DeviceMessageStatus status;

  public EmptyDeviceResponse(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final int messagePriority,
      final DeviceMessageStatus status) {
    super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);
    this.status = status;
  }

  public EmptyDeviceResponse(final DeviceRequest deviceRequest, final DeviceMessageStatus status) {
    super(
        deviceRequest.getOrganisationIdentification(),
        deviceRequest.getDeviceIdentification(),
        deviceRequest.getCorrelationUid(),
        deviceRequest.getMessagePriority());
    this.status = status;
  }

  public DeviceMessageStatus getStatus() {
    return this.status;
  }
}
