/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.Event;

public class EventsConverter extends
BidirectionalConverter<com.alliander.osgp.dto.valueobjects.smartmetering.EventDto, Event> {

    @Override
    public Event convertTo(final com.alliander.osgp.dto.valueobjects.smartmetering.EventDto source,
            final Type<Event> destinationType) {
        if (source == null) {
            return null;
        }

        return new Event(source.getTimestamp(), source.getEventCode(), source.getEventCounter());
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.smartmetering.EventDto convertFrom(final Event source,
            final Type<com.alliander.osgp.dto.valueobjects.smartmetering.EventDto> destinationType) {
        if (source == null) {
            return null;
        }

        return new com.alliander.osgp.dto.valueobjects.smartmetering.EventDto(source.getTimestamp(),
                source.getEventCode(), source.getEventCounter());
    }
}
