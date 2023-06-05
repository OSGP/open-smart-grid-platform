// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
