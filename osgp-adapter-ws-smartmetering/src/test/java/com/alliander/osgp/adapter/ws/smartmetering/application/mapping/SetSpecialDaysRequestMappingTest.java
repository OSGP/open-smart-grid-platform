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
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.junit.Before;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDay;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDaysRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequest;

public class SetSpecialDaysRequestMappingTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    // Tests only work with Orika if a CosemDateConverter is registered.
    @Before
    public void init() {
        this.mapperFactory.getConverterFactory().registerConverter(new CosemDateConverter());
    }

    // To check mapping of SetSpecialDaysRequest, when its
    // SpecialDaysRequestData is null
    @Test
    public void testSpecialDaysRequestMappingNull() {
        final String deviceIdentification = "nr1";
        final SpecialDaysRequestData specialDaysRequestData = null;
        final SetSpecialDaysRequest setSpecialDaysRequest = new SetSpecialDaysRequest();
        setSpecialDaysRequest.setDeviceIdentification(deviceIdentification);
        setSpecialDaysRequest.setSpecialDaysRequestData(specialDaysRequestData);

        final SpecialDaysRequest specialDaysRequest = this.mapperFactory.getMapperFacade().map(setSpecialDaysRequest,
                SpecialDaysRequest.class);

        assertNotNull(specialDaysRequest);
        assertEquals(deviceIdentification, specialDaysRequest.getDeviceIdentification());
        assertNull(specialDaysRequest.getSpecialDaysRequestData());

    }

    // To check mapping of SetSpecialDaysRequest, when its
    // SpecialDaysRequestData has an empty List.
    @Test
    public void testSpecialDaysRequestMappingEmptyList() {
        final String deviceIdentification = "nr1";
        // If i'm correct, the SpecialDaysRequestData no-arg constructor will
        // result in an empty list being mapped
        final SpecialDaysRequestData specialDaysRequestData = new SpecialDaysRequestData();
        final SetSpecialDaysRequest setSpecialDaysRequest = new SetSpecialDaysRequest();
        setSpecialDaysRequest.setDeviceIdentification(deviceIdentification);
        setSpecialDaysRequest.setSpecialDaysRequestData(specialDaysRequestData);
        // actual mapping
        final SpecialDaysRequest specialDaysRequest = this.mapperFactory.getMapperFacade().map(setSpecialDaysRequest,
                SpecialDaysRequest.class);
        // check mapping
        assertNotNull(specialDaysRequest);
        assertEquals(deviceIdentification, specialDaysRequest.getDeviceIdentification());
        assertTrue(specialDaysRequest.getSpecialDaysRequestData().getSpecialDays().isEmpty());
    }

    // To check mapping of SetSpecialDaysRequest, when its
    // SpecialDaysRequestData has a filled List (1 entry)
    @Test
    public void testSetSpecialDaysRequestMappingFilledList() {
        // build test data
        final String deviceIdentification = "nr1";
        final int dayId = 1;
        final SpecialDaysRequestData specialDaysRequestData = new SpecialDaysRequestData();
        final SpecialDay specialDay = new SpecialDay();
        final byte[] cosemDate = { (byte) 0x07, (byte) 0xE0, 4, 6, 4 };
        specialDay.setDayId(dayId);
        specialDay.setSpecialDayDate(cosemDate);
        // To add a SpecialDay to the List, you need to use the getter in
        // combination with add()
        specialDaysRequestData.getSpecialDays().add(specialDay);
        final SetSpecialDaysRequest setSpecialDaysRequest = new SetSpecialDaysRequest();
        setSpecialDaysRequest.setDeviceIdentification(deviceIdentification);
        setSpecialDaysRequest.setSpecialDaysRequestData(specialDaysRequestData);
        // actual mapping
        final SpecialDaysRequest specialDaysRequest = this.mapperFactory.getMapperFacade().map(setSpecialDaysRequest,
                SpecialDaysRequest.class);
        // check mapping
        assertNotNull(specialDaysRequest);
        assertEquals(deviceIdentification, specialDaysRequest.getDeviceIdentification());

        assertNotNull(specialDaysRequest.getSpecialDaysRequestData());
        assertEquals(specialDaysRequestData.getSpecialDays().size(), specialDaysRequest.getSpecialDaysRequestData()
                .getSpecialDays().size());

        assertEquals(2016, specialDaysRequest.getSpecialDaysRequestData().getSpecialDays().get(0).getSpecialDayDate()
                .getYear());
        assertEquals(4, specialDaysRequest.getSpecialDaysRequestData().getSpecialDays().get(0).getSpecialDayDate()
                .getMonth());
        assertEquals(6, specialDaysRequest.getSpecialDaysRequestData().getSpecialDays().get(0).getSpecialDayDate()
                .getDayOfMonth());
        assertEquals(4, specialDaysRequest.getSpecialDaysRequestData().getSpecialDays().get(0).getSpecialDayDate()
                .getDayOfWeek());

        assertEquals(dayId, specialDaysRequest.getSpecialDaysRequestData().getSpecialDays().get(0).getDayId());
    }

    // Test mapping. If no byte[] is specified for SpecialDay, mapping should
    // fail.
    @Test(expected = NullPointerException.class)
    public void testWithoutByteArray() {
        // build test data
        final String deviceIdentification = "nr1";
        final int dayId = 1;
        final SpecialDaysRequestData specialDaysRequestData = new SpecialDaysRequestData();
        final SpecialDay specialDay = new SpecialDay();
        specialDay.setDayId(dayId);
        // To add a SpecialDay to the List, you need to use the getter in
        // combination with add()
        specialDaysRequestData.getSpecialDays().add(specialDay);
        final SetSpecialDaysRequest setSpecialDaysRequest = new SetSpecialDaysRequest();
        setSpecialDaysRequest.setDeviceIdentification(deviceIdentification);
        setSpecialDaysRequest.setSpecialDaysRequestData(specialDaysRequestData);
        // actual mapping
        this.mapperFactory.getMapperFacade().map(setSpecialDaysRequest, SpecialDaysRequest.class);

    }

}
