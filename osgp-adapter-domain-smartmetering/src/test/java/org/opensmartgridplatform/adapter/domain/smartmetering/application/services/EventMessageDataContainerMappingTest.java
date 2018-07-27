/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.junit.Test;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ManagementMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.EventMessagesResponse;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.EventMessageDataResponseDto;

public class EventMessageDataContainerMappingTest {

    private ManagementMapper managementMapper = new ManagementMapper();

    // Test if mapping with a null List succeeds
    @Test
    public void testWithNullList() {
        // build test data
        final EventMessageDataResponseDto containerDto = new EventMessageDataResponseDto(null);
        // actual mapping
        final EventMessagesResponse container = this.managementMapper.map(containerDto,
                EventMessagesResponse.class);
        // test mapping
        assertNotNull(container);
        assertNull(container.getEvents());

    }

    // Test if mapping with an empty list succeeds
    @Test
    public void testWithEmptyList() {
        // build test data
        final EventMessageDataResponseDto containerDto = new EventMessageDataResponseDto(new ArrayList<EventDto>());
        // actual mapping
        final EventMessagesResponse container = this.managementMapper.map(containerDto,
                EventMessagesResponse.class);
        // test mapping
        assertNotNull(container);
        assertNotNull(container.getEvents());
        assertTrue(container.getEvents().isEmpty());

    }

    // Test if mapping with a filled List succeeds
    @Test
    public void testWithFilledList() {
        // build test data
        final EventDto event = new EventDto(new DateTime(), new Integer(1), new Integer(2));
        final ArrayList<EventDto> events = new ArrayList<EventDto>();
        events.add(event);
        final EventMessageDataResponseDto containerDto = new EventMessageDataResponseDto(events);
        // actual mapping
        final EventMessagesResponse container = this.managementMapper.map(containerDto,
                EventMessagesResponse.class);
        // test mapping
        assertNotNull(container);
        assertNotNull(container.getEvents());
        assertEquals(containerDto.getEvents().get(0).getTimestamp(), container.getEvents().get(0).getTimestamp());
        assertEquals(containerDto.getEvents().get(0).getEventCode(), container.getEvents().get(0).getEventCode());
        assertEquals(containerDto.getEvents().get(0).getEventCounter(), container.getEvents().get(0).getEventCounter());
    }

}
