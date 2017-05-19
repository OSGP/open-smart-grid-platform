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
 *
 *
 */
public class ChannelElementValues implements Serializable {

    private static final long serialVersionUID = 5377631203726277889L;

    private int channel;
    // these values correspond with attr 6..9 that are read from the meter
    private String identificationNumber;
    private String manufacturerIdentification;
    private String version;
    private String deviceTypeIdentification;

    public ChannelElementValues(final int channel, final String identificationNumber,
            final String manufacturerIdentification, final String version, final String deviceTypeIdentification) {
        super();
        this.channel = channel;
        this.identificationNumber = identificationNumber;
        this.manufacturerIdentification = manufacturerIdentification;
        this.version = version;
        this.deviceTypeIdentification = deviceTypeIdentification;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public int getChannel() {
        return this.channel;
    }

    public String getIdentificationNumber() {
        return this.identificationNumber;
    }

    public String getManufacturerIdentification() {
        return this.manufacturerIdentification;
    }

    public String getVersion() {
        return this.version;
    }

    public String getDeviceTypeIdentification() {
        return this.deviceTypeIdentification;
    }

}