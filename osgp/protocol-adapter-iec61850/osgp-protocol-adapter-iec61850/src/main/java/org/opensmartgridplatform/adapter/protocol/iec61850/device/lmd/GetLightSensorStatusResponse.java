// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
