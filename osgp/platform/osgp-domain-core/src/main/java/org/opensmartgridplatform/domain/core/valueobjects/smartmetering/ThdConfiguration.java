// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class ThdConfiguration implements Serializable {

  private static final long serialVersionUID = 1540432244403617197L;

  protected long thdValueThreshold;
  protected long thdValueHysteresis;
  protected long thdMinDurationNormalToOver;
  protected long thdMinDurationOverToNormal;
  protected long thdTimeThreshold;

  public ThdConfiguration(
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
