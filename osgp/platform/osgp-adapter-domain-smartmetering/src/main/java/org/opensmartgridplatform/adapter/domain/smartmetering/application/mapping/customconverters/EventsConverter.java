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

        EventLogCategory eventLogCategory = EventLogCategory.fromValue(source.getEventLogCategoryName());
        if (eventLogCategory == EventLogCategory.POWER_FAILURE_EVENT_LOG) {
            return new Event(source.getTimestamp(), source.getEventCode(), eventLogCategory, source.getStartTime(),
                    source.getDuration());
        } else {
            return new Event(source.getTimestamp(), source.getEventCode(), source.getEventCounter(), eventLogCategory);
        }
    }

    @Override
    public EventDto convertFrom(final Event source, final Type<EventDto> destinationType,
            final MappingContext context) {
        if (source == null) {
            return null;
        }

        if (source.getEventLogCategory() == EventLogCategory.POWER_FAILURE_EVENT_LOG) {
            return new EventDto(source.getTimestamp(), source.getEventCode(), source.getEventLogCategory().name(),
                    source.getStartTime(), source.getDuration());
        } else {
            return new EventDto(source.getTimestamp(), source.getEventCode(), source.getEventCounter(),
                    source.getEventLogCategory().name());
        }
    }
}
