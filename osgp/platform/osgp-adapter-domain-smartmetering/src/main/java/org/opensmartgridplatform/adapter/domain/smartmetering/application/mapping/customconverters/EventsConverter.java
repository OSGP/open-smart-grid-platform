/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.Event;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventLogCategory;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDto;

public class EventsConverter extends BidirectionalConverter<EventDto, Event> {

    @Override
    public Event convertTo(final EventDto source, final Type<Event> destinationType, final MappingContext context) {
        if (source == null) {
            return null;
        }

        return new Event(source.getTimestamp(), source.getEventCode(), source.getEventCounter(),
                EventLogCategory.fromValue(source.getEventLogCategoryName()), source.getStartTime(),
                source.getDuration());
    }

    @Override
    public EventDto convertFrom(final Event source, final Type<EventDto> destinationType,
            final MappingContext context) {
        if (source == null) {
            return null;
        }

        return new EventDto(source.getTimestamp(), source.getEventCode(), source.getEventCounter(),
                source.getEventLogCategory().name(), source.getStartTime(), source.getDuration());
    }
}
