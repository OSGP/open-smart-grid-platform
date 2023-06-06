// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.device.rtu.requests;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SetDataRequestDto;

public class SetDataDeviceRequest extends DeviceRequest {

  private SetDataRequestDto setDataRequest;

  public SetDataDeviceRequest(
      final Builder deviceRequestBuilder, final SetDataRequestDto setDataRequest) {
    super(deviceRequestBuilder);
    this.setDataRequest = setDataRequest;
  }

  public SetDataRequestDto getSetDataRequest() {
    return this.setDataRequest;
  }
}
