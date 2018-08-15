/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.util.List;
import java.util.Objects;

public class CdmaMastSegment implements Comparable<CdmaMastSegment> {

    private final String mastSegment;
    private final List<CdmaDevice> cdmaDevices;

    public CdmaMastSegment(final String mastSegment, final List<CdmaDevice> cdmaDevices) {
        this.mastSegment = mastSegment;
        this.cdmaDevices = cdmaDevices;
    }

    public String getMastSegment() {
        return this.mastSegment;
    }

    @Override
    public int hashCode() {
        return this.mastSegment.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof CdmaMastSegment)) {
            return false;
        }

        final CdmaMastSegment other = (CdmaMastSegment) obj;

        return Objects.equals(this.mastSegment, other.mastSegment);
    }

    @Override
    public int compareTo(final CdmaMastSegment other) {
        return this.mastSegment.compareTo(other.mastSegment);
    }
}
