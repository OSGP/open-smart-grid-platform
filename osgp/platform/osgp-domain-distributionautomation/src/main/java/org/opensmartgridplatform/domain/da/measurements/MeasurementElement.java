/**
 * Copyright 2019 Smart Society Services B.V.
 */
package org.opensmartgridplatform.domain.da.measurements;

public abstract class MeasurementElement<T> {
    protected T value;

    public MeasurementElement(final T value) {
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();
}
