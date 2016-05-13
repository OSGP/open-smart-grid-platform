/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.junit.Test;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ManagementMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.EventLogCategory;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FindEventsRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FindEventsRequestDataList;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsRequestList;

public class FindEventsRequestMessageDataContainerMappingTest {

    private ManagementMapper managementMapper = new ManagementMapper();

    // Test if mapping with a null List succeeds
    @Test
    public void testWithNullList() {
        // build test data
        final FindEventsRequestDataList container = new FindEventsRequestDataList(null);
        // actual mapping
        final FindEventsRequestList containerDto = this.managementMapper.map(container,
                FindEventsRequestList.class);
        // test mapping
        assertNotNull(containerDto);
        assertNull(containerDto.getFindEventsQueryList());
    }

    // Test if mapping with an empty List succeeds
    @Test
    public void testWithEmptyList() {
        // build test data
        final FindEventsRequestDataList container = new FindEventsRequestDataList(
                new ArrayList<FindEventsRequestData>());
        // actual mapping
        final FindEventsRequestList containerDto = this.managementMapper.map(container,
                FindEventsRequestList.class);
        // test mapping
        assertNotNull(containerDto);
        assertNotNull(containerDto.getFindEventsQueryList());
        assertTrue(containerDto.getFindEventsQueryList().isEmpty());
    }

    // Test if mapping with a non-empty List succeeds
    @Test
    public void testWithNonEmptyList() {
        // build test data
        final FindEventsRequestData findEventsQuery = new FindEventsRequestData(EventLogCategory.STANDARD_EVENT_LOG,
                new DateTime(), new DateTime());
        final ArrayList<FindEventsRequestData> findEventsQueryList = new ArrayList<FindEventsRequestData>();
        findEventsQueryList.add(findEventsQuery);
        final FindEventsRequestDataList container = new FindEventsRequestDataList(
                findEventsQueryList);
        // actual mapping
        final FindEventsRequestList containerDto = this.managementMapper.map(container,
                FindEventsRequestList.class);
        // test mapping
        assertNotNull(containerDto);
        assertNotNull(containerDto.getFindEventsQueryList());
        assertEquals(container.getFindEventsQueryList().get(0).getEventLogCategory().name(), containerDto
                .getFindEventsQueryList().get(0).getEventLogCategory().name());
        assertEquals(container.getFindEventsQueryList().get(0).getFrom(), containerDto.getFindEventsQueryList().get(0)
                .getFrom());
        assertEquals(container.getFindEventsQueryList().get(0).getUntil(), containerDto.getFindEventsQueryList().get(0)
                .getUntil());
    }
}
