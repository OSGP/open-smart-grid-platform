package com.alliander.osgp.adapter.domain.core.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.LightType;

public class LightTypeConverter extends
        BidirectionalConverter<com.alliander.osgp.dto.valueobjects.LightType, LightType> {

    @Override
    public LightType convertTo(final com.alliander.osgp.dto.valueobjects.LightType source,
            final Type<LightType> destinationType) {
        if (source == null) {
            return null;
        }

        return LightType.valueOf(source.toString());
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.LightType convertFrom(final LightType source,
            final Type<com.alliander.osgp.dto.valueobjects.LightType> destinationType) {
        if (source == null) {
            return null;
        }

        return com.alliander.osgp.dto.valueobjects.LightType.valueOf(source.toString());
    }

}
