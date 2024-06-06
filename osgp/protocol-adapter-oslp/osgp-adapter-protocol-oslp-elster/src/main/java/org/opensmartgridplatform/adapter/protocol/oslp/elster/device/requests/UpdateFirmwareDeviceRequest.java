// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;

public class UpdateFirmwareDeviceRequest extends DeviceRequest {

  private final String firmwareDomain;
  private final String firmwareUrl;

  public UpdateFirmwareDeviceRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final int messagePriority,
      final String firmwareDomain,
      final String firmwareUrl) {
    super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);
    this.firmwareDomain = firmwareDomain;
    this.firmwareUrl = firmwareUrl;
  }

  public UpdateFirmwareDeviceRequest(
      final Builder deviceRequestBuilder, final String firmwareDomain, final String firmwareUrl) {
    super(deviceRequestBuilder);
    this.firmwareDomain = firmwareDomain;
    this.firmwareUrl = firmwareUrl;
  }

  public String getFirmwareDomain() {
    return this.firmwareDomain;
  }

  public String getFirmwareUrl() {
    return this.firmwareUrl;
  }
}
