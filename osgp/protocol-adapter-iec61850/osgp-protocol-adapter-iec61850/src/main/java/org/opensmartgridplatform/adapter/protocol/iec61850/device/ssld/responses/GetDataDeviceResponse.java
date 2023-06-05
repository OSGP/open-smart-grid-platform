// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceMessageStatus;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataResponseDto;

public class GetDataDeviceResponse extends EmptyDeviceResponse {

  GetDataResponseDto dataResponse;

  public GetDataDeviceResponse(
      final DeviceRequest deviceRequest,
      final DeviceMessageStatus status,
      final GetDataResponseDto dataResponse) {
    super(
        deviceRequest.getOrganisationIdentification(),
        deviceRequest.getDeviceIdentification(),
        deviceRequest.getCorrelationUid(),
        deviceRequest.getMessagePriority(),
        status);
    this.dataResponse = dataResponse;
  }

  public GetDataResponseDto getDataResponse() {
    return this.dataResponse;
  }
}
