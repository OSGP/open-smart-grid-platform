/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
