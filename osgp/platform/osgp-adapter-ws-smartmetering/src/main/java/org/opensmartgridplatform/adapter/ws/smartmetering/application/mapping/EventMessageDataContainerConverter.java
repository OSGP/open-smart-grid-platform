/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.FindEventsResponseData;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.Event;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventMessagesResponse;

public class EventMessageDataContainerConverter
    extends CustomConverter<
        EventMessagesResponse,
        org.opensmartgridplatform.adapter.ws.schema.smartmetering.management
            .FindEventsResponseData> {

  @Override
  public FindEventsResponseData convert(
      final EventMessagesResponse source,
      final Type<? extends FindEventsResponseData> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    final org.opensmartgridplatform.adapter.ws.schema.smartmetering.management
            .FindEventsResponseData
        response =
            new org.opensmartgridplatform.adapter.ws.schema.smartmetering.management
                .FindEventsResponseData();

    for (final Event event : source.getEvents()) {
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event
          eventResponse =
              this.mapperFacade.map(
                  event,
                  org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event.class);
      response.getEvents().add(eventResponse);
    }

    return response;
  }
}
