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

import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.RetrievePushNotificationAlarmResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PushNotificationAlarm;

public class PushNotificationAlarmMappingTest {

    private MonitoringMapper monitoringMapper = new MonitoringMapper();
    private static final String DEVICE_ID = "id1";
    private static final AlarmType ALARMTYPE = AlarmType.CLOCK_INVALID;

    /**
     * Tests if a PushNotificationAlarm object is mapped correctly with a filled
     * Set.
     */
    @Test
    public void testPushNotificationAlarmMappingWithFilledSet() {

        // build test data
        final Set<AlarmType> alarms = new TreeSet<>();
        alarms.add(ALARMTYPE);
        final PushNotificationAlarm original = new PushNotificationAlarm(DEVICE_ID, alarms);

        // actual mapping
        final RetrievePushNotificationAlarmResponse mapped = this.monitoringMapper.map(original,
                RetrievePushNotificationAlarmResponse.class);

        // check mapping
        assertNotNull(mapped);
        assertNotNull(mapped.getDeviceIdentification());
        assertNotNull(mapped.getAlarmRegister());
        assertNotNull(mapped.getAlarmRegister().getAlarmTypes());
        assertNotNull(mapped.getAlarmRegister().getAlarmTypes().get(0));

        assertEquals(DEVICE_ID, mapped.getDeviceIdentification());
        assertEquals(ALARMTYPE.name(), mapped.getAlarmRegister().getAlarmTypes().get(0).name());
    }

    /**
     * Tests if a PushNotificationAlarm is mapped correctly with an empty Set.
     */
    @Test
    public void testPushNotificationAlarmMappingWithEmptySet() {

        // build test data
        final Set<AlarmType> alarms = new TreeSet<>();
        final PushNotificationAlarm original = new PushNotificationAlarm(DEVICE_ID, alarms);

        // actual mapping
        final RetrievePushNotificationAlarmResponse mapped = this.monitoringMapper.map(original,
                RetrievePushNotificationAlarmResponse.class);

        // check mapping
        assertNotNull(mapped);
        assertNotNull(mapped.getDeviceIdentification());
        assertNotNull(mapped.getAlarmRegister());
        assertNotNull(mapped.getAlarmRegister().getAlarmTypes());

        assertEquals(DEVICE_ID, mapped.getDeviceIdentification());
        assertTrue(mapped.getAlarmRegister().getAlarmTypes().isEmpty());
    }

    /**
     * Tests if mapping a PushNotificationAlarm object succeeds with a null Set.
     */
    @Test
    public void testPushNotificiationAlarmMappingWithNullSet() {

        // build test data
        final Set<AlarmType> alarms = null;
        final PushNotificationAlarm original = new PushNotificationAlarm(DEVICE_ID, alarms);

        // actual mapping
        final RetrievePushNotificationAlarmResponse mapped = this.monitoringMapper.map(original,
                RetrievePushNotificationAlarmResponse.class);

        // check mapping
        assertNotNull(mapped);
        assertNotNull(mapped.getDeviceIdentification());
        assertEquals(DEVICE_ID, mapped.getDeviceIdentification());

        // constructor for PushNotificationAlarm creates a new Set when passed a
        // null value, so we should check for an empty List
        assertNotNull(mapped.getAlarmRegister());
        assertNotNull(mapped.getAlarmRegister().getAlarmTypes());
        assertTrue(mapped.getAlarmRegister().getAlarmTypes().isEmpty());
    }
}
