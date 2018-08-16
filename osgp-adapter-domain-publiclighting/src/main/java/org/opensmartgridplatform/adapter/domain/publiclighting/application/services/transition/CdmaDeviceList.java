/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.services.transition;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects.CdmaDevice;

import com.google.common.base.Objects;

public class CdmaDeviceList implements Comparable<CdmaDeviceList> {

    private final String mastSegment;
    private final Short batchNumber;
    private final List<CdmaDevice> devices;
    final int iteration;

    public CdmaDeviceList(final List<CdmaDevice> devices, final int iteration) {
        if (devices.isEmpty()) {
            this.mastSegment = null;
            this.batchNumber = null;
        } else {
            final CdmaDevice firstDevice = devices.get(0);
            this.mastSegment = firstDevice.getMastSegment();
            this.batchNumber = firstDevice.getBatchNumber();
        }
        this.iteration = iteration;
        this.devices = devices;
    }

    public List<CdmaDevice> getDevices() {
        return this.devices;
    }

    public int getSeries() {
        return this.iteration;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.iteration, this.mastSegment, this.batchNumber);
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof CdmaDeviceList)) {
            return false;
        }

        final CdmaDeviceList other = (CdmaDeviceList) obj;

        return this.iteration == other.iteration && Objects.equal(this.mastSegment, other.mastSegment)
                && Objects.equal(this.batchNumber, other.batchNumber);
    }

    @Override
    public int compareTo(final CdmaDeviceList other) {

        final int compareIteration = this.iteration - other.iteration;
        if (compareIteration != 0) {
            return compareIteration;
        }

        final int compareMastSegment = ObjectUtils.compare(this.mastSegment, other.mastSegment);
        if (compareMastSegment != 0) {
            return compareMastSegment;
        }

        return ObjectUtils.compare(this.batchNumber, other.batchNumber);
    }
}
