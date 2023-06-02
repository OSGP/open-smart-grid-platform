//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.FirmwareModuleData;

public class UpdateFirmwareDeviceRequest extends DeviceRequest {

  private String firmwareDomain;
  private String firmwareUrl;
  private FirmwareModuleData firmwareModuleData;

  public UpdateFirmwareDeviceRequest(
      final Builder deviceRequestBuilder,
      final String firmwareDomain,
      final String firmwareUrl,
      final FirmwareModuleData firmwareModuleData) {
    super(deviceRequestBuilder);
    this.firmwareDomain = firmwareDomain;
    this.firmwareUrl = firmwareUrl;
    this.firmwareModuleData = firmwareModuleData;
  }

  public String getFirmwareDomain() {
    return this.firmwareDomain;
  }

  public String getFirmwareUrl() {
    return this.firmwareUrl;
  }

  public FirmwareModuleData getFirmwareModuleData() {
    return this.firmwareModuleData;
  }
}
