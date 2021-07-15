/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.math.BigDecimal;

/** A metervalue with its standardized osgp unit */
public class OsgpMeterValue implements Serializable {

  private static final long serialVersionUID = 1L;

  private final BigDecimal value;
  private final OsgpUnit osgpUnit;

  public OsgpMeterValue(final BigDecimal value, final OsgpUnit unit) {
    this.value = value;
    this.osgpUnit = unit;
  }

  public BigDecimal getValue() {
    return this.value;
  }

  public OsgpUnit getOsgpUnit() {
    return this.osgpUnit;
  }

  @Override
  public String toString() {
    return "OsgpMeterValue [value=" + this.value + ", osgpUnit=" + this.osgpUnit + "]";
  }
}
