// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
