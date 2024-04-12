// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class ThdConfigurationDto implements Serializable {

  private static final long serialVersionUID = -8443967481145879325L;

  protected long thdValueThreshold;
  protected long thdValueHysteresis;
  protected long thdMinDurationNormalToOver;
  protected long thdMinDurationOverToNormal;
  protected long thdTimeThreshold;

  public ThdConfigurationDto(
      final long thdValueThreshold,
      final long thdValueHysteresis,
      final long thdMinDurationNormalToOver,
      final long thdMinDurationOverToNormal,
      final long thdTimeThreshold) {
    this.thdValueThreshold = thdValueThreshold;
    this.thdValueHysteresis = thdValueHysteresis;
    this.thdMinDurationNormalToOver = thdMinDurationNormalToOver;
    this.thdMinDurationOverToNormal = thdMinDurationOverToNormal;
    this.thdTimeThreshold = thdTimeThreshold;
  }

  @Override
  public String toString() {
    return "ThdConfiguration[valueThreshold="
        + this.thdValueThreshold
        + ", valueHysteresis="
        + this.thdValueHysteresis
        + ", minDurationNormalToOver="
        + this.thdMinDurationNormalToOver
        + ", minDurationOverToNormal="
        + this.thdMinDurationOverToNormal
        + ", timeThreshold="
        + this.thdTimeThreshold
        + "]";
  }

  public long getThdValueThreshold() {
    return this.thdValueThreshold;
  }

  public long getThdValueHysteresis() {
    return this.thdValueHysteresis;
  }

  public long getThdMinDurationNormalToOver() {
    return this.thdMinDurationNormalToOver;
  }

  public long getThdMinDurationOverToNormal() {
    return this.thdMinDurationOverToNormal;
  }

  public long getThdTimeThreshold() {
    return this.thdTimeThreshold;
  }
}
