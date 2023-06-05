// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.activitycalendar;

public class WeekProfile {
  private String name;
  private int mondayDayId;
  private int tuesdayDayId;
  private int wednesdayDayId;
  private int thursdayDayId;
  private int fridayDayId;
  private int saturdayDayId;
  private int sundayDayId;

  public WeekProfile(
      final String name,
      final int mondayDayId,
      final int tuesdayDayId,
      final int wednesdayDayId,
      final int thursdayDayId,
      final int fridayDayId,
      final int saturdayDayId,
      final int sundayDayId) {
    this.name = name;
    this.mondayDayId = mondayDayId;
    this.tuesdayDayId = tuesdayDayId;
    this.wednesdayDayId = wednesdayDayId;
    this.thursdayDayId = thursdayDayId;
    this.fridayDayId = fridayDayId;
    this.saturdayDayId = saturdayDayId;
    this.sundayDayId = sundayDayId;
  }

  public String getName() {
    return this.name;
  }

  public int getMondayDayId() {
    return this.mondayDayId;
  }

  public int getTuesdayDayId() {
    return this.tuesdayDayId;
  }

  public int getWednesdayDayId() {
    return this.wednesdayDayId;
  }

  public int getThursdayDayId() {
    return this.thursdayDayId;
  }

  public int getFridayDayId() {
    return this.fridayDayId;
  }

  public int getSaturdayDayId() {
    return this.saturdayDayId;
  }

  public int getSundayDayId() {
    return this.sundayDayId;
  }
}
