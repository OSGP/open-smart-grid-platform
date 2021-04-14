/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;

public class PsldDataDto implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 5384893430194329868L;

  private int totalLightingHours;

  public PsldDataDto(final int totalLightingHours) {
    this.totalLightingHours = totalLightingHours;
  }

  public int getTotalLightingHours() {
    return this.totalLightingHours;
  }
}
