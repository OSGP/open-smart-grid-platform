/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

public class CdmaMastSegment implements Comparable<CdmaMastSegment> {

    private final String mastSegment;
    private List<CdmaDevice> cdmaDevices;
    private TreeMap<Short, List<CdmaDevice>> rawCdmaBatches;

    public CdmaMastSegment(final String mastSegment, final List<CdmaDevice> cdmaDevices) {
        this.mastSegment = mastSegment;
        this.cdmaDevices = cdmaDevices;
    }

    public CdmaMastSegment(final String mastSegment, final TreeMap<Short, List<CdmaDevice>> rawCdmaBatches) {
        this.mastSegment = mastSegment;
        this.rawCdmaBatches = rawCdmaBatches;
    }

    public String getMastSegment() {
        return this.mastSegment;
    }

    public List<CdmaMastSegment> from(final TreeMap<String, TreeMap<Short, List<CdmaDevice>>> rawMastSegments) {
        final List<CdmaMastSegment> cdmaMastSegments = new ArrayList<>();
        for (final Entry<String, TreeMap<Short, List<CdmaDevice>>> rawMastSegment : rawMastSegments.entrySet()) {
            final CdmaMastSegment cdmaMastSegment = new CdmaMastSegment(rawMastSegment.getKey(),
                    rawMastSegment.getValue());
        }

        return cdmaMastSegments;
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
