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
import java.util.Objects;

import org.opensmartgridplatform.domain.core.valueobjects.CdmaDevice;

public class CdmaBatch implements Comparable<CdmaBatch> {

    private Short batchNumber;
    private List<CdmaBatchDevice> cdmaBatchDevices;

    public CdmaBatch(final CdmaDevice cdmaDevice) {
        this.cdmaBatchDevices = new ArrayList<>();
        this.addCdmaDevice(cdmaDevice);

        this.batchNumber = cdmaDevice.getBatchNumber();
    }

    public void addCdmaDevice(final CdmaDevice cdmaDevice) {
        this.validateCdmaDevice(cdmaDevice);

        final CdmaBatchDevice cdmaBatchDevice = new CdmaBatchDevice(cdmaDevice);
        this.cdmaBatchDevices.add(cdmaBatchDevice);
    }

    private void validateCdmaDevice(final CdmaDevice cdmaDevice) {
        if (cdmaDevice == null) {
            throw new IllegalArgumentException("cmdDevice is mandatory, null value is not allowed");
        }

        if (this.getBatchNumber() != null && !this.getBatchNumber().equals(cdmaDevice.getBatchNumber())) {
            throw new IllegalArgumentException("cdmaDevice.batchNumber not equal to batchNumber of the CdmaBatch");
        }
    }

    public Short getBatchNumber() {
        return this.batchNumber;
    }

    public List<CdmaBatchDevice> getCdmaBatchDevices() {
        return this.cdmaBatchDevices;
    }

    @Override
    public int hashCode() {
        return this.batchNumber.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof CdmaBatch)) {
            return false;
        }

        final CdmaBatch other = (CdmaBatch) obj;

        return Objects.equals(this.batchNumber, other.batchNumber);
    }

    @Override
    public String toString() {
        return "CdmaBatch [batchNumber=" + this.batchNumber + ", cdmaBatchDevices=" + this.cdmaBatchDevices + "]";
    }

    @Override
    public int compareTo(final CdmaBatch other) {
        return this.batchNumber.compareTo(other.batchNumber);
    }
}
