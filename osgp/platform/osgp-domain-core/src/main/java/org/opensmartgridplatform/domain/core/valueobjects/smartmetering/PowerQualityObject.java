// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class PowerQualityObject implements Serializable {

  private static final long serialVersionUID = 991045734132231909L;

  private final String name;
  private final String unit;

  public PowerQualityObject(final String name, final String unit) {
    this.name = name;
    this.unit = unit;
  }

  public String getName() {
    return this.name;
  }

  public String getUnit() {
    return this.unit;
  }
}
