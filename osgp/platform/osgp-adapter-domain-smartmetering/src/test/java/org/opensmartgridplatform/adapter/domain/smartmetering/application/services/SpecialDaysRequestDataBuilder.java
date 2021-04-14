/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import java.util.ArrayList;
import java.util.List;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecialDay;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SpecialDaysRequestData;

public class SpecialDaysRequestDataBuilder {
  private List<SpecialDay> specialDays = new ArrayList<SpecialDay>();

  public SpecialDaysRequestDataBuilder withSpecialDays(final List<SpecialDay> specialDays) {
    this.specialDays = specialDays;
    return this;
  }

  public SpecialDaysRequestDataBuilder addSpecialDay(final SpecialDay specialDay) {
    this.specialDays.add(specialDay);
    return this;
  }

  public SpecialDaysRequestData build() {
    return new SpecialDaysRequestData(this.specialDays);
  }
}
