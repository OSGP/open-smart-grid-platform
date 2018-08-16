/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects;

import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import org.opensmartgridplatform.domain.core.valueobjects.CdmaDevice;

public class CdmaMastSegment implements Comparable<CdmaMastSegment> {

    private final String mastSegmentName;
    private SortedMap<Short, CdmaBatch> cdmaBatches;

    public CdmaMastSegment(final CdmaDevice cdmaDevice) {
        this.mastSegmentName = cdmaDevice.getMastSegmentName();

        final CdmaBatch cdmaBatch = new CdmaBatch(cdmaDevice);
        this.cdmaBatches = new TreeMap<>();
        this.cdmaBatches.put(cdmaBatch.getBatchNumber(), cdmaBatch);
    }

    public void addCdmaDevice(final CdmaDevice cdmaDevice) {
        final Short batchNumber = cdmaDevice.getBatchNumber();

        final CdmaBatch cdmaBatch = this.cdmaBatches.get(batchNumber);
        if (cdmaBatch == null) {
            final CdmaBatch newBatch = new CdmaBatch(cdmaDevice);
            this.cdmaBatches.put(batchNumber, newBatch);
        } else {
            cdmaBatch.addCdmaDevice(cdmaDevice);
        }
    }

    public String getMastSegment() {
        return this.mastSegmentName;
    }

    public SortedMap<Short, CdmaBatch> getCdmaBatches() {
        return this.cdmaBatches;
    }

    /* @formatter:off
    public List<CdmaMastSegment> from(final TreeMap<String, TreeMap<Short, List<CdmaDevice>>> rawMastSegments) {
        final List<CdmaMastSegment> cdmaMastSegments = new ArrayList<>();
        for (final Entry<String, TreeMap<Short, List<CdmaDevice>>> rawMastSegment : rawMastSegments.entrySet()) {
            final CdmaMastSegment cdmaMastSegment = new CdmaMastSegment(rawMastSegment.getKey(),
                    rawMastSegment.getValue());
        }

        return cdmaMastSegments;
    }
    * @formatter:on
    */

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
    public int compareTo(final CdmaMastSegment other) {
        return this.mastSegmentName.compareTo(other.mastSegmentName);
    }
}
