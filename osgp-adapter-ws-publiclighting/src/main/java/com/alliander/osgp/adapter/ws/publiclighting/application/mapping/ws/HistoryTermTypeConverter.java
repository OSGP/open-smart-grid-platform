package com.alliander.osgp.adapter.ws.publiclighting.application.mapping.ws;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.HistoryTermType;

public class HistoryTermTypeConverter
        extends
        CustomConverter<com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.HistoryTermType, HistoryTermType> {
    @Override
    public HistoryTermType convert(
            final com.alliander.osgp.adapter.ws.schema.publiclighting.devicemonitoring.HistoryTermType source,
            final Type<? extends HistoryTermType> destinationType) {
        return HistoryTermType.valueOf(source.value());
    }
}
