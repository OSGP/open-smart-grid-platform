/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.valueobjects;

import java.net.InetAddress;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public class CdmaDevice {

    private static final Short MAX_BATCH_NUMBER = (short) 99;
    private static final String DEFAULT_MASTSEGMENT = "DEVICE-WITHOUT-MASTSEGMENT";

    private String deviceIdentification;
    private InetAddress networkAddress;
    private String mastSegment;
    private Short batchNumber;

    public CdmaDevice(final String deviceIdentification, final InetAddress networkAddress, final String mastSegment) {
        this(deviceIdentification, networkAddress, mastSegment, (short) 1);
    }

    public CdmaDevice(final String deviceIdentification, final InetAddress networkAddress, final String mastSegment,
            final Short batchNumber) {
        this.deviceIdentification = deviceIdentification;
        this.networkAddress = networkAddress;
        this.mastSegment = mastSegment;
        this.batchNumber = batchNumber;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public InetAddress getNetworkAddress() {
        return this.networkAddress;
    }

    public String getMastSegment() {
        return this.mastSegment;
    }

    public boolean hasDefaultMastSegment() {
        return DEFAULT_MASTSEGMENT.equals(this.mastSegment);
    }

    public Short getBatchNumber() {
        return this.batchNumber;
    }

    public CdmaDevice mapEmptyFields() {
        if (StringUtils.isNotEmpty(this.mastSegment) && this.batchNumber != null) {
            return this;
        }

        final Short newBatchNumber = this.batchNumber == null ? MAX_BATCH_NUMBER : this.batchNumber;
        final String newMastSegment = StringUtils.isEmpty(this.mastSegment) ? DEFAULT_MASTSEGMENT : this.mastSegment;
        return new CdmaDevice(this.deviceIdentification, this.networkAddress, newMastSegment, newBatchNumber);
    }

    @Override
    public int hashCode() {
        return this.deviceIdentification.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof CdmaDevice)) {
            return false;
        }

        final CdmaDevice other = (CdmaDevice) obj;

        return Objects.equals(this.deviceIdentification, other.deviceIdentification);
    }

    public int getLength() {
        return this.deviceIdentification.length();
    }

    @Override
    public String toString() {
        return "CdmaBatchDevice [deviceIdentification=" + this.deviceIdentification + ", networkAddress="
                + this.networkAddress + ", mastSegment=" + this.mastSegment + ", batchNumber=" + this.batchNumber + "]";
    }
}
