/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.device.da.rtu;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceMessageStatus;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses.EmptyDeviceResponse;

public class DaDeviceResponse extends EmptyDeviceResponse {

  private final Serializable dataResponse;

  public DaDeviceResponse(
      final DeviceRequest deviceRequest,
      final DeviceMessageStatus status,
      final Serializable dataResponse) {
    super(
        deviceRequest.getOrganisationIdentification(),
        deviceRequest.getDeviceIdentification(),
        deviceRequest.getCorrelationUid(),
        deviceRequest.getMessagePriority(),
        status);
    this.dataResponse = dataResponse;
  }

  public Serializable getDataResponse() {
    return this.dataResponse;
  }
}
