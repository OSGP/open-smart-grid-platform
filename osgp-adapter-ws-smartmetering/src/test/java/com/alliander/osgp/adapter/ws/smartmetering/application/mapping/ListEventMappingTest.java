/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.Event;
import com.alliander.osgp.shared.mappers.XMLGregorianCalendarToDateTimeConverter;

public class ListEventMappingTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    // Mapping a List<Event> needs these converters: orika has trouble mapping
    // dates/calendars by default, and cannot map from object to enum by
    // default.
    @Before
    public void init() {
        this.mapperFactory.getConverterFactory().registerConverter(new XMLGregorianCalendarToDateTimeConverter());
        this.mapperFactory.getConverterFactory().registerConverter(new EventConverter());
    }

    // Test to see if mapping a List<Event> succeeds
    @Test
    public void testListEventMapping() {
        // build test data
        final DateTime timestamp = new DateTime();
        final Integer eventCode = new Integer(10);
        final Integer eventCounter = new Integer(1);
        final Event event = new Event(timestamp, eventCode, eventCounter);
        final List<Event> listOriginal = new ArrayList<>();
        listOriginal.add(event);

        // actual mapping
        final List<com.alliander.osgp.adapter.ws.schema.smartmetering.management.Event> listMapped = this.mapperFactory
                .getMapperFacade().mapAsList(listOriginal,
                        com.alliander.osgp.adapter.ws.schema.smartmetering.management.Event.class);

        // check mapping
        assertNotNull(listMapped);

        assertEquals(timestamp.getYear(), listMapped.get(0).getTimestamp().getYear());
        assertEquals(timestamp.getMonthOfYear(), listMapped.get(0).getTimestamp().getMonth());
        assertEquals(timestamp.getDayOfMonth(), listMapped.get(0).getTimestamp().getDay());

        assertEquals(timestamp.getHourOfDay(), listMapped.get(0).getTimestamp().getHour());
        assertEquals(timestamp.getMinuteOfHour(), listMapped.get(0).getTimestamp().getMinute());
        assertEquals(timestamp.getSecondOfMinute(), listMapped.get(0).getTimestamp().getSecond());

        // casting to int necessary to avoid ambiguous calling.
        assertEquals((int) eventCode, listMapped.get(0).getEventType().ordinal());

        assertEquals(eventCounter, listMapped.get(0).getEventCounter());

    }
}
