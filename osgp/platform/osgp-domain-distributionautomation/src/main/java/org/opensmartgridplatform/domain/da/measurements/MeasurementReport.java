/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.da.measurements;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class MeasurementReport implements Serializable {

  private static final long serialVersionUID = 1L;

  private MeasurementReportHeader reportHeader;
  private List<MeasurementGroup> measurementGroups;

  private MeasurementReport(final Builder builder) {
    this.reportHeader = builder.reportHeader;
    this.measurementGroups = builder.measurementGroups;
  }

  public MeasurementReport(
      final MeasurementReportHeader reportHeader, final List<MeasurementGroup> measurementGroups) {
    this.reportHeader = reportHeader;
    this.measurementGroups = measurementGroups;
  }

  public MeasurementReportHeader getReportHeader() {
    return this.reportHeader;
  }

  public List<MeasurementGroup> getMeasurementGroups() {
    return this.measurementGroups;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof MeasurementReport)) {
      return false;
    }

    final MeasurementReport that = (MeasurementReport) obj;
    return Objects.equals(this.reportHeader, that.reportHeader)
        && Objects.equals(this.measurementGroups, that.measurementGroups);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.reportHeader, this.measurementGroups);
  }

  @Override
  public String toString() {
    return "MeasurementReport [reportHeader="
        + this.reportHeader
        + ", measurementGroups="
        + this.measurementGroups
        + "]";
  }

  public static class Builder {
    private MeasurementReportHeader reportHeader;
    private List<MeasurementGroup> measurementGroups;

    public Builder withMeasurementReportHeader(final MeasurementReportHeader reportHeader) {
      this.reportHeader = reportHeader;
      return this;
    }

    public Builder withMeasurementGroups(final List<MeasurementGroup> measurementGroups) {
      this.measurementGroups = measurementGroups;
      return this;
    }

    public MeasurementReport build() {
      return new MeasurementReport(this);
    }
  }
}
