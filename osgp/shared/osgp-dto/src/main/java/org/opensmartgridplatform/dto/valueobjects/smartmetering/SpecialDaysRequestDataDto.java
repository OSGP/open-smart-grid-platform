/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.ArrayList;
import java.util.List;

public class SpecialDaysRequestDataDto implements ActionRequestDto {

  private static final long serialVersionUID = 2733945253731054997L;

  private List<SpecialDayDto> specialDays;

  public SpecialDaysRequestDataDto(final List<SpecialDayDto> specialDays) {
    super();
    this.specialDays = new ArrayList<>(specialDays);
  }

  public List<SpecialDayDto> getSpecialDays() {
    return new ArrayList<>(this.specialDays);
  }
}
