// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
