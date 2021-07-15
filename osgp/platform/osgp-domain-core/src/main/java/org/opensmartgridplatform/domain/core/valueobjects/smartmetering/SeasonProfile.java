/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class SeasonProfile implements Comparable<SeasonProfile>, Serializable {

  private static final long serialVersionUID = -9110599718005128216L;

  private String seasonProfileName;

  private CosemDateTime seasonStart;

  private WeekProfile weekProfile;

  public SeasonProfile(
      final String seasonProfileName,
      final CosemDateTime seasonStart,
      final WeekProfile weekProfile) {
    this.seasonProfileName = seasonProfileName;
    this.seasonStart = seasonStart;
    this.weekProfile = weekProfile;
  }

  public String getSeasonProfileName() {
    return this.seasonProfileName;
  }

  public CosemDateTime getSeasonStart() {
    return this.seasonStart;
  }

  public WeekProfile getWeekProfile() {
    return this.weekProfile;
  }

  @Override
  public String toString() {
    return "SeasonProfile [seasonProfileName="
        + this.seasonProfileName
        + ", seasonStart="
        + this.seasonStart
        + ", weekProfile="
        + this.weekProfile
        + "]";
  }

  @Override
  public int compareTo(final SeasonProfile other) {
    return this.seasonProfileName.compareTo(other.seasonProfileName);
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
    final SeasonProfile other = (SeasonProfile) obj;
    if (!this.seasonProfileName.equals(other.seasonProfileName)) {
      return false;
    }
    if (this.seasonStart.equals(other.seasonStart)) {
      return false;
    }
    if (!this.weekProfile.equals(other.weekProfile)) {
      return false;
    }
    return true;
  }
}
