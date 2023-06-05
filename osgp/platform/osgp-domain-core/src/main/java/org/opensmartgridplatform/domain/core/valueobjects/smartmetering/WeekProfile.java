// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class WeekProfile implements Comparable<WeekProfile>, Serializable {

  private static final long serialVersionUID = 2838240604182800624L;

  private String weekProfileName;

  private DayProfile monday;

  private DayProfile tuesday;

  private DayProfile wednesday;

  private DayProfile thursday;

  private DayProfile friday;

  private DayProfile saturday;

  private DayProfile sunday;

  private WeekProfile(final Builder builder) {
    this.weekProfileName = builder.weekProfileName;
    this.monday = builder.monday;
    this.tuesday = builder.tuesday;
    this.wednesday = builder.wednesday;
    this.thursday = builder.thursday;
    this.friday = builder.friday;
    this.saturday = builder.saturday;
    this.sunday = builder.sunday;
  }

  public static class Builder {

    private String weekProfileName;
    private DayProfile monday;
    private DayProfile tuesday;
    private DayProfile wednesday;
    private DayProfile thursday;
    private DayProfile friday;
    private DayProfile saturday;
    private DayProfile sunday;

    public WeekProfile build() {
      return new WeekProfile(this);
    }

    public Builder withWeekProfileName(final String weekProfileName) {
      this.weekProfileName = weekProfileName;
      return this;
    }

    public Builder withMonday(final DayProfile monday) {
      this.monday = monday;
      return this;
    }

    public Builder withTuesday(final DayProfile tuesday) {
      this.tuesday = tuesday;
      return this;
    }

    public Builder withWednesday(final DayProfile wednesday) {
      this.wednesday = wednesday;
      return this;
    }

    public Builder withThursday(final DayProfile thursday) {
      this.thursday = thursday;
      return this;
    }

    public Builder withFriday(final DayProfile friday) {
      this.friday = friday;
      return this;
    }

    public Builder withSaturday(final DayProfile saturday) {
      this.saturday = saturday;
      return this;
    }

    public Builder withSunday(final DayProfile sunday) {
      this.sunday = sunday;
      return this;
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public String getWeekProfileName() {
    return this.weekProfileName;
  }

  public DayProfile getMonday() {
    return this.monday;
  }

  public DayProfile getTuesday() {
    return this.tuesday;
  }

  public DayProfile getWednesday() {
    return this.wednesday;
  }

  public DayProfile getThursday() {
    return this.thursday;
  }

  public DayProfile getFriday() {
    return this.friday;
  }

  public DayProfile getSaturday() {
    return this.saturday;
  }

  public DayProfile getSunday() {
    return this.sunday;
  }

  @Override
  public String toString() {
    return "WeekProfile [weekProfileName="
        + this.weekProfileName
        + ", monday="
        + this.monday
        + ", tuesday="
        + this.tuesday
        + ", wednesday="
        + this.wednesday
        + ", thursday="
        + this.thursday
        + ", friday="
        + this.friday
        + ", saturday="
        + this.saturday
        + ", sunday="
        + this.sunday
        + "]";
  }

  @Override
  public int compareTo(final WeekProfile other) {
    return this.weekProfileName.compareTo(other.weekProfileName);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.friday == null) ? 0 : this.friday.hashCode());
    result = prime * result + ((this.monday == null) ? 0 : this.monday.hashCode());
    result = prime * result + ((this.saturday == null) ? 0 : this.saturday.hashCode());
    result = prime * result + ((this.sunday == null) ? 0 : this.sunday.hashCode());
    result = prime * result + ((this.thursday == null) ? 0 : this.thursday.hashCode());
    result = prime * result + ((this.tuesday == null) ? 0 : this.tuesday.hashCode());
    result = prime * result + ((this.wednesday == null) ? 0 : this.wednesday.hashCode());
    result =
        prime * result + ((this.weekProfileName == null) ? 0 : this.weekProfileName.hashCode());
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
    final WeekProfile other = (WeekProfile) obj;
    if (!this.friday.equals(other.friday)) {
      return false;
    }
    if (!this.monday.equals(other.monday)) {
      return false;
    }
    if (!this.saturday.equals(other.saturday)) {
      return false;
    }
    if (!this.sunday.equals(other.sunday)) {
      return false;
    }
    if (!this.thursday.equals(other.thursday)) {
      return false;
    }
    if (!this.tuesday.equals(other.tuesday)) {
      return false;
    }
    if (!this.wednesday.equals(other.wednesday)) {
      return false;
    }
    if (!this.weekProfileName.equals(other.weekProfileName)) {
      return false;
    }
    return true;
  }
}
