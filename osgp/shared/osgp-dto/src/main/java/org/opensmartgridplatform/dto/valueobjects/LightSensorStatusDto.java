/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects;

import java.io.Serializable;
import java.util.Objects;

public class LightSensorStatusDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private final boolean on;

    public LightSensorStatusDto(final boolean on) {
        this.on = on;
    }

    public boolean isOn() {
        return this.on;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LightSensorStatusDto)) {
            return false;
        }
        final LightSensorStatusDto other = (LightSensorStatusDto) obj;
        return this.on == other.on;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.on);
    }

    @Override
    public String toString() {
        return "LightSensorStatusDto [on=" + this.on + "]";
    }
}
