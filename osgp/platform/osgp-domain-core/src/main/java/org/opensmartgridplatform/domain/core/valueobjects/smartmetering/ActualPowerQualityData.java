/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.util.List;

public class ActualPowerQualityData extends ActionResponse {

  private static final long serialVersionUID = -156966569210717657L;

  private final List<PowerQualityObject> powerQualityObjects;
  private final List<PowerQualityValue> powerQualityValues;

  public ActualPowerQualityData(
      final List<PowerQualityObject> powerQualityObjects,
      final List<PowerQualityValue> powerQualityValues) {
    this.powerQualityObjects = powerQualityObjects;
    this.powerQualityValues = powerQualityValues;
  }

  public List<PowerQualityObject> getPowerQualityObjects() {
    return this.powerQualityObjects;
  }

  public List<PowerQualityValue> getPowerQualityValues() {
    return this.powerQualityValues;
  }
}
