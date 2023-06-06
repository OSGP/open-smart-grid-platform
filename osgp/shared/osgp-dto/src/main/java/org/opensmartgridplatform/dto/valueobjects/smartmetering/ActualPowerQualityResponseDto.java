// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class ActualPowerQualityResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = -156966569210717654L;

  private ActualPowerQualityDataDto actualPowerQualityData;

  public ActualPowerQualityDataDto getActualPowerQualityData() {
    return this.actualPowerQualityData;
  }

  public void setActualPowerQualityDataDto(final ActualPowerQualityDataDto actualPowerQualityData) {
    this.actualPowerQualityData = actualPowerQualityData;
  }
}
