package com.alliander.osgp.adapter.domain.smartmetering.application.mapping;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.domain.core.valueobjects.EventLogCategory;
import com.alliander.osgp.domain.core.valueobjects.FindEventsQuery;

public class FindEventsQueryConverter extends
        BidirectionalConverter<com.alliander.osgp.dto.valueobject.smartmetering.FindEventsQuery, FindEventsQuery> {

    @Override
    public FindEventsQuery convertTo(final com.alliander.osgp.dto.valueobject.smartmetering.FindEventsQuery source,
            final Type<FindEventsQuery> destinationType) {
        if (source == null) {
            return null;
        }

        final EventLogCategory eventLogCategory = EventLogCategory.valueOf(source.getEventLogCategory().toString());

        return new FindEventsQuery(eventLogCategory, source.getFrom(), source.getUntil());
    }

    @Override
    public com.alliander.osgp.dto.valueobject.smartmetering.FindEventsQuery convertFrom(final FindEventsQuery source,
            final Type<com.alliander.osgp.dto.valueobject.smartmetering.FindEventsQuery> destinationType) {
        if (source == null) {
            return null;
        }

        final com.alliander.osgp.dto.valueobject.smartmetering.EventLogCategory eventLogCategory = com.alliander.osgp.dto.valueobject.smartmetering.EventLogCategory
                .valueOf(source.getEventLogCategory().toString());

        return new com.alliander.osgp.dto.valueobject.smartmetering.FindEventsQuery(eventLogCategory, source.getFrom(),
                source.getUntil());
    }

}
