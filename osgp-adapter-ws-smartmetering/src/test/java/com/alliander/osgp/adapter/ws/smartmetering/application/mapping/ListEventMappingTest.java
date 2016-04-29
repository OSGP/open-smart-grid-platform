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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.Event;

public class ListEventMappingTest {

    private ManagementMapper managementMapper = new ManagementMapper();
    private static final Integer EVENTCODE = new Integer(10);
    private static final Integer EVENTCOUNTER = new Integer(1);

    /**
     * Tests if mapping a List, typed to Event, succeeds if the List is filled.
     */
    @Test
    public void testFilledListEventMapping() {

        // build test data
        final DateTime timestamp = new DateTime();
        final Event event = new Event(timestamp, EVENTCODE, EVENTCOUNTER);
        final List<Event> listOriginal = new ArrayList<>();
        listOriginal.add(event);

        // actual mapping
        final List<com.alliander.osgp.adapter.ws.schema.smartmetering.management.Event> listMapped = this.managementMapper
                .mapAsList(listOriginal, com.alliander.osgp.adapter.ws.schema.smartmetering.management.Event.class);

        // check mapping
        assertNotNull(listMapped);
        assertNotNull(listMapped.get(0));
        assertNotNull(listMapped.get(0).getEventCounter());
        assertNotNull(listMapped.get(0).getEventType());
        assertNotNull(listMapped.get(0).getTimestamp());

        assertEquals(timestamp.getYear(), listMapped.get(0).getTimestamp().getYear());
        assertEquals(timestamp.getMonthOfYear(), listMapped.get(0).getTimestamp().getMonth());
        assertEquals(timestamp.getDayOfMonth(), listMapped.get(0).getTimestamp().getDay());

        assertEquals(timestamp.getHourOfDay(), listMapped.get(0).getTimestamp().getHour());
        assertEquals(timestamp.getMinuteOfHour(), listMapped.get(0).getTimestamp().getMinute());
        assertEquals(timestamp.getSecondOfMinute(), listMapped.get(0).getTimestamp().getSecond());

        assertEquals((int) EVENTCODE, listMapped.get(0).getEventType().ordinal());

        assertEquals(EVENTCOUNTER, listMapped.get(0).getEventCounter());
    }

    /**
     * Tests if mapping a List, typed to Event, succeeds if the List is empty.
     */
    @Test
    public void testEmptyListEventMapping() {

        // build test data
        final List<Event> listOriginal = new ArrayList<>();

        // actual mapping
        final List<com.alliander.osgp.adapter.ws.schema.smartmetering.management.Event> listMapped = this.managementMapper
                .mapAsList(listOriginal, com.alliander.osgp.adapter.ws.schema.smartmetering.management.Event.class);

        // check mapping
        assertNotNull(listMapped);
        assertTrue(listMapped.isEmpty());
    }

    /**
     * Tests if mapping a List, typed to Event, succeeds if the List is null.
     */
    @Test(expected = NullPointerException.class)
    public void testNullListEventMapping() {

        // build test data
        final List<Event> listOriginal = null;

        // actual mapping
        this.managementMapper.mapAsList(listOriginal,
                com.alliander.osgp.adapter.ws.schema.smartmetering.management.Event.class);

    }
}
