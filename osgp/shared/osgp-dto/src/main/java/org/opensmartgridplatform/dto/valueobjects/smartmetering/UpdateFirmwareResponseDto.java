// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class UpdateFirmwareResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = -9159077783233215317L;

  private final String firmwareIdentification;

  public UpdateFirmwareResponseDto(final String firmwareIdentification) {
    this.firmwareIdentification = firmwareIdentification;
  }

  public String getFirmwareIdentification() {
    return this.firmwareIdentification;
  }
}
