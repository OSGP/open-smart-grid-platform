/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EventType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.Event;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class EventConverter extends
        BidirectionalConverter<Event, com.alliander.osgp.adapter.ws.schema.smartmetering.management.Event> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConverter.class);

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.management.Event convertTo(final Event source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.management.Event> destinationType) {
        if (source == null) {
            return null;
        }

        try {
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.EventType eventType = com.alliander.osgp.domain.core.valueobjects.smartmetering.EventType
                    .getValue(source.getEventCode());
            final XMLGregorianCalendar timestamp = DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    source.getTimestamp().toGregorianCalendar());
            final com.alliander.osgp.adapter.ws.schema.smartmetering.management.Event event = new com.alliander.osgp.adapter.ws.schema.smartmetering.management.Event();
            event.setEventType(EventType.fromValue(eventType.toString()));
            event.setTimestamp(timestamp);
            event.setEventCounter(source.getEventCounter());
            return event;
        } catch (final DatatypeConfigurationException e) {
            LOGGER.error("DatatypeConfigurationException", e);
        }

        return null;
    }

    @Override
    public Event convertFrom(final com.alliander.osgp.adapter.ws.schema.smartmetering.management.Event source,
            final Type<Event> destinationType) {
        if (source == null) {
            return null;
        }

        final DateTime timestamp = new DateTime(source.getTimestamp().toGregorianCalendar().getTime());
        final Integer eventCode = com.alliander.osgp.domain.core.valueobjects.smartmetering.EventType.valueOf(
                source.getEventType().toString()).getValue();
        return new Event(timestamp, eventCode, source.getEventCounter());
    }
}
