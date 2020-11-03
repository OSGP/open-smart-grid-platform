/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventConverter extends
        BidirectionalConverter<Event, org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConverter.class);

    @Override
    public org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event convertTo(final Event source,
            final Type<org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event> destinationType,
            final MappingContext context) {
        if (source == null) {
            return null;
        }

        try {
            final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventType eventType =
                    org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventType.getByEventCode(
                    source.getEventCode());
            final XMLGregorianCalendar timestamp = DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    source.getTimestamp().toGregorianCalendar());
            final org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event event =
                    new org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event();
            event.setEventType(EventType.fromValue(eventType.toString()));
            event.setTimestamp(timestamp);
            event.setEventCounter(source.getEventCounter());
            event.setEventLogCategory(EventLogCategory.fromValue(source.getEventLogCategory().name()));
            event.setDuration(source.getDuration());
            if (source.getStartTime() != null) {
                event.setStartTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                        source.getStartTime().toGregorianCalendar()));
            }
            return event;
        } catch (final DatatypeConfigurationException e) {
            LOGGER.error("DatatypeConfigurationException", e);
        }

        return null;
    }

    @Override
    public Event convertFrom(final org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.Event source,
            final Type<Event> destinationType, final MappingContext context) {
        if (source == null) {
            return null;
        }

        final DateTime timestamp = new DateTime(source.getTimestamp().toGregorianCalendar().getTime());
        final Integer eventCode = org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventType.valueOf(
                source.getEventType().toString()).getEventCode();
        final org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventLogCategory eventLogCategory =
                org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventLogCategory.fromValue(
                source.getEventLogCategory().value());
        DateTime startTime = null;
        if (source.getStartTime() != null) {
            startTime = new DateTime(source.getStartTime().toGregorianCalendar().getTime());
        }

        return new Event(timestamp, eventCode, source.getEventCounter(), eventLogCategory,
                startTime, source.getDuration());
    }
}
