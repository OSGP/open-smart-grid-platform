// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;

@Getter
public class ThdConfiguration implements Serializable {

  private final long minDurationNormalToOver;
  private final long minDurationOverToNormal;
  private final long timeThreshold;
  private final int valueHysteresis;
  private final int valueThreshold;

  public ThdConfiguration(final Builder builder) {

    this.minDurationNormalToOver = Objects.requireNonNull(builder.minDurationNormalToOver);
    this.minDurationOverToNormal = Objects.requireNonNull(builder.minDurationOverToNormal);
    this.timeThreshold = Objects.requireNonNull(builder.timeThreshold);
    this.valueHysteresis = Objects.requireNonNull(builder.valueHysteresis);
    this.valueThreshold = Objects.requireNonNull(builder.valueThreshold);
  }

  public static class Builder {
    private long minDurationNormalToOver;
    private long minDurationOverToNormal;
    private long timeThreshold;
    private int valueHysteresis;
    private int valueThreshold;

    public ThdConfiguration.Builder withMinDurationNormalToOver(
        final long minDurationNormalToOver) {
      this.minDurationNormalToOver = minDurationNormalToOver;
      return this;
    }

    public ThdConfiguration.Builder withMinDurationOverToNormal(
        final long minDurationOverToNormal) {
      this.minDurationOverToNormal = minDurationOverToNormal;
      return this;
    }

    public ThdConfiguration.Builder withTimeThreshold(final long timeThreshold) {
      this.timeThreshold = timeThreshold;
      return this;
    }

    public ThdConfiguration.Builder withValueHysteresis(final int valueHysteresis) {
      this.valueHysteresis = valueHysteresis;
      return this;
    }

    public ThdConfiguration.Builder withValueThreshold(final int valueThreshold) {
      this.valueThreshold = valueThreshold;
      return this;
    }

    public ThdConfiguration build() {
      return new ThdConfiguration(this);
    }
  }
}
