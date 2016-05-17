/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindEventsResponseData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.Event;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.EventMessagesResponse;

public class EventMessageDataContainerConverter
        extends
        CustomConverter<EventMessagesResponse, com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindEventsResponseData> {

    @Override
    public FindEventsResponseData convert(final EventMessagesResponse source,
            final Type<? extends FindEventsResponseData> destinationType) {
        if (source == null) {
            return null;
        }

        final com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindEventsResponseData response = new com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindEventsResponseData();

        for (final Event event : source.getEvents()) {
            final com.alliander.osgp.adapter.ws.schema.smartmetering.management.Event eventResponse = this.mapperFacade
                    .map(event, com.alliander.osgp.adapter.ws.schema.smartmetering.management.Event.class);
            response.getEvents().add(eventResponse);
        }

        return response;
    }

}
