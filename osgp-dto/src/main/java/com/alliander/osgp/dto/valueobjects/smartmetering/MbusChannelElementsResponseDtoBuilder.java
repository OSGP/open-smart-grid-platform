/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MbusChannelElementsResponseDtoBuilder implements Serializable {

    private static final long serialVersionUID = 5377631203726277880L;

    /**
     * This contains the channel on which the mbus-device is connected. It is
     * null if NO channel is found that matches the original values from the
     * request dto see also isChannelFound()
     */
    private Integer channel;

    /**
     * this contains the values read from the e-meter until a match is found. if
     * no channel is found, it will contain 4 entries
     */
    private List<ChannelElementValues> channelElements = new ArrayList<>();

    /**
     * the original values from the request dto, which are retrieved from
     * smartmeter database table.
     */
    private MbusChannelElementsDto mbusChannelElementsDto;

    public MbusChannelElementsResponseDtoBuilder() {
        // empty ctor.
    }

    public MbusChannelElementsResponseDtoBuilder(final MbusChannelElementsResponseDto sourceDto) {
        super();
        this.mbusChannelElementsDto = sourceDto.getMbusChannelElementsDto();
        this.channel = sourceDto.getChannel();
        this.channelElements = sourceDto.getChannelElements();
    }

    public MbusChannelElementsResponseDtoBuilder withMbusChannelElementsDto(
            final MbusChannelElementsDto mbusChannelElementsDto) {
        this.mbusChannelElementsDto = mbusChannelElementsDto;
        return this;
    }

    public MbusChannelElementsResponseDtoBuilder withChannel(final Integer channel) {
        this.channel = channel;
        return this;
    }

    public MbusChannelElementsResponseDtoBuilder withAddChannelValues(final ChannelElementValues channelElements) {
        this.channelElements.add(channelElements);
        return this;
    }

    public MbusChannelElementsResponseDto build() {
        return new MbusChannelElementsResponseDto(this.mbusChannelElementsDto, this.channel, this.channelElements);
    }
}