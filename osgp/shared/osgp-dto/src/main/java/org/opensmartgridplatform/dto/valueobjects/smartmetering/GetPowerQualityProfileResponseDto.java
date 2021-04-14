/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.List;

public class GetPowerQualityProfileResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = -156966569210717654L;

  private List<PowerQualityProfileDataDto> powerQualityProfileDatas;

  public List<PowerQualityProfileDataDto> getPowerQualityProfileResponseDatas() {
    return this.powerQualityProfileDatas;
  }

  public void setPowerQualityProfileDatas(
      final List<PowerQualityProfileDataDto> powerQualityProfileDatas) {
    this.powerQualityProfileDatas = powerQualityProfileDatas;
  }
}
