/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class SeasonProfileDto implements Comparable<SeasonProfileDto>, Serializable {

  private static final long serialVersionUID = -9110599718005128216L;

  private String seasonProfileName;

  private CosemDateTimeDto seasonStart;

  private WeekProfileDto weekProfile;

  public SeasonProfileDto(
      final String seasonProfileName,
      final CosemDateTimeDto seasonStart,
      final WeekProfileDto weekProfile) {
    this.seasonProfileName = seasonProfileName;
    this.seasonStart = new CosemDateTimeDto(seasonStart);
    this.weekProfile = weekProfile;
  }

  public String getSeasonProfileName() {
    return this.seasonProfileName;
  }

  public CosemDateTimeDto getSeasonStart() {
    return new CosemDateTimeDto(this.seasonStart);
  }

  public WeekProfileDto getWeekProfile() {
    return this.weekProfile;
  }

  @Override
  public String toString() {
    return "SeasonProfile [\n\t\t\t\tseasonProfileName="
        + this.seasonProfileName
        + ", \n\t\t\t\tseasonStart="
        + this.seasonStart
        + ", \n\t\t\t\tweekProfile="
        + this.weekProfile
        + "\n\t\t\t]";
  }

  @Override
  public int compareTo(final SeasonProfileDto other) {
    return this.seasonStart.compareTo(other.seasonStart);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.seasonProfileName.hashCode();
    result = prime * result + this.seasonStart.hashCode();
    result = prime * result + this.weekProfile.hashCode();
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
    final SeasonProfileDto other = (SeasonProfileDto) obj;
    if (!this.seasonProfileName.equals(other.seasonProfileName)) {
      return false;
    }
    if (!this.seasonStart.equals(other.seasonStart)) {
      return false;
    }
    if (!this.weekProfile.equals(other.weekProfile)) {
      return false;
    }
    return true;
  }
}
