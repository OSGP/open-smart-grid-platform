//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class ActualPowerQualityResponse extends ActionResponse implements Serializable {

  private static final long serialVersionUID = -156966569210717657L;

  private ActualPowerQualityData actualPowerQualityData;

  public ActualPowerQualityResponse(final ActualPowerQualityData actualPowerQualityData) {
    this.actualPowerQualityData = actualPowerQualityData;
  }

  public ActualPowerQualityData getActualPowerQualityData() {
    return this.actualPowerQualityData;
  }

  public void setActualPowerQualityData(final ActualPowerQualityData actualPowerQualityData) {
    this.actualPowerQualityData = actualPowerQualityData;
  }
}
