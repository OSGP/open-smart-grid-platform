/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.dto.valueobjects.smartmetering;

/**
 * a data transfer object for the request of coupling a m-bus device
 */
public class CoupleMbusDeviceRequestDto implements ActionRequestDto {

    private static final long serialVersionUID = 8904946214580868649L;

    private String mbusDeviceIdentification;

    private short channel;

    /**
     * @param mbusDeviceIdentification
     *            the mbus device that needs to be coupled to the device in the
     *            metadata information of the request
     * @param channel
     *            the channel on which the mbus device needs to be coupled
     */
    public CoupleMbusDeviceRequestDto(final String mbusDeviceIdentification, final short channel) {
        super();
        this.mbusDeviceIdentification = mbusDeviceIdentification;
        this.channel = channel;
    }

    public String getMbusDeviceIdentification() {
        return this.mbusDeviceIdentification;
    }

    public short getChannel() {
        return this.channel;
    }

}
