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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDay;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDaysRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequest;

public class SetSpecialDaysRequestMappingTest {

    private ConfigurationMapper configurationMapper = new ConfigurationMapper();
    private static final String DEVICE_ID = "nr1";
    private static final int DAY_ID = 1;
    private static final byte[] COSEMDATE_BYTE_ARRAY = { (byte) 0x07, (byte) 0xE0, 4, 6, 4 };

    /**
     * Tests mapping of a SetSpecialDaysReqeust object, when its
     * SpecialDaysReqeustData object is null.
     */
    @Test
    public void testSpecialDaysRequestMappingNull() {

        // build test data
        final SpecialDaysRequestData specialDaysRequestData = null;
        final SetSpecialDaysRequest setSpecialDaysRequest = new SetSpecialDaysRequest();
        setSpecialDaysRequest.setDeviceIdentification(DEVICE_ID);
        setSpecialDaysRequest.setSpecialDaysRequestData(specialDaysRequestData);

        // actual mapping
        final SpecialDaysRequest specialDaysRequest = this.configurationMapper.map(setSpecialDaysRequest,
                SpecialDaysRequest.class);

        // check mapping
        assertNotNull(specialDaysRequest);
        assertNotNull(specialDaysRequest.getDeviceIdentification());
        assertEquals(DEVICE_ID, specialDaysRequest.getDeviceIdentification());
        assertNull(specialDaysRequest.getSpecialDaysRequestData());

    }

    /**
     * Tests mapping of a SetSpecialDaysRequest, when its SpecialDaysRequestData
     * object has an empty List.
     */
    @Test
    public void testSpecialDaysRequestMappingEmptyList() {

        // build test data
        // No-arg constructor for SpecialDaysRequestData takes care of creating
        // a empty List.
        final SpecialDaysRequestData specialDaysRequestData = new SpecialDaysRequestData();
        final SetSpecialDaysRequest setSpecialDaysRequest = new SetSpecialDaysRequest();
        setSpecialDaysRequest.setDeviceIdentification(DEVICE_ID);
        setSpecialDaysRequest.setSpecialDaysRequestData(specialDaysRequestData);

        // actual mapping
        final SpecialDaysRequest specialDaysRequest = this.configurationMapper.map(setSpecialDaysRequest,
                SpecialDaysRequest.class);

        // check mapping
        assertNotNull(specialDaysRequest);
        assertNotNull(specialDaysRequest.getDeviceIdentification());
        assertNotNull(specialDaysRequest.getSpecialDaysRequestData());
        assertNotNull(specialDaysRequest.getSpecialDaysRequestData().getSpecialDays());
        assertEquals(DEVICE_ID, specialDaysRequest.getDeviceIdentification());
        assertTrue(specialDaysRequest.getSpecialDaysRequestData().getSpecialDays().isEmpty());
    }

    /**
     * Tests mapping of a SetSpecialDaysRequest object, when its
     * SpecialDaysRequestData object has a filled List (1 entry).
     */
    @Test
    public void testSetSpecialDaysRequestMappingFilledList() {

        // build test data
        final SpecialDaysRequestData specialDaysRequestData = new SpecialDaysRequestData();
        final SpecialDay specialDay = new SpecialDay();
        specialDay.setDayId(DAY_ID);
        specialDay.setSpecialDayDate(COSEMDATE_BYTE_ARRAY);
        // To add a SpecialDay to the List, you need to use the getter in
        // combination with add()
        specialDaysRequestData.getSpecialDays().add(specialDay);
        final SetSpecialDaysRequest setSpecialDaysRequest = new SetSpecialDaysRequest();
        setSpecialDaysRequest.setDeviceIdentification(DEVICE_ID);
        setSpecialDaysRequest.setSpecialDaysRequestData(specialDaysRequestData);

        // actual mapping
        final SpecialDaysRequest specialDaysRequest = this.configurationMapper.map(setSpecialDaysRequest,
                SpecialDaysRequest.class);

        // check mapping
        assertNotNull(specialDaysRequest);
        assertNotNull(specialDaysRequest.getDeviceIdentification());
        assertNotNull(specialDaysRequest.getSpecialDaysRequestData());
        assertNotNull(specialDaysRequest.getSpecialDaysRequestData().getSpecialDays());
        assertNotNull(specialDaysRequest.getSpecialDaysRequestData().getSpecialDays().get(0));
        assertNotNull(specialDaysRequest.getSpecialDaysRequestData().getSpecialDays().get(0).getSpecialDayDate());

        assertEquals(DEVICE_ID, specialDaysRequest.getDeviceIdentification());
        assertEquals(specialDaysRequestData.getSpecialDays().size(), specialDaysRequest.getSpecialDaysRequestData()
                .getSpecialDays().size());
        assertEquals(DAY_ID, specialDaysRequest.getSpecialDaysRequestData().getSpecialDays().get(0).getDayId());

        // For more info on the mapping of byte[] to CosemDate object, see the
        // CosemDateConverterTest.
    }

    /**
     * Tests mapping of a SpecialDaysRequesData object. A NullPointerException
     * should be thrown when no byte[] is specified for SpecialDay.
     */
    @Test(expected = NullPointerException.class)
    public void testWithoutByteArray() {

        // build test data
        final SpecialDaysRequestData specialDaysRequestData = new SpecialDaysRequestData();
        final SpecialDay specialDay = new SpecialDay();
        specialDay.setDayId(DAY_ID);
        // To add a SpecialDay to the List, you need to use the getter in
        // combination with add()
        specialDaysRequestData.getSpecialDays().add(specialDay);
        final SetSpecialDaysRequest setSpecialDaysRequest = new SetSpecialDaysRequest();
        setSpecialDaysRequest.setDeviceIdentification(DEVICE_ID);
        setSpecialDaysRequest.setSpecialDaysRequestData(specialDaysRequestData);

        // actual mapping
        this.configurationMapper.map(setSpecialDaysRequest, SpecialDaysRequest.class);

    }

}
