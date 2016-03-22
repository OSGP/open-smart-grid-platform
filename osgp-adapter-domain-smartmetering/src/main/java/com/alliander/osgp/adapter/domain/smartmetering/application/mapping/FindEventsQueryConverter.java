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

import com.alliander.osgp.domain.core.valueobjects.smartmetering.EventLogCategory;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FindEventsQuery;
import com.alliander.osgp.dto.valueobjects.smartmetering.EventLogCategoryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQueryDto;

public class FindEventsQueryConverter extends BidirectionalConverter<FindEventsQueryDto, FindEventsQuery> {

    @Override
    public FindEventsQuery convertTo(final FindEventsQueryDto source, final Type<FindEventsQuery> destinationType) {
        if (source == null) {
            return null;
        }

        final EventLogCategory eventLogCategory = EventLogCategory.valueOf(source.getEventLogCategory().toString());

        return new FindEventsQuery(eventLogCategory, source.getFrom(), source.getUntil());
    }

    @Override
    public FindEventsQueryDto convertFrom(final FindEventsQuery source, final Type<FindEventsQueryDto> destinationType) {
        if (source == null) {
            return null;
        }

        final EventLogCategoryDto eventLogCategory = EventLogCategoryDto.valueOf(source.getEventLogCategory()
                .toString());

        return new FindEventsQueryDto(eventLogCategory, source.getFrom(), source.getUntil());
    }

}
