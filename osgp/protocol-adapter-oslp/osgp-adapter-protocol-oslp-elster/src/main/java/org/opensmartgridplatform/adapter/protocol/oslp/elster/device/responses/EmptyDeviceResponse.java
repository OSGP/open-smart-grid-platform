// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.responses;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceMessageStatus;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceResponse;

public class EmptyDeviceResponse extends DeviceResponse {

  private final DeviceMessageStatus status;

  public EmptyDeviceResponse(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final int messagePriority,
      final DeviceMessageStatus status) {
    super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);
    this.status = status;
  }

  public EmptyDeviceResponse(final DeviceRequest deviceRequest, final DeviceMessageStatus status) {
    super(
        deviceRequest.getOrganisationIdentification(),
        deviceRequest.getDeviceIdentification(),
        deviceRequest.getCorrelationUid(),
        deviceRequest.getMessagePriority());
    this.status = status;
  }

  public DeviceMessageStatus getStatus() {
    return this.status;
  }
}
