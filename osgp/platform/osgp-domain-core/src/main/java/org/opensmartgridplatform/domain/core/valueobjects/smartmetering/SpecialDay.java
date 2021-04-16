/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class SpecialDay implements Serializable {

  private static final long serialVersionUID = 8009905447322732462L;

  private final CosemDate specialDayDate;

  private final int dayId;

  public SpecialDay(final CosemDate specialDayDate, final int dayId) {
    super();
    this.specialDayDate = new CosemDate(specialDayDate);
    this.dayId = dayId;
  }

  public CosemDate getSpecialDayDate() {
    return new CosemDate(this.specialDayDate);
  }

  public int getDayId() {
    return this.dayId;
  }
}
