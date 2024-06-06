// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class ActualPowerQualityDataDto implements Serializable {

  private static final long serialVersionUID = 5222890224967684849L;

  private final List<PowerQualityObjectDto> powerQualityObjects;
  private final List<PowerQualityValueDto> powerQualityValues;

  public ActualPowerQualityDataDto(
      final List<PowerQualityObjectDto> powerQualityObjects,
      final List<PowerQualityValueDto> powerQualityValues) {
    this.powerQualityObjects = powerQualityObjects;
    this.powerQualityValues = powerQualityValues;
  }

  public List<PowerQualityObjectDto> getPowerQualityObjects() {
    return this.powerQualityObjects;
  }

  public List<PowerQualityValueDto> getPowerQualityValues() {
    return this.powerQualityValues;
  }
}
