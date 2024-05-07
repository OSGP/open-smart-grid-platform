// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import lombok.Getter;

@Getter
public class SetThdConfigurationRequestDto implements ActionRequestDto {

  private static final long serialVersionUID = 411378475387180322L;

  private final long minDurationNormalToOver;
  private final long minDurationOverToNormal;
  private final long timeThreshold;
  private final int valueHysteresis;
  private final int valueThreshold;

  public SetThdConfigurationRequestDto(
      final long minDurationNormalToOver,
      final long minDurationOverToNormal,
      final long timeThreshold,
      final int valueHysteresis,
      final int valueThreshold) {
    this.minDurationNormalToOver = minDurationNormalToOver;
    this.minDurationOverToNormal = minDurationOverToNormal;
    this.timeThreshold = timeThreshold;
    this.valueHysteresis = valueHysteresis;
    this.valueThreshold = valueThreshold;
  }

  @Override
  public String toString() {
    return "ThdConfiguration[valueThreshold="
        + this.valueThreshold
        + ", valueHysteresis="
        + this.valueHysteresis
        + ", minDurationNormalToOver="
        + this.minDurationNormalToOver
        + ", minDurationOverToNormal="
        + this.minDurationOverToNormal
        + ", timeThreshold="
        + this.timeThreshold
        + "]";
  }
}
