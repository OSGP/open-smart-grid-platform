// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.responses;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponse;

public class GetFirmwareVersionDeviceResponse extends DeviceResponse {

  private final String firmwareVersion;

  public GetFirmwareVersionDeviceResponse(
      final DeviceRequest deviceRequest, final String firmwareVersion) {
    super(
        deviceRequest.getOrganisationIdentification(),
        deviceRequest.getDeviceIdentification(),
        deviceRequest.getCorrelationUid(),
        deviceRequest.getMessagePriority());
    this.firmwareVersion = firmwareVersion;
  }

  public String getFirmwareVersion() {
    return this.firmwareVersion;
  }
}
