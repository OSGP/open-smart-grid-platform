/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

public class DeCoupleMbusDeviceByChannelResponseDto extends ActionResponseDto {

    private static final long serialVersionUID = -4454979905929290745L;

    private final String mBusDeviceIdentification;
    private final Short channel;

    public DeCoupleMbusDeviceByChannelResponseDto(final String mBusDeviceIdentification, final Short channel) {
        this.mBusDeviceIdentification = mBusDeviceIdentification;
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "DeCoupleMbusDeviceByChannelResponseDto [mBusDeviceIdentification="
                + this.mBusDeviceIdentification + "]";
    }

    public String getmBusDeviceIdentification() {
        return this.mBusDeviceIdentification;
    }

    public Short getChannel() {
        return channel;
    }
}
