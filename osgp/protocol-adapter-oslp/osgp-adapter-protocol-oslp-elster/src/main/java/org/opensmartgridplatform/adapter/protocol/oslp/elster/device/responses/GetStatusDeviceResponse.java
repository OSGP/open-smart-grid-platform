//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.responses;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponse;
import org.opensmartgridplatform.dto.valueobjects.DeviceStatusDto;

public class GetStatusDeviceResponse extends DeviceResponse {

  private final DeviceStatusDto deviceStatus;

  public GetStatusDeviceResponse(
      final DeviceRequest deviceRequest, final DeviceStatusDto deviceStatus) {
    super(
        deviceRequest.getOrganisationIdentification(),
        deviceRequest.getDeviceIdentification(),
        deviceRequest.getCorrelationUid(),
        deviceRequest.getMessagePriority());
    this.deviceStatus = deviceStatus;
  }

  public DeviceStatusDto getDeviceStatus() {
    return this.deviceStatus;
  }
}
