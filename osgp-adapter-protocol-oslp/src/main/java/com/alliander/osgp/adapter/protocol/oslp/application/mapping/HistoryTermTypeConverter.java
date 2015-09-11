package com.alliander.osgp.adapter.protocol.oslp.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.dto.valueobjects.HistoryTermType;
import com.alliander.osgp.oslp.Oslp;

public class HistoryTermTypeConverter extends BidirectionalConverter<HistoryTermType, Oslp.HistoryTermType> {

    @Override
    public com.alliander.osgp.oslp.Oslp.HistoryTermType convertTo(final HistoryTermType source,
            final Type<com.alliander.osgp.oslp.Oslp.HistoryTermType> destinationType) {
        if (source == null) {
            return null;
        }

        if (source.equals(HistoryTermType.LONG)) {
            return com.alliander.osgp.oslp.Oslp.HistoryTermType.Long;
        } else {
            return com.alliander.osgp.oslp.Oslp.HistoryTermType.Short;
        }
    }

    @Override
    public HistoryTermType convertFrom(final com.alliander.osgp.oslp.Oslp.HistoryTermType source,
            final Type<HistoryTermType> destinationType) {
        if (source == null) {
            return null;
        }

        if (source.equals(com.alliander.osgp.oslp.Oslp.HistoryTermType.Long)) {
            return HistoryTermType.LONG;
        } else {
            return HistoryTermType.SHORT;
        }
    }

}
