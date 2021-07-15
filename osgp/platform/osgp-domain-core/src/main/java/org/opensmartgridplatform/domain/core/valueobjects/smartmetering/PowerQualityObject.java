/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
