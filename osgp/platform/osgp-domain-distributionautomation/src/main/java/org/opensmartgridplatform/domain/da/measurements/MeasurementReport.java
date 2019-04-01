/**
 * Copyright 2019 Smart Society Services B.V.
 */
package org.opensmartgridplatform.domain.da.measurements;

import java.util.List;

public class MeasurementReport {

    private MeasurementReportHeader measurementReportHeader;
    private List<MeasurementGroup> measurementGroups;

    private MeasurementReport(final Builder builder) {
        this.measurementReportHeader = builder.measurementReportHeader;
        this.measurementGroups = builder.measurementGroups;
    }

    public MeasurementReport(final MeasurementReportHeader header, final List<MeasurementGroup> measurementGroups) {
        this.measurementReportHeader = header;
        this.measurementGroups = measurementGroups;
    }

    public MeasurementReportHeader getHeader() {
        return this.measurementReportHeader;
    }

    public List<MeasurementGroup> getMeasurementGroups() {
        return this.measurementGroups;
    }

    public static class Builder {
        private MeasurementReportHeader measurementReportHeader;
        private List<MeasurementGroup> measurementGroups;

        public Builder withMeasurementReportHeader(final MeasurementReportHeader measurementReportHeader) {
            this.measurementReportHeader = measurementReportHeader;
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
