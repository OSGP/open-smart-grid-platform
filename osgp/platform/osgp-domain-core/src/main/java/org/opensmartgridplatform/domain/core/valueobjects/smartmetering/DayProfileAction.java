// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class DayProfileAction implements Comparable<DayProfileAction>, Serializable {

  private static final long serialVersionUID = 3913348299915167189L;

  private Integer scriptSelector;

  private CosemTime startTime;

  public DayProfileAction(final Integer scriptSelector, final CosemTime startTime) {
    this.scriptSelector = scriptSelector;
    this.startTime = startTime;
  }

  public Integer getScriptSelector() {
    return this.scriptSelector;
  }

  public CosemTime getStartTime() {
    return this.startTime;
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
  public int compareTo(final DayProfileAction other) {
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
    result = prime * result + this.scriptSelector.hashCode();
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
    final DayProfileAction other = (DayProfileAction) obj;
    if (!this.scriptSelector.equals(other.scriptSelector)) {
      return false;
    }
    if (!this.startTime.equals(other.startTime)) {
      return false;
    }
    return true;
  }
}
