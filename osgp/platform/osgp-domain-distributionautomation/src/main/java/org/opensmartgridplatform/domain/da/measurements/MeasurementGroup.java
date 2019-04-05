/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.da.measurements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MeasurementGroup {

    private int measurementGroupIdentifier;
    private List<Measurement> measurements;

    public MeasurementGroup(final int measurementGroupIdentifier, final List<Measurement> measurements) {
        this.measurementGroupIdentifier = measurementGroupIdentifier;
        this.measurements = new ArrayList<>(measurements);
    }

    public int getMeasurementGroupIdentifier() {
        return this.measurementGroupIdentifier;
    }

    public List<Measurement> getMeasurements() {
        return this.measurements;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof MeasurementGroup)) {
            return false;
        }

        final MeasurementGroup that = (MeasurementGroup) obj;
        return Objects.equals(this.measurementGroupIdentifier, that.measurementGroupIdentifier)
                && Objects.equals(this.measurements, that.measurements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.measurementGroupIdentifier, this.measurements);
    }
}
