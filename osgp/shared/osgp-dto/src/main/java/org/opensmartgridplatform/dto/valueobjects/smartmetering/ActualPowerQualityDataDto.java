/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
