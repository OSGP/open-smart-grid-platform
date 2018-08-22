/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects;

import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.opensmartgridplatform.domain.core.valueobjects.CdmaDevice;

public class CdmaRun {

    private SortedMap<String, CdmaMastSegment> mastSegments;

    public CdmaRun() {
        this.mastSegments = new TreeMap<>();
    }

    public void add(final CdmaDevice cdmaDevice) {
        String mastSegmentName = cdmaDevice.getMastSegmentName();
        if (mastSegmentName == null) {
            mastSegmentName = CdmaDevice.DEFAULT_MASTSEGMENT;
        }

        final CdmaMastSegment mastSegment = this.mastSegments.get(mastSegmentName);
        if (mastSegment == null) {
            final CdmaMastSegment newMastSegment = new CdmaMastSegment(cdmaDevice);
            this.mastSegments.put(mastSegmentName, newMastSegment);
        } else {
            mastSegment.addCdmaDevice(cdmaDevice);
        }
    }

    public Iterator<CdmaMastSegment> getMastSegmentIterator() {
        return this.mastSegments.values().iterator();
    }

    @Override
    public String toString() {
        final List<String> segmentNames = this.mastSegments.values().stream().map(CdmaMastSegment::getMastSegment)
                .collect(Collectors.toList());
        return "CdmaRun [mastSegments=" + String.join(",", segmentNames) + "]";
    }
}
