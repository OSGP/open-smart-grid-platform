/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.elster.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.dto.valueobjects.MeterTypeDto;
import com.alliander.osgp.oslp.Oslp;

public class MeterTypeConverter extends BidirectionalConverter<MeterTypeDto, Oslp.MeterType> {

    @Override
    public com.alliander.osgp.oslp.Oslp.MeterType convertTo(final MeterTypeDto source,
            final Type<com.alliander.osgp.oslp.Oslp.MeterType> destinationType) {
        if (source == null) {
            return null;
        }

        return Oslp.MeterType.valueOf(source.toString());
    }

    @Override
    public MeterTypeDto convertFrom(final com.alliander.osgp.oslp.Oslp.MeterType source,
            final Type<MeterTypeDto> destinationType) {
        if (source == null || source == Oslp.MeterType.MT_NOT_SET) {
            return null;
        }

        return MeterTypeDto.valueOf(source.toString());
    }

}
