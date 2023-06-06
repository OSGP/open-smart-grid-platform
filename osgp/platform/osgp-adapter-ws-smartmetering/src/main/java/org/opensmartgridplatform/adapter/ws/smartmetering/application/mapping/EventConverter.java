// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import java.util.List;
import java.util.stream.Collectors;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EventLogCategory;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EventType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.Event;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventConverter
    extends BidirectionalConverter<
        Event, org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event> {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventConverter.class);

  @Override
  public org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event convertTo(
      final Event source,
      final Type<org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event>
          destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    try {
      final XMLGregorianCalendar timestamp =
          DatatypeFactory.newInstance()
              .newXMLGregorianCalendar(source.getTimestamp().toGregorianCalendar());
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event event =
          new org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event();
      event.setEventCode(source.getEventCode());
      event.setEventType(EventType.fromValue(source.getEventType().name()));
      event.setTimestamp(timestamp);
      event.setEventCounter(source.getEventCounter());
      event.setEventLogCategory(EventLogCategory.fromValue(source.getEventLogCategory().name()));

      for (final EventDetail sourceEventDetail : source.getEventDetails()) {
        final org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EventDetail
            eventDetail =
                new org.opensmartgridplatform.adapter.ws.schema.smartmetering.management
                    .EventDetail();
        eventDetail.setName(sourceEventDetail.getName());
        eventDetail.setValue(sourceEventDetail.getValue());
        event.getEventDetails().add(eventDetail);
      }
      return event;
    } catch (final DatatypeConfigurationException e) {
      LOGGER.error("DatatypeConfigurationException", e);
    }

    return null;
  }

  @Override
  public Event convertFrom(
      final org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event source,
      final Type<Event> destinationType,
      final MappingContext context) {
    if (source == null) {
      return null;
    }

    final DateTime timestamp = new DateTime(source.getTimestamp().toGregorianCalendar().getTime());
    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventLogCategory
        eventLogCategory =
            org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventLogCategory
                .fromValue(source.getEventLogCategory().value());
    final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventType eventType =
        org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventType.fromValue(
            source.getEventType().value());

    final List<EventDetail> eventDetails =
        source.getEventDetails().stream()
            .map(
                sourceEventDetail ->
                    new EventDetail(sourceEventDetail.getName(), sourceEventDetail.getValue()))
            .collect(Collectors.toList());

    return new Event(
        timestamp, eventType, source.getEventCounter(), eventLogCategory, eventDetails);
  }
}
