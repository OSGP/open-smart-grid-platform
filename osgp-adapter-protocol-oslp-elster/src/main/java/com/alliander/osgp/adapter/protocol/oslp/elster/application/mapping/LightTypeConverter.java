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

import com.alliander.osgp.dto.valueobjects.LightType;
import com.alliander.osgp.oslp.Oslp;

public class LightTypeConverter extends BidirectionalConverter<LightType, Oslp.LightType> {

    @Override
    public com.alliander.osgp.oslp.Oslp.LightType convertTo(final LightType source,
            final Type<com.alliander.osgp.oslp.Oslp.LightType> destinationType) {
        if (source == null) {
            return null;
        }

        return Oslp.LightType.valueOf(source.toString());
    }

    @Override
    public LightType convertFrom(final com.alliander.osgp.oslp.Oslp.LightType source,
            final Type<LightType> destinationType) {
        if (source == null || source == Oslp.LightType.LT_NOT_SET) {
            return null;
        }

        return LightType.valueOf(source.toString());
    }

}
