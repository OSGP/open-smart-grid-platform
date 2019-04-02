/**
 * Copyright 2019 Smart Society Services B.V.
 */
package org.opensmartgridplatform.domain.da.measurements.elements;

import java.util.Objects;

import org.opensmartgridplatform.domain.da.measurements.MeasurementElement;

public class FloatingPointMeasurementElement implements MeasurementElement {

    private Float value;

    public FloatingPointMeasurementElement(final Float value) {
        this.value = value;
    }

    public Float getValue() {
        return this.value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FloatingPointMeasurementElement)) {
            return false;
        }
        final FloatingPointMeasurementElement that = (FloatingPointMeasurementElement) obj;
        return Float.compare(this.value, that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }
}
