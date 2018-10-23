package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.mapping;

import org.opensmartgridplatform.dto.valueobjects.HistoryTermTypeDto;
import org.opensmartgridplatform.oslp.Oslp;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class HistoryTermTypeConverter extends BidirectionalConverter<HistoryTermTypeDto, Oslp.HistoryTermType> {

    @Override
    public org.opensmartgridplatform.oslp.Oslp.HistoryTermType convertTo(final HistoryTermTypeDto source,
            final Type<org.opensmartgridplatform.oslp.Oslp.HistoryTermType> destinationType, final MappingContext context) {
        if (source == null) {
            return null;
        }

        if (source.equals(HistoryTermTypeDto.LONG)) {
            return org.opensmartgridplatform.oslp.Oslp.HistoryTermType.Long;
        } else {
            return org.opensmartgridplatform.oslp.Oslp.HistoryTermType.Short;
        }
    }

    @Override
    public HistoryTermTypeDto convertFrom(final org.opensmartgridplatform.oslp.Oslp.HistoryTermType source,
            final Type<HistoryTermTypeDto> destinationType, final MappingContext context) {
        if (source == null) {
            return null;
        }

        if (source.equals(org.opensmartgridplatform.oslp.Oslp.HistoryTermType.Long)) {
            return HistoryTermTypeDto.LONG;
        } else {
            return HistoryTermTypeDto.SHORT;
        }
    }

}
