// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.math.BigDecimal;

/** A metervalue with scaler applied together with its unit on the meter */
public class DlmsMeterValueDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private final BigDecimal value;
  private final DlmsUnitTypeDto dlmsUnit;

  public DlmsMeterValueDto(final BigDecimal value, final DlmsUnitTypeDto unit) {
    this.value = value;
    this.dlmsUnit = unit;
  }

  public BigDecimal getValue() {
    return this.value;
  }

  public DlmsUnitTypeDto getDlmsUnit() {
    return this.dlmsUnit;
  }

  @Override
  public String toString() {
    return "DlmsMeterValue [value=" + this.value + ", dlmsUnit=" + this.dlmsUnit + "]";
  }
}
