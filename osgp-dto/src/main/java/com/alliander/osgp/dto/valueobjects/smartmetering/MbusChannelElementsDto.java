/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class MbusChannelElementsDto implements Serializable {

    private static final long serialVersionUID = 5377631203726277889L;

    private String mbusIdentificationNumber;
    private String mbusManufacturerIdentification;
    private String mbusVersion;
    private String mbusDeviceTypeIdentification;

    public MbusChannelElementsDto(final String mbusIdentificationNumber, final String mbusManufacturerIdentification,
            final String mbusVersion, final String mbusDeviceTypeIdentification) {
        super();
        this.mbusIdentificationNumber = mbusIdentificationNumber;
        this.mbusManufacturerIdentification = mbusManufacturerIdentification;
        this.mbusVersion = mbusVersion;
        this.mbusDeviceTypeIdentification = mbusDeviceTypeIdentification;
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