/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.dto.valueobjects.LightValueDto;
import com.alliander.osgp.oslp.Oslp;
import com.google.protobuf.ByteString;

public class LightValueToOslpLightValueMapper extends BidirectionalConverter<LightValueDto, Oslp.LightValue> {

    @Override
    public LightValueDto convertFrom(final Oslp.LightValue source, final Type<LightValueDto> destinationType) {
        int index = 0;
        if (source.hasIndex()) {
            index = source.getIndex().byteAt(0);
        }

        int dimValue = 0;
        if (source.hasDimValue()) {
            dimValue = source.getDimValue().byteAt(0);
        }

        return new LightValueDto(index, source.hasOn(), dimValue);
    }

    @Override
    public Oslp.LightValue convertTo(final LightValueDto source, final Type<Oslp.LightValue> desitnationType) {
        final Oslp.LightValue.Builder builder = Oslp.LightValue.newBuilder();
        builder.setIndex(this.mapperFacade.map(source.getIndex(), ByteString.class));
        builder.setDimValue(this.mapperFacade.map(source.getDimValue(), ByteString.class));
        builder.setOn(source.isOn());

        return builder.build();
    }

}
