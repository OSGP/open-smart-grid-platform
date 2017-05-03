/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.application.mapping;

import com.alliander.osgp.dto.valueobjects.LightTypeDto;
import com.alliander.osgp.oslp.Oslp;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class LightTypeConverter extends BidirectionalConverter<LightTypeDto, Oslp.LightType> {

    @Override
    public com.alliander.osgp.oslp.Oslp.LightType convertTo(final LightTypeDto source,
            final Type<com.alliander.osgp.oslp.Oslp.LightType> destinationType, final MappingContext context) {
        if (source == null) {
            return null;
        }

        return Oslp.LightType.valueOf(source.toString());
    }

    @Override
    public LightTypeDto convertFrom(final com.alliander.osgp.oslp.Oslp.LightType source,
            final Type<LightTypeDto> destinationType, final MappingContext context) {
        if (source == null || source == Oslp.LightType.LT_NOT_SET) {
            return null;
        }

        return LightTypeDto.valueOf(source.toString());
    }

}
