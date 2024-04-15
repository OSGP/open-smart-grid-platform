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
  private final int valueHysteresis;
  private final int valueThreshold;

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

  public static class Builder {

    private long minDurationNormalToOver;
    private long minDurationOverToNormal;
    private long timeThreshold;
    private int valueHysteresis;
    private int valueThreshold;

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

    public ThdConfigurationDto.Builder withValueHysteresis(final int valueHysteresis) {
      this.valueHysteresis = valueHysteresis;
      return this;
    }

    public ThdConfigurationDto.Builder withValueThreshold(final int valueThreshold) {
      this.valueThreshold = valueThreshold;
      return this;
    }

    public ThdConfigurationDto build() {
      return new ThdConfigurationDto(this);
    }
  }
}
