/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

public class CoupleMbusDeviceByChannelResponseDto extends ActionResponseDto {

    private static final long serialVersionUID = -6470713070003127394L;

    private String mbusDeviceIdentification;
    private ChannelElementValuesDto channelElementValues;

    public CoupleMbusDeviceByChannelResponseDto(final String mbusDeviceIdentification,
            final ChannelElementValuesDto channelElementValues) {
        super();
        this.mbusDeviceIdentification = mbusDeviceIdentification;
        this.channelElementValues = channelElementValues;
    }

    public String getMbusDeviceIdentification() {
        return this.mbusDeviceIdentification;
    }

    public ChannelElementValuesDto getChannelElementValues() {
        return this.channelElementValues;
    }

}
