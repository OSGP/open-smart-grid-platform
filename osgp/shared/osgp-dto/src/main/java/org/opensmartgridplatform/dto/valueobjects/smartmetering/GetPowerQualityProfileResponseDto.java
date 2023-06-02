//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
