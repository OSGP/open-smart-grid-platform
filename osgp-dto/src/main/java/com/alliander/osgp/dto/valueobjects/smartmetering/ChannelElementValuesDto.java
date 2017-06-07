/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

/**
 * This Dto is used to transfer information from the the protocol adapter back
 * to the core. It contains the original request values, plus the values it
 * found on the e-meter in the smart_meter table.
 */
public class ChannelElementValuesDto implements Serializable {

    private static final long serialVersionUID = 5377631203726277889L;

    private final short channel;
    /**
     * These values correspond with attributes 5..9 that are read from the meter
     * with classid 72 and obiscode 1-X:24.0.1.255.
     */
    private final short primaryAddress;
    private final int identificationNumber;
    private final int manufacturerIdentification;
    private final short version;
    private final short deviceTypeIdentification;

    public ChannelElementValuesDto(final short channel, final short primaryAddress, final int identificationNumber,
            final int manufacturerIdentification, final short version, final short deviceTypeIdentification) {
        this.channel = channel;
        this.primaryAddress = primaryAddress;
        this.identificationNumber = identificationNumber;
        this.manufacturerIdentification = manufacturerIdentification;
        this.version = version;
        this.deviceTypeIdentification = deviceTypeIdentification;
    }

    public int getChannel() {
        return this.channel;
    }

    public short getPrimaryAddress() {
        return this.primaryAddress;
    }

    public int getIdentificationNumber() {
        return this.identificationNumber;
    }

    public int getManufacturerIdentification() {
        return this.manufacturerIdentification;
    }

    public short getVersion() {
        return this.version;
    }

    public short getDeviceTypeIdentification() {
        return this.deviceTypeIdentification;
    }

    public boolean hasIdentificationNumber() {
        return this.identificationNumber > 0;
    }

    public boolean hasManufacturerIdentification() {
        return this.manufacturerIdentification > 0;
    }

    public boolean hasVersion() {
        return this.version > 0;
    }

    public boolean hasDeviceTypeIdentification() {
        return this.deviceTypeIdentification > 0;
    }

    @Override
    public String toString() {
        return "ChannelElementValues [channel=" + this.channel + ", primaryAddress=" + this.primaryAddress
                + ", identificationNumber=" + this.identificationNumber + ", manufacturerIdentification="
                + this.manufacturerIdentification + ", version=" + this.version + ", deviceTypeIdentification="
                + this.deviceTypeIdentification + "]";
    }
}