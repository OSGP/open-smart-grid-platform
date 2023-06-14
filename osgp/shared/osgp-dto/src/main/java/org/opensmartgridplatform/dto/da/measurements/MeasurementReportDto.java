// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.da.measurements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MeasurementReportDto implements Serializable {

  private static final long serialVersionUID = -8043065228864074341L;

  private final MeasurementReportHeaderDto reportHeader;
  private final List<MeasurementGroupDto> measurementGroups;

  public MeasurementReportDto(
      final MeasurementReportHeaderDto reportHeader,
      final List<MeasurementGroupDto> measurementGroups) {
    this.reportHeader = reportHeader;
    this.measurementGroups = new ArrayList<>(measurementGroups);
  }

  public MeasurementReportHeaderDto getReportHeader() {
    return this.reportHeader;
  }

  public List<MeasurementGroupDto> getMeasurementGroups() {
    return Collections.unmodifiableList(this.measurementGroups);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof MeasurementReportDto)) {
      return false;
    }

    final MeasurementReportDto that = (MeasurementReportDto) obj;
    return Objects.equals(this.reportHeader, that.reportHeader)
        && Objects.equals(this.measurementGroups, that.measurementGroups);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.reportHeader, this.measurementGroups);
  }

  @Override
  public String toString() {
    return "MeasurementReportDto [reportHeader="
        + this.reportHeader
        + ", measurementGroups="
        + this.measurementGroups
        + "]";
  }
}
