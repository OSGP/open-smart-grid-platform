/**
 * Copyright 2019 Smart Society Services B.V.
 */
package org.opensmartgridplatform.domain.da.measurements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Measurement {
    private List<MeasurementElement> measurementElements = new ArrayList<>();

    public Measurement(final List<MeasurementElement> measurementElements) {
        this.measurementElements = new ArrayList<>(measurementElements);
    }

    public List<MeasurementElement> getMeasurementElements() {
        return new ArrayList<>(this.measurementElements);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Measurement)) {
            return false;
        }

        final Measurement that = (Measurement) obj;
        return Objects.equals(this.measurementElements, that.measurementElements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.measurementElements);
    }
}