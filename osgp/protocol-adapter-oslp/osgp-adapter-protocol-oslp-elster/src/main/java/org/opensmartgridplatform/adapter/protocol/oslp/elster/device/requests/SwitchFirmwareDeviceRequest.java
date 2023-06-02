//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;

public class SwitchFirmwareDeviceRequest extends DeviceRequest {

  private final String version;

  public SwitchFirmwareDeviceRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final int messagePriority,
      final String version) {
    super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);

    this.version = version;
  }

  public SwitchFirmwareDeviceRequest(final Builder deviceRequestBuilder, final String version) {
    super(deviceRequestBuilder);
    this.version = version;
  }

  public String getVersion() {
    return this.version;
  }
}
