package com.alliander.osgp.adapter.domain.core.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.MeterType;

public class MeterTypeConverter extends
        BidirectionalConverter<com.alliander.osgp.dto.valueobjects.MeterType, MeterType> {

    @Override
    public MeterType convertTo(final com.alliander.osgp.dto.valueobjects.MeterType source,
            final Type<MeterType> destinationType) {
        if (source == null) {
            return null;
        }

        return MeterType.valueOf(source.toString());
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.MeterType convertFrom(final MeterType source,
            final Type<com.alliander.osgp.dto.valueobjects.MeterType> destinationType) {
        if (source == null) {
            return null;
        }

        return com.alliander.osgp.dto.valueobjects.MeterType.valueOf(source.toString());
    }

}
