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

import java.util.Set;
import java.util.TreeSet;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.junit.Before;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.RetrievePushNotificationAlarmResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PushNotificationAlarm;

public class PushNotificationAlarmMappingTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    // This converter is needed because RetrievePushNotificationAlarmResponse
    // doesn't have a List<AlarmType>, but an AlarmRegister. The
    // List<AlarmType> is in that class.
    @Before
    public void init() {
        this.mapperFactory.getConverterFactory().registerConverter(new PushNotificationsAlarmConverter());
    }

    // Test to see if a PushNotificationAlarm object is mapped correctly with a
    // filled Set.
    @Test
    public void testPushNotificationAlarmMappingWithFilledSet() {

        // build test data
        final String deviceIdentification = "id1";
        final AlarmType alarmType = AlarmType.CLOCK_INVALID;
        final Set<AlarmType> alarms = new TreeSet<>();
        alarms.add(alarmType);
        final PushNotificationAlarm original = new PushNotificationAlarm(deviceIdentification, alarms);

        // actual mapping
        final RetrievePushNotificationAlarmResponse mapped = this.mapperFactory.getMapperFacade().map(original,
                RetrievePushNotificationAlarmResponse.class);

        // check mapping
        assertNotNull(mapped);
        assertEquals(original.getDeviceIdentification(), mapped.getDeviceIdentification());
        assertEquals(alarmType.name(), mapped.getAlarmRegister().getAlarmTypes().get(0).name());
    }

    // Test to see if a PushNotificationAlarm can be mapped with an empty Set
    @Test
    public void testPushNotificationAlarmMappingWithEmptySet() {

        // build test data
        final String deviceIdentification = "id1";
        final Set<AlarmType> alarms = new TreeSet<>();
        final PushNotificationAlarm original = new PushNotificationAlarm(deviceIdentification, alarms);

        // actual mapping
        final RetrievePushNotificationAlarmResponse mapped = this.mapperFactory.getMapperFacade().map(original,
                RetrievePushNotificationAlarmResponse.class);

        // check mapping
        assertNotNull(mapped);
        assertEquals(deviceIdentification, mapped.getDeviceIdentification());
        assertTrue(mapped.getAlarmRegister().getAlarmTypes().isEmpty());
    }

    // Test to see if mapping a PushNotificationAlarm object succeeds with a
    // null Set.
    @Test
    public void testPushNotificiationAlarmMappingWithNullSet() {

        // build test data
        final String deviceIdentification = "id1";
        final Set<AlarmType> alarms = null;
        final PushNotificationAlarm original = new PushNotificationAlarm(deviceIdentification, alarms);

        // actual mapping
        final RetrievePushNotificationAlarmResponse mapped = this.mapperFactory.getMapperFacade().map(original,
                RetrievePushNotificationAlarmResponse.class);

        // check mapping
        assertNotNull(mapped);
        assertEquals(deviceIdentification, mapped.getDeviceIdentification());
        // constructor for PushNotificationAlarm creates a new Set when passed a
        // null value, so we should check for an empty List
        assertTrue(mapped.getAlarmRegister().getAlarmTypes().isEmpty());
    }
}
