// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.da.measurements;

import java.io.Serializable;
import java.util.Objects;

public class MeasurementReportHeaderDto implements Serializable {

  private static final long serialVersionUID = 1408641961558265027L;

  private final String measurementType;
  private final String reason;
  private final int originatorAddress;
  private final int commonAddress;

  public MeasurementReportHeaderDto(
      final String measurementType,
      final String reason,
      final int originatorAddress,
      final int commonAddress) {
    this.measurementType = measurementType;
    this.reason = reason;
    this.originatorAddress = originatorAddress;
    this.commonAddress = commonAddress;
  }

  public String getMeasurementType() {
    return this.measurementType;
  }

  public String getReason() {
    return this.reason;
  }

  public int getCommonAddress() {
    return this.commonAddress;
  }

  public int getOriginatorAddress() {
    return this.originatorAddress;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof MeasurementReportHeaderDto)) {
      return false;
    }
    final MeasurementReportHeaderDto that = (MeasurementReportHeaderDto) obj;
    return Objects.equals(this.measurementType, that.measurementType)
        && Objects.equals(this.reason, that.reason)
        && Objects.equals(this.originatorAddress, that.originatorAddress)
        && Objects.equals(this.commonAddress, that.commonAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        this.measurementType, this.reason, this.originatorAddress, this.commonAddress);
  }

  @Override
  public String toString() {
    return "MeasurementReportHeaderDto [measurementType="
        + this.measurementType
        + ", reason="
        + this.reason
        + ", originatorAddress="
        + this.originatorAddress
        + ", commonAddress="
        + this.commonAddress
        + "]";
  }
}
