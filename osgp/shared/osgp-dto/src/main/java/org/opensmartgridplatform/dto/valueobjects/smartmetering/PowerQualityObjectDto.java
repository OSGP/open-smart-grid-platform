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
import lombok.Getter;

@Getter
public class PowerQualityObjectDto implements Serializable {

  private static final long serialVersionUID = 2123390296585369208L;

  private final String name;
  private final String unit;

  public PowerQualityObjectDto(final String name, final String unit) {
    this.name = name;
    this.unit = unit;
  }
}
