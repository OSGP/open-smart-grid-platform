/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class ActivityCalendarDataDto implements ActionRequestDto {

  private static final long serialVersionUID = -4254902895916399366L;

  private ActivityCalendarDto activityCalendar;

  public ActivityCalendarDataDto(final ActivityCalendarDto activityCalendar) {
    this.activityCalendar = activityCalendar;
  }

  public ActivityCalendarDto getActivityCalendar() {
    return this.activityCalendar;
  }
}
