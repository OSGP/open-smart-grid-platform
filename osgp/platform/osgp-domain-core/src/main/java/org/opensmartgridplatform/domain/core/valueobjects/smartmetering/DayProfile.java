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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DayProfile implements Comparable<DayProfile>, Serializable {

  private static final long serialVersionUID = 3513563551917685789L;

  private Integer dayId;

  private List<DayProfileAction> dayProfileActionList;

  public DayProfile(final Integer dayId, final List<DayProfileAction> dayProfileActionList) {
    this.dayId = dayId;
    this.dayProfileActionList = new ArrayList<DayProfileAction>(dayProfileActionList);
  }

  public Integer getDayId() {
    return this.dayId;
  }

  public List<DayProfileAction> getDayProfileActionList() {
    return Collections.unmodifiableList(this.dayProfileActionList);
  }

  public void setDayProfileActionList(final List<DayProfileAction> dayProfileActionList) {
    this.dayProfileActionList = dayProfileActionList;
  }

  @Override
  public String toString() {
    return "DayProfile [dayId="
        + this.dayId
        + ", dayProfileActionList="
        + this.dayProfileActionList
        + "]";
  }

  @Override
  public int compareTo(final DayProfile other) {
    return this.dayId.compareTo(other.dayId);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.dayId.hashCode();
    result = prime * result + this.dayProfileActionList.hashCode();
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
    final DayProfile other = (DayProfile) obj;
    if (!this.dayId.equals(other.dayId)) {
      return false;
    }
    if (!this.dayProfileActionList.equals(other.dayProfileActionList)) {
      return false;
    }
    return true;
  }
}
