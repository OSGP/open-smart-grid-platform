//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.customconverters;

import java.util.List;
import java.util.stream.Collectors;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.Event;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventDetail;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventLogCategory;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDetailDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventTypeDto;

public class EventsConverter extends BidirectionalConverter<EventDto, Event> {

  @Override
  public Event convertTo(
      final EventDto source, final Type<Event> destinationType, final MappingContext context) {
    if (source == null) {
      return null;
    }
    final EventType eventType = EventType.valueOf(source.getEventTypeDto().name());
    final List<EventDetail> eventDetails =
        source.getEventDetails().stream()
            .map(sourceDetail -> new EventDetail(sourceDetail.getName(), sourceDetail.getValue()))
            .collect(Collectors.toList());

    return new Event(
        source.getTimestamp(),
        eventType,
        source.getEventCounter(),
        EventLogCategory.fromValue(source.getEventLogCategoryName()),
        eventDetails);
  }

  @Override
  public EventDto convertFrom(
      final Event source, final Type<EventDto> destinationType, final MappingContext context) {
    if (source == null) {
      return null;
    }
    final EventDto eventDto =
        new EventDto(
            source.getTimestamp(),
            source.getEventCode(),
            source.getEventCounter(),
            source.getEventLogCategory().name());
    eventDto.setEventTypeDto(EventTypeDto.valueOf(source.getEventType().name()));
    for (final EventDetail sourceEventDetail : source.getEventDetails()) {
      eventDto.addEventDetail(
          new EventDetailDto(sourceEventDetail.getName(), sourceEventDetail.getValue()));
    }
    return eventDto;
  }
}
