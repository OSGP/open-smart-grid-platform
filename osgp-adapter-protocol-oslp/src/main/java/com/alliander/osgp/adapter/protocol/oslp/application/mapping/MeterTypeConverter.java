package com.alliander.osgp.adapter.protocol.oslp.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.dto.valueobjects.MeterType;
import com.alliander.osgp.oslp.Oslp;

public class MeterTypeConverter extends BidirectionalConverter<MeterType, Oslp.MeterType> {

    @Override
    public com.alliander.osgp.oslp.Oslp.MeterType convertTo(final MeterType source,
            final Type<com.alliander.osgp.oslp.Oslp.MeterType> destinationType) {
        if (source == null) {
            return null;
        }

        return Oslp.MeterType.valueOf(source.toString());
    }

    @Override
    public MeterType convertFrom(final com.alliander.osgp.oslp.Oslp.MeterType source,
            final Type<MeterType> destinationType) {
        if (source == null || source == Oslp.MeterType.MT_NOT_SET) {
            return null;
        }

        return MeterType.valueOf(source.toString());
    }

}
