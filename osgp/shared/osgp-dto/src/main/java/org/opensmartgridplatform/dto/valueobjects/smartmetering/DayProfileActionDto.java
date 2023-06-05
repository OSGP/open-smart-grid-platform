// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class DayProfileActionDto implements Comparable<DayProfileActionDto>, Serializable {

  private static final long serialVersionUID = 3913348299915167189L;

  private Integer scriptSelector;

  private CosemTimeDto startTime;

  public DayProfileActionDto(final Integer scriptSelector, final CosemTimeDto startTime) {
    this.scriptSelector = scriptSelector;
    this.startTime = new CosemTimeDto(startTime);
  }

  public Integer getScriptSelector() {
    return this.scriptSelector;
  }

  public CosemTimeDto getStartTime() {
    return new CosemTimeDto(this.startTime);
  }

  @Override
  public String toString() {
    return "DayProfileAction [scriptSelector="
        + this.scriptSelector
        + ", startTime="
        + this.startTime
        + "]";
  }

  @Override
  public int compareTo(final DayProfileActionDto other) {
    final int rank = this.scriptSelector.compareTo(other.scriptSelector);
    if (rank != 0) {
      return rank;
    }
    return this.startTime.compareTo(other.startTime);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + this.startTime.hashCode();
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
    final DayProfileActionDto other = (DayProfileActionDto) obj;
    if (!this.scriptSelector.equals(other.scriptSelector)) {
      return false;
    }
    if (!this.startTime.equals(other.startTime)) {
      return false;
    }
    return true;
  }
}
