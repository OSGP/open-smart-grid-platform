/**
 * Copyright 2019 Smart Society Services B.V.
 */
package org.opensmartgridplatform.domain.da.measurements.elements;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;

import org.opensmartgridplatform.domain.da.measurements.MeasurementElement;

public class TimestampMeasurementElement extends MeasurementElement<Long> {

    public TimestampMeasurementElement(final long value) {
        super(value);
    }

    public TimestampMeasurementElement(final ZonedDateTime zonedDateTime) {
        super(zonedDateTime.toInstant().toEpochMilli());
    }

    public ZonedDateTime asZonedDateTime() {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(this.value), ZoneOffset.UTC);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof TimestampMeasurementElement)) {
            return false;
        }
        final TimestampMeasurementElement that = (TimestampMeasurementElement) obj;
        return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

}
