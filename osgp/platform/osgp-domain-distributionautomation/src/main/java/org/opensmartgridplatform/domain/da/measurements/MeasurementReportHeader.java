//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.da.measurements;

import com.google.common.base.Objects;
import java.io.Serializable;

public class MeasurementReportHeader implements Serializable {

  private static final long serialVersionUID = 1L;

  private MeasurementType measurementType;
  private ReasonType reasonType;
  private int originatorAddress;
  private int commonAddress;

  public MeasurementReportHeader(
      final MeasurementType measurementType,
      final ReasonType reasonType,
      final int originatorAddress,
      final int commonAddress) {
    // Constructor for mapping by Orika
    this.measurementType = measurementType;
    this.reasonType = reasonType;
    this.originatorAddress = originatorAddress;
    this.commonAddress = commonAddress;
  }

  private MeasurementReportHeader(final Builder builder) {
    this.measurementType = builder.measurementType;
    this.reasonType = builder.reasonType;
    this.originatorAddress = builder.originatorAddress;
    this.commonAddress = builder.commonAddress;
  }

  public MeasurementType getMeasurementType() {
    return this.measurementType;
  }

  public ReasonType getReasonType() {
    return this.reasonType;
  }

  public int getOriginatorAddress() {
    return this.originatorAddress;
  }

  public int getCommonAddress() {
    return this.commonAddress;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof MeasurementReportHeader)) {
      return false;
    }
    final MeasurementReportHeader that = (MeasurementReportHeader) obj;
    return Objects.equal(this.measurementType, that.measurementType)
        && Objects.equal(this.reasonType, that.reasonType)
        && Objects.equal(this.originatorAddress, that.originatorAddress)
        && Objects.equal(this.commonAddress, that.commonAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        this.measurementType, this.reasonType, this.originatorAddress, this.commonAddress);
  }

  @Override
  public String toString() {
    return "MeasurementReportHeader [measurementType="
        + this.measurementType
        + ", reasonType="
        + this.reasonType
        + ", originatorAddress="
        + this.originatorAddress
        + ", commonAddress="
        + this.commonAddress
        + "]";
  }

  public static class Builder {
    private MeasurementType measurementType;
    private ReasonType reasonType;
    private int originatorAddress;
    private int commonAddress;

    public Builder withMeasurementType(final MeasurementType measurementType) {
      this.measurementType = measurementType;
      return this;
    }

    public Builder withReasonType(final ReasonType reasonType) {
      this.reasonType = reasonType;
      return this;
    }

    public Builder withOriginatorAddress(final int originatorAddress) {
      this.originatorAddress = originatorAddress;
      return this;
    }

    public Builder withCommonAddress(final int commonAddress) {
      this.commonAddress = commonAddress;
      return this;
    }

    public MeasurementReportHeader build() {
      return new MeasurementReportHeader(this);
    }
  }
}
