/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class SpecialDayDto implements Serializable {

  private static final long serialVersionUID = 1218914744657942413L;

  private final CosemDateDto specialDayDate;

  private final int dayId;

  public SpecialDayDto(final CosemDateDto specialDayDate, final int dayId) {
    super();
    this.specialDayDate = new CosemDateDto(specialDayDate);
    this.dayId = dayId;
  }

  public CosemDateDto getSpecialDayDate() {
    return new CosemDateDto(this.specialDayDate);
  }

  public int getDayId() {
    return this.dayId;
  }
}
