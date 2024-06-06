// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

public class RelayData implements java.io.Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = -5504760646997048607L;

  private int index;
  private int totalLightingMinutes;

  public RelayData(final int index, final int totalLightingMinutes) {
    this.index = index;
    this.totalLightingMinutes = totalLightingMinutes;
  }

  public int getIndex() {
    return this.index;
  }

  public void setIndex(final int index) {
    this.index = index;
  }

  public int getTotalLightingMinutes() {
    return this.totalLightingMinutes;
  }

  public void setTotalLightingMinutes(final int totalLightingMinutes) {
    this.totalLightingMinutes = totalLightingMinutes;
  }
}
