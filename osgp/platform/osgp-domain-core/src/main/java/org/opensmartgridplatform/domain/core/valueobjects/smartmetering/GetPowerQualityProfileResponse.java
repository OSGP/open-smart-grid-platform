/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
