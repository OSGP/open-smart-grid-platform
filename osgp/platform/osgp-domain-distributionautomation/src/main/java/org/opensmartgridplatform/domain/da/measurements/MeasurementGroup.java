/**
 * Copyright 2019 Smart Society Services B.V.
 */
package org.opensmartgridplatform.domain.da.measurements;

import java.util.ArrayList;
import java.util.List;

public class MeasurementGroup {

    private int measurementGroupIdentifier;
    private List<Measurement> measurements;

    private MeasurementGroup(final Builder builder) {
        this.measurementGroupIdentifier = builder.measurementGroupIdentifier;
        this.measurements = builder.measurements;
    }

    public MeasurementGroup(final int measurementGroupIdentifier, final List<Measurement> measurements) {
        this.measurementGroupIdentifier = measurementGroupIdentifier;
        this.measurements = measurements;
    }

    public int getMeasurementGroupIdentifier() {
        return this.measurementGroupIdentifier;
    }

    public List<Measurement> getMeasurements() {
        return this.measurements;
    }

    public static class Builder {
        private int measurementGroupIdentifier;
        private List<Measurement> measurements;

        public Builder(final int measurementGroupIdentifier) {
            this.measurementGroupIdentifier = measurementGroupIdentifier;
        }

        public Builder withMeasurements(final List<Measurement> measurements) {
            if (this.measurements == null) {
                this.measurements = new ArrayList<>();
            }
            this.measurements.addAll(measurements);
            return this;
        }

        public MeasurementGroup build() {
            return new MeasurementGroup(this);
        }
    }

}
