// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.Objects;
import lombok.Getter;

@Getter
public class ThdConfigurationDto implements ActionRequestDto {

  private final long minDurationNormalToOver;
  private final long minDurationOverToNormal;
  private final long timeThreshold;
  private final long valueHysteresis;
  private final long valueThreshold;

  protected ThdConfigurationDto(final Builder builder) {

    this.minDurationNormalToOver = Objects.requireNonNull(builder.minDurationNormalToOver);
    this.minDurationOverToNormal = Objects.requireNonNull(builder.minDurationOverToNormal);
    this.timeThreshold = Objects.requireNonNull(builder.timeThreshold);
    this.valueHysteresis = Objects.requireNonNull(builder.valueHysteresis);
    this.valueThreshold = Objects.requireNonNull(builder.valueThreshold);
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

  public static class Builder {

    private long minDurationNormalToOver;
    private long minDurationOverToNormal;
    private long timeThreshold;
    private long valueHysteresis;
    private long valueThreshold;

    public ThdConfigurationDto.Builder withMinDurationNormalToOver(
        final long minDurationNormalToOver) {
      this.minDurationNormalToOver = minDurationNormalToOver;
      return this;
    }

    public ThdConfigurationDto.Builder withMinDurationOverToNormal(
        final long minDurationOverToNormal) {
      this.minDurationOverToNormal = minDurationOverToNormal;
      return this;
    }

    public ThdConfigurationDto.Builder withTimeThreshold(final long timeThreshold) {
      this.timeThreshold = timeThreshold;
      return this;
    }

    public ThdConfigurationDto.Builder withValueHysteresis(final long valueHysteresis) {
      this.valueHysteresis = valueHysteresis;
      return this;
    }

    public ThdConfigurationDto.Builder withValueThreshold(final long valueThreshold) {
      this.valueThreshold = valueThreshold;
      return this;
    }

    public ThdConfigurationDto build() {
      return new ThdConfigurationDto(this);
    }
  }
}
