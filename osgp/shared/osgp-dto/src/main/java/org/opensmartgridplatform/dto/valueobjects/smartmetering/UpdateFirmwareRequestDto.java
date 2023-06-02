//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class UpdateFirmwareRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = 4779593744529504287L;

  private final String firmwareIdentification;
  private final String deviceIdentification;

  public UpdateFirmwareRequestDto(
      final String firmwareIdentification, final String deviceIdentification) {
    this.firmwareIdentification = firmwareIdentification;
    this.deviceIdentification = deviceIdentification;
  }

  public String getFirmwareIdentification() {
    return this.firmwareIdentification;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }
}
