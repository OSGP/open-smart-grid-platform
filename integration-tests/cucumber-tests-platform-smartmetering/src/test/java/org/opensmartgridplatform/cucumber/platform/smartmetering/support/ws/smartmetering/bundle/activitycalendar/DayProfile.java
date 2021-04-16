/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.activitycalendar;

import java.util.ArrayList;
import java.util.List;

public class DayProfile {
  private int dayId;
  private List<DayProfileAction> dayProfileActions;

  public DayProfile(final int dayId) {
    this.dayId = dayId;
    this.dayProfileActions = new ArrayList<>();
  }

  public int getDayId() {
    return this.dayId;
  }

  public List<DayProfileAction> getDayProfileActions() {
    return this.dayProfileActions;
  }
}
