package com.alliander.osgp.adapter.ws.core.application.mapping.ws;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.EventType;

public class EventTypeConverter extends
        CustomConverter<EventType, com.alliander.osgp.adapter.ws.schema.core.devicemanagement.EventType> {

    @Override
    public com.alliander.osgp.adapter.ws.schema.core.devicemanagement.EventType convert(final EventType source,
            final Type<? extends com.alliander.osgp.adapter.ws.schema.core.devicemanagement.EventType> destinationType) {
        return com.alliander.osgp.adapter.ws.schema.core.devicemanagement.EventType.fromValue(source.name());
    }
}
