// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
