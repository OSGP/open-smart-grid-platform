/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

public class GetMBusEncryptionKeyStatusRequestDto implements ActionRequestDto {

    private static final long serialVersionUID = 3432576706197401825L;

    private String mBusDeviceIdentification;
    private Short channel;

    public GetMBusEncryptionKeyStatusRequestDto(final String mBusDeviceIdentification, final Short channel) {
        this.mBusDeviceIdentification = mBusDeviceIdentification;
        this.channel = channel;
    }

    public String getMBusDeviceIdentification() {
        return this.mBusDeviceIdentification;
    }

    public Short getChannel() {
        return this.channel;
    }
}
