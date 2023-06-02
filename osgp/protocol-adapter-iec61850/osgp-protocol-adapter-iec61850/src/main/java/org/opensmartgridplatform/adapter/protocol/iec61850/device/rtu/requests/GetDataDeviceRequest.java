//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.requests;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataRequestDto;

public class GetDataDeviceRequest extends DeviceRequest {

  private GetDataRequestDto getDataRequest;

  public GetDataDeviceRequest(
      final Builder deviceRequestBuilder, final GetDataRequestDto getDataRequest) {
    super(deviceRequestBuilder);
    this.getDataRequest = getDataRequest;
  }

  public GetDataRequestDto getDataRequest() {
    return this.getDataRequest;
  }
}
