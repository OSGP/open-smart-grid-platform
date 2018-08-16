/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects;

import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.NotImplementedException;
import org.opensmartgridplatform.domain.core.valueobjects.CdmaDevice;

public class CdmaRun {

    private SortedMap<String, CdmaMastSegment> mastSegments;
    // private List<CdmaBatch> batchesWithoutMastSegment;

    public CdmaRun() {
        this.mastSegments = new TreeMap<>();
        // batchesWithoutMastSegment = new ArrayList<>();
    }

    /*
     * public CdmaRun(final List<CdmaMastSegment> mastSegments, final
     * List<CdmaBatch> batchesWithoutMastSegment) { this.mastSegments =
     * mastSegments; this.batchesWithoutMastSegment = batchesWithoutMastSegment;
     * }
     */

    public void add(final CdmaDevice cdmaDevice) {
        final String mastSegmentName = cdmaDevice.getMastSegmentName();

        final CdmaMastSegment mastSegment = this.mastSegments.get(cdmaDevice.getMastSegmentName());
        if (mastSegment == null) {
            final CdmaMastSegment newMastSegment = new CdmaMastSegment(cdmaDevice);
            this.mastSegments.put(mastSegmentName, newMastSegment);
        } else {
            mastSegment.addCdmaDevice(cdmaDevice);
        }
    }

    public void combine(final CdmaRun otherCdmaRun) {
        // TO DO: haal alle losse cdma devices uit de andere run en voeg die
        // toe, liefst als set/lijst i.p.v. losse devices.
        throw new NotImplementedException("Wordt deze aangeroepen? Ik moet deze nog maken!");
    }

    public SortedMap<String, CdmaMastSegment> getMastSegments() {
        return this.mastSegments;
    }

    // public List<CdmaBatch> getBatchesWithoutMastSegment() {
    // return this.batchesWithoutMastSegment;
    // }
}
