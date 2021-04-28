/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.device.lmd;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponse;
import org.opensmartgridplatform.dto.valueobjects.LightSensorStatusDto;

public class GetLightSensorStatusResponse extends DeviceResponse {

  private final LightSensorStatusDto lightSensorStatus;

  public GetLightSensorStatusResponse(
      final DeviceRequest deviceRequest, final LightSensorStatusDto lightSensorStatus) {
    super(
        deviceRequest.getOrganisationIdentification(),
        deviceRequest.getDeviceIdentification(),
        deviceRequest.getCorrelationUid(),
        deviceRequest.getMessagePriority());
    this.lightSensorStatus = lightSensorStatus;
  }

  public LightSensorStatusDto getLightSensorStatus() {
    return this.lightSensorStatus;
  }
}
