/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.core.application.mapping;

import com.alliander.osgp.domain.core.valueobjects.MeterType;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class MeterTypeConverter
        extends BidirectionalConverter<com.alliander.osgp.dto.valueobjects.MeterTypeDto, MeterType> {

    @Override
    public MeterType convertTo(final com.alliander.osgp.dto.valueobjects.MeterTypeDto source,
            final Type<MeterType> destinationType, final MappingContext context) {
        if (source == null) {
            return null;
        }

        return MeterType.valueOf(source.toString());
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.MeterTypeDto convertFrom(final MeterType source,
            final Type<com.alliander.osgp.dto.valueobjects.MeterTypeDto> destinationType,
            final MappingContext context) {
        if (source == null) {
            return null;
        }

        return com.alliander.osgp.dto.valueobjects.MeterTypeDto.valueOf(source.toString());
    }

}
