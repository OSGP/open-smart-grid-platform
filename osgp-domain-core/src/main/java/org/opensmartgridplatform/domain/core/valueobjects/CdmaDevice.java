/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.net.InetAddress;
import java.util.Objects;

public class CdmaDevice {

    private static final Short MAX_BATCH_NUMBER = (short) 99;
    public static final String DEFAULT_MASTSEGMENT = "1DEVICE-WITHOUT-MASTSEGMENT";

    private String deviceIdentification;
    private InetAddress networkAddress;
    private String mastSegmentName;
    private Short batchNumber;

    public CdmaDevice(final String deviceIdentification, final InetAddress networkAddress, final String mastSegmentName,
            final Short batchNumber) {
        this.deviceIdentification = deviceIdentification;
        this.networkAddress = networkAddress;
        this.mastSegmentName = mastSegmentName == null ? DEFAULT_MASTSEGMENT : mastSegmentName;
        this.batchNumber = batchNumber == null ? MAX_BATCH_NUMBER : batchNumber;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public InetAddress getNetworkAddress() {
        return this.networkAddress;
    }

    public String getMastSegmentName() {
        return this.mastSegmentName;
    }

    public Short getBatchNumber() {
        return this.batchNumber;
    }

    /*
     * public CdmaDevice mapEmptyFields() { if
     * (StringUtils.isNotEmpty(this.mastSegmentName) && this.batchNumber !=
     * null) { return this; }
     *
     * final Short newBatchNumber = this.batchNumber == null ? MAX_BATCH_NUMBER
     * : this.batchNumber; final String newMastSegmentName =
     * StringUtils.isEmpty(this.mastSegmentName) ? DEFAULT_MASTSEGMENT :
     * this.mastSegmentName; return new CdmaDevice(this.deviceIdentification,
     * this.networkAddress, newMastSegmentName, newBatchNumber); }
     */

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
                + this.networkAddress + ", mastSegmentName=" + this.mastSegmentName + ", batchNumber="
                + this.batchNumber + "]";
    }
}
