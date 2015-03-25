package com.alliander.osgp.adapter.protocol.oslp.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.dto.valueobjects.RelayType;
import com.alliander.osgp.oslp.Oslp;

public class RelayTypeConverter extends BidirectionalConverter<RelayType, Oslp.RelayType> {

    @Override
    public com.alliander.osgp.oslp.Oslp.RelayType convertTo(final RelayType source,
            final Type<com.alliander.osgp.oslp.Oslp.RelayType> destinationType) {
        if (source == null) {
            return null;
        }

        return Oslp.RelayType.valueOf(source.toString());
    }

    @Override
    public RelayType convertFrom(final com.alliander.osgp.oslp.Oslp.RelayType source,
            final Type<RelayType> destinationType) {
        if (source == null || source == Oslp.RelayType.RT_NOT_SET) {
            return null;
        }

        return RelayType.valueOf(source.toString());
    }

}
