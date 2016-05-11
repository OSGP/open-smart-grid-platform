/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.tariffswitching.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.LightType;

public class LightTypeConverter extends
        BidirectionalConverter<com.alliander.osgp.dto.valueobjects.LightTypeDto, LightType> {

    @Override
    public LightType convertTo(final com.alliander.osgp.dto.valueobjects.LightTypeDto source,
            final Type<LightType> destinationType) {
        if (source == null) {
            return null;
        }

        return LightType.valueOf(source.toString());
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.LightTypeDto convertFrom(final LightType source,
            final Type<com.alliander.osgp.dto.valueobjects.LightTypeDto> destinationType) {
        if (source == null) {
            return null;
        }

        return com.alliander.osgp.dto.valueobjects.LightTypeDto.valueOf(source.toString());
    }

}
