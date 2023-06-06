// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses;

import java.util.List;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponse;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionDto;

public class GetFirmwareVersionDeviceResponse extends DeviceResponse {

  private final List<FirmwareVersionDto> firmwareVersions;

  public GetFirmwareVersionDeviceResponse(
      final DeviceRequest deviceRequest, final List<FirmwareVersionDto> firmwareVersions) {
    super(
        deviceRequest.getOrganisationIdentification(),
        deviceRequest.getDeviceIdentification(),
        deviceRequest.getCorrelationUid(),
        deviceRequest.getMessagePriority());
    this.firmwareVersions = firmwareVersions;
  }

  public List<FirmwareVersionDto> getFirmwareVersions() {
    return this.firmwareVersions;
  }
}
