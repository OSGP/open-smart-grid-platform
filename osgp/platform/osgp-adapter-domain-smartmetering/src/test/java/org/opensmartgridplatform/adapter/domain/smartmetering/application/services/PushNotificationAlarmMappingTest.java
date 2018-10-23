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

import java.util.Arrays;
import java.util.EnumSet;

import org.junit.Test;

import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmType;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushNotificationAlarm;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushNotificationAlarmDto;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

public class PushNotificationAlarmMappingTest {

    private final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    private final byte[] alarmBytes = "some bytes".getBytes();

    // Test if mapping a PushNotificationAlarm object succeeds with null
    // variables
    @Test
    public void testWithNullVariables() {
        // build test data
        final String deviceId = null;
        final EnumSet<AlarmTypeDto> alarms = null;
        final PushNotificationAlarmDto pushNotificationAlarmDto = new PushNotificationAlarmDto(deviceId, alarms,
                this.alarmBytes);
        // actual mapping
        final PushNotificationAlarm pushNotificationAlarm = this.mapperFactory.getMapperFacade()
                .map(pushNotificationAlarmDto, PushNotificationAlarm.class);
        // test mapping
        assertNotNull(pushNotificationAlarm);
        assertNull(pushNotificationAlarm.getDeviceIdentification());
        // the constructor creates an empty EnumSet when passed a null value.
        assertTrue(pushNotificationAlarmDto.getAlarms().isEmpty());
        assertTrue(pushNotificationAlarm.getAlarms().isEmpty());
        assertTrue("The alarm byes should be the same after the mapping",
                Arrays.equals(pushNotificationAlarm.getAlarmBytes(), this.alarmBytes));
    }

    // Test if mapping a PushNotificationAlarm object succeeds when the EnumSet
    // is empty
    @Test
    public void testWithEmptyEnumSet() {
        // build test data
        final String deviceId = "device1";
        final EnumSet<AlarmTypeDto> alarms = EnumSet.noneOf(AlarmTypeDto.class);
        final PushNotificationAlarmDto pushNotificationAlarmDto = new PushNotificationAlarmDto(deviceId, alarms,
                this.alarmBytes);
        // actual mapping
        final PushNotificationAlarm pushNotificationAlarm = this.mapperFactory.getMapperFacade()
                .map(pushNotificationAlarmDto, PushNotificationAlarm.class);
        // test mapping
        assertNotNull(pushNotificationAlarm);
        assertEquals(deviceId, pushNotificationAlarm.getDeviceIdentification());
        assertTrue(pushNotificationAlarm.getAlarms().isEmpty());
        assertTrue(Arrays.equals(pushNotificationAlarm.getAlarmBytes(), this.alarmBytes));
    }

    // Test if mapping a PushNotificationAlarm object succeeds when the EnumSet
    // is not empty
    @Test
    public void testWithNonEmptyEnumSet() {
        // build test data
        final String deviceId = "device1";
        final EnumSet<AlarmTypeDto> alarms = EnumSet.of(AlarmTypeDto.CLOCK_INVALID);
        final PushNotificationAlarmDto pushNotificationAlarmDto = new PushNotificationAlarmDto(deviceId, alarms,
                this.alarmBytes);
        // actual mapping
        final PushNotificationAlarm pushNotificationAlarm = this.mapperFactory.getMapperFacade()
                .map(pushNotificationAlarmDto, PushNotificationAlarm.class);
        // test mapping
        assertNotNull(pushNotificationAlarm);
        assertEquals(deviceId, pushNotificationAlarm.getDeviceIdentification());
        assertEquals(alarms.size(), pushNotificationAlarm.getAlarms().size());
        assertTrue(pushNotificationAlarm.getAlarms().contains(AlarmType.CLOCK_INVALID));
        assertTrue(Arrays.equals(pushNotificationAlarm.getAlarmBytes(), this.alarmBytes));
    }
}
