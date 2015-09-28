package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.Event;

public class EventsConverter extends BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.Event, Event> {

    @Override
    public Event convertTo(final com.alliander.osgp.dto.valueobjects.smartmetering.Event source, final Type<Event> destinationType) {
        if (source == null) {
            return null;
        }

        return new Event(source.getTimestamp(), source.getEventCode());
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.Event convertFrom(final Event source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.Event> destinationType) {
        if (source == null) {
            return null;
        }

        return new com.alliander.osgp.dto.valueobjects.smartmetering.Event(source.getTimestamp(), source.getEventCode());
    }
}
