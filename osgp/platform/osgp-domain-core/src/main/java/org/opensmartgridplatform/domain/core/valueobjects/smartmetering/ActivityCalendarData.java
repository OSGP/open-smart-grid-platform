//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class ActivityCalendarData implements Serializable, ActionRequest {

  private static final long serialVersionUID = 4760320198627332239L;

  private final ActivityCalendar activityCalendar;

  public ActivityCalendarData(final ActivityCalendar activityCalendar) {
    this.activityCalendar = activityCalendar;
  }

  public ActivityCalendar getActivityCalendar() {
    return this.activityCalendar;
  }

  @Override
  public void validate() throws FunctionalException {
    // No validation needed
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SET_ACTIVITY_CALENDAR;
  }
}
