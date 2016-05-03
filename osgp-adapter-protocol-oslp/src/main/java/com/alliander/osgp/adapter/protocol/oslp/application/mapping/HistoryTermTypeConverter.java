package com.alliander.osgp.adapter.protocol.oslp.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.dto.valueobjects.HistoryTermTypeDto;
import com.alliander.osgp.oslp.Oslp;

public class HistoryTermTypeConverter extends BidirectionalConverter<HistoryTermTypeDto, Oslp.HistoryTermType> {

    @Override
    public com.alliander.osgp.oslp.Oslp.HistoryTermType convertTo(final HistoryTermTypeDto source,
            final Type<com.alliander.osgp.oslp.Oslp.HistoryTermType> destinationType) {
        if (source == null) {
            return null;
        }

        if (source.equals(HistoryTermTypeDto.LONG)) {
            return com.alliander.osgp.oslp.Oslp.HistoryTermType.Long;
        } else {
            return com.alliander.osgp.oslp.Oslp.HistoryTermType.Short;
        }
    }

    @Override
    public HistoryTermTypeDto convertFrom(final com.alliander.osgp.oslp.Oslp.HistoryTermType source,
            final Type<HistoryTermTypeDto> destinationType) {
        if (source == null) {
            return null;
        }

        if (source.equals(com.alliander.osgp.oslp.Oslp.HistoryTermType.Long)) {
            return HistoryTermTypeDto.LONG;
        } else {
            return HistoryTermTypeDto.SHORT;
        }
    }

}
