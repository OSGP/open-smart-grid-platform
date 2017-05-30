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
 * in the smart_meter table.
 *
 *
 */
public class MbusChannelElementsDto implements Serializable {

    private static final long serialVersionUID = 5377631203726277889L;

    /**
     * this is the name of the mbus-device that is given in the request
     */
    private final String mbusDeviceIdentification;

    /**
     * these are the properties that belong to this mbus-device in the core dbs
     * (smartmeter table)
     */
    private final String mbusIdentificationNumber;
    private final String mbusManufacturerIdentification;
    private final String mbusVersion;
    private final String mbusDeviceTypeIdentification;

    public MbusChannelElementsDto(final String mbusDeviceIdentification, final String mbusIdentificationNumber,
            final String mbusManufacturerIdentification, final String mbusVersion,
            final String mbusDeviceTypeIdentification) {
        super();
        this.mbusDeviceIdentification = mbusDeviceIdentification;
        this.mbusIdentificationNumber = mbusIdentificationNumber;
        this.mbusManufacturerIdentification = mbusManufacturerIdentification;
        this.mbusVersion = mbusVersion;
        this.mbusDeviceTypeIdentification = mbusDeviceTypeIdentification;
    }

    public String getMbusDeviceIdentification() {
        return this.mbusDeviceIdentification;
    }

    public String getMbusIdentificationNumber() {
        return this.mbusIdentificationNumber;
    }

    public String getMbusManufacturerIdentification() {
        return this.mbusManufacturerIdentification;
    }

    public String getMbusVersion() {
        return this.mbusVersion;
    }

    public String getMbusDeviceTypeIdentification() {
        return this.mbusDeviceTypeIdentification;
    }

}