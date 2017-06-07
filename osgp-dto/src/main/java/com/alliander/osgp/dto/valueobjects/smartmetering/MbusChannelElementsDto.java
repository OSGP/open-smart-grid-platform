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
 * This Dto is used to transfer information from the core to the protocol
 * adapter. The values are populated with corresponding fields from the gasmeter
 * in the smart_meter table. In the protocol adapter these values can then be
 * compared with the correspondong field that are read from the device.
 */
public class MbusChannelElementsDto implements Serializable {

    private static final long serialVersionUID = 5377631203726277889L;

    /**
     * this is the name of the mbus-device that is given in the request
     */
    private final String mbusDeviceIdentification;

    /**
     * These are the properties that belong to this mbus-device in the core dbs
     * (smartmeter table).
     */
    private final Long mbusIdentificationNumber;
    private final String mbusManufacturerIdentification;
    private final Short mbusVersion;
    private final Short mbusDeviceTypeIdentification;

    public MbusChannelElementsDto(final String mbusDeviceIdentification, final Long mbusIdentificationNumber,
            final String mbusManufacturerIdentification, final Short mbusVersion,
            final Short mbusDeviceTypeIdentification) {
        this.mbusDeviceIdentification = mbusDeviceIdentification;
        this.mbusIdentificationNumber = mbusIdentificationNumber;
        this.mbusManufacturerIdentification = mbusManufacturerIdentification;
        this.mbusVersion = mbusVersion;
        this.mbusDeviceTypeIdentification = mbusDeviceTypeIdentification;
    }

    public String getMbusDeviceIdentification() {
        return this.mbusDeviceIdentification;
    }

    public Long getMbusIdentificationNumber() {
        return this.mbusIdentificationNumber;
    }

    public String getMbusManufacturerIdentification() {
        return this.mbusManufacturerIdentification;
    }

    public Short getMbusVersion() {
        return this.mbusVersion;
    }

    public Short getMbusDeviceTypeIdentification() {
        return this.mbusDeviceTypeIdentification;
    }

}