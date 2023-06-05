// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ActivityCalendar implements Comparable<ActivityCalendar>, Serializable {

  private static final long serialVersionUID = -8278955482889960359L;

  private String calendarName;

  private CosemDateTime activatePassiveCalendarTime;

  private List<SeasonProfile> seasonProfileList;

  public ActivityCalendar(
      final String calendarName,
      final CosemDateTime activatePassiveCalendarTime,
      final List<SeasonProfile> seasonProfileList) {
    this.calendarName = calendarName;
    this.activatePassiveCalendarTime = new CosemDateTime(activatePassiveCalendarTime);
    this.seasonProfileList = new ArrayList<>(seasonProfileList);
  }

  public String getCalendarName() {
    return this.calendarName;
  }

  public CosemDateTime getActivatePassiveCalendarTime() {
    return new CosemDateTime(this.activatePassiveCalendarTime);
  }

  public List<SeasonProfile> getSeasonProfileList() {
    return new ArrayList<>(this.seasonProfileList);
  }

  @Override
  public String toString() {
    return "ActivityCalendar [calendarName="
        + this.calendarName
        + ", seasonProfileList="
        + this.seasonProfileList
        + "]";
  }

  @Override
  public int compareTo(final ActivityCalendar other) {
    return this.calendarName.compareTo(other.calendarName);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.calendarName.hashCode();
    result = prime * result + this.activatePassiveCalendarTime.hashCode();
    result = prime * result + this.seasonProfileList.hashCode();
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final ActivityCalendar other = (ActivityCalendar) obj;
    if (!this.calendarName.equals(other.calendarName)) {
      return false;
    }
    if (this.activatePassiveCalendarTime.equals(other.activatePassiveCalendarTime)) {
      return false;
    }
    if (!this.seasonProfileList.equals(other.seasonProfileList)) {
      return false;
    }
    return true;
  }
}
