/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects;

import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.opensmartgridplatform.domain.core.valueobjects.CdmaDevice;

public class CdmaMastSegment implements Comparable<CdmaMastSegment> {

    private final String mastSegmentName;
    private SortedMap<Short, CdmaBatch> cdmaBatches;

    public CdmaMastSegment(final CdmaDevice cdmaDevice) {
        this.cdmaBatches = new TreeMap<>();
        this.addCdmaDevice(cdmaDevice);

        this.mastSegmentName = cdmaDevice.getMastSegmentName();
    }

    public void addCdmaDevice(final CdmaDevice cdmaDevice) {
        this.validateCdmaDevice(cdmaDevice);

        final Short batchNumber = cdmaDevice.getBatchNumber();

        final CdmaBatch cdmaBatch = this.cdmaBatches.get(batchNumber);
        if (cdmaBatch == null) {
            final CdmaBatch newBatch = new CdmaBatch(cdmaDevice);
            this.cdmaBatches.put(batchNumber, newBatch);
        } else {
            cdmaBatch.addCdmaDevice(cdmaDevice);
        }
    }

    private void validateCdmaDevice(final CdmaDevice cdmaDevice) {
        if (cdmaDevice == null) {
            throw new IllegalArgumentException("cmdDevice is mandatory, null value is not allowed");
        }

        if (this.getMastSegment() != null && !this.getMastSegment().equals(cdmaDevice.getMastSegmentName())) {
            throw new IllegalArgumentException(
                    "cdmaDevice.mastSegmentName not equal to mastSegmentName of the CdmaMastSegment");
        }
    }

    /**
     * Returns the first (lowest) CdmaBatch of a CdmaMastSegment and removes the
     * CdmaBatch from the CdmaMastSegment.
     *
     * @return when there are CdmaBatches, the first CdmaBatch. Otherwise null.
     */
    public CdmaBatch popCdmaBatch() {
        if (this.cdmaBatches.isEmpty()) {
            return null;
        } else {
            final Short firstKey = this.cdmaBatches.firstKey();
            final CdmaBatch firstBatch = this.cdmaBatches.get(firstKey);
            this.cdmaBatches.remove(firstKey);

            return firstBatch;
        }
    }

    public String getMastSegment() {
        return this.mastSegmentName;
    }

    public boolean empty() {
        return this.cdmaBatches.isEmpty();
    }

    @Override
    public int hashCode() {
        return this.mastSegmentName.hashCode();
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

        return Objects.equals(this.mastSegmentName, other.mastSegmentName);
    }

    @Override
    public String toString() {
        final List<String> batchNumbers = this.cdmaBatches.values().stream().map(x -> x.getBatchNumber().toString())
                .collect(Collectors.toList());

        return "CdmaMastSegment [mastSegmentName=" + this.mastSegmentName + ", cdmaBatches="
                + String.join(",", batchNumbers) + "]";
    }

    @Override
    public int compareTo(final CdmaMastSegment other) {
        if (this.mastSegmentName.equals(CdmaDevice.DEFAULT_MASTSEGMENT)) {
            return this.mastSegmentName.equals(other.mastSegmentName) ? 0 : 1;
        } else if (other.mastSegmentName.equals(CdmaDevice.DEFAULT_MASTSEGMENT)) {
            return -1;
        }

        return this.mastSegmentName.compareTo(other.mastSegmentName);
    }
}
