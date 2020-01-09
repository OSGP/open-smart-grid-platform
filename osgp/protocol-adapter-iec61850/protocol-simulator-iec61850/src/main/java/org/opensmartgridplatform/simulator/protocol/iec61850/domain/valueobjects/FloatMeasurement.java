package org.opensmartgridplatform.simulator.protocol.iec61850.domain.valueobjects;

import java.time.ZonedDateTime;
import java.util.Objects;

public class FloatMeasurement {
    private final ZonedDateTime timestamp;
    private final float value;

    public FloatMeasurement(final ZonedDateTime timestamp, final float value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public ZonedDateTime getTimestamp() {
        return this.timestamp;
    }

    public float getValue() {
        return this.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.timestamp, this.value);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FloatMeasurement)) {
            return false;
        }
        final FloatMeasurement other = (FloatMeasurement) obj;

        return Objects.equals(this.timestamp, other.timestamp)
                && Objects.equals(Float.floatToIntBits(this.value), Float.floatToIntBits(other.value));
    }

    @Override
    public String toString() {
        return "FloatMeasurement [timestamp=" + this.timestamp + ", value=" + this.value + "]";
    }
}
