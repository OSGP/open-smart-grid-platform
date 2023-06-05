// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DayProfileDto implements Comparable<DayProfileDto>, Serializable {

  private static final long serialVersionUID = 3513563551917685789L;

  private Integer dayId;

  private List<DayProfileActionDto> dayProfileActionList;

  public DayProfileDto(final Integer dayId, final List<DayProfileActionDto> dayProfileActionList) {
    this.dayId = dayId;
    this.dayProfileActionList = new ArrayList<>(dayProfileActionList);
  }

  public Integer getDayId() {
    return this.dayId;
  }

  public List<DayProfileActionDto> getDayProfileActionList() {
    return Collections.unmodifiableList(this.dayProfileActionList);
  }

  public void setDayProfileActionList(final List<DayProfileActionDto> dayProfileActionList) {
    this.dayProfileActionList = dayProfileActionList;
  }

  @Override
  public String toString() {
    return "DayProfile [\n\t\t\t\t\t\tdayId="
        + this.dayId
        + ", \n\t\t\t\t\t\tdayProfileActionList=\n\t\t\t\t\t\t"
        + this.dayProfileActionList
        + "\n\t\t\t\t\t]";
  }

  @Override
  public int compareTo(final DayProfileDto other) {
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
    final DayProfileDto other = (DayProfileDto) obj;
    if (!this.dayId.equals(other.dayId)) {
      return false;
    }
    if (!this.dayProfileActionList.equals(other.dayProfileActionList)) {
      return false;
    }
    return true;
  }
}
