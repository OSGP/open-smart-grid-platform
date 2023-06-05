// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class GetPowerQualityProfileResponse extends ActionResponse implements Serializable {

  private static final long serialVersionUID = -156966569210717657L;

  private List<PowerQualityProfileData> powerQualityProfileDatas;

  public void setPowerQualityProfileDatas(
      final List<PowerQualityProfileData> powerQualityProfileDatas) {
    this.powerQualityProfileDatas = powerQualityProfileDatas;
  }

  public List<PowerQualityProfileData> getPowerQualityProfileDatas() {
    return this.powerQualityProfileDatas;
  }
}
