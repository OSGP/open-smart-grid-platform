/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotification;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmType;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotificationDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotificationsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmTypeDto;

public class AlarmNotificationsMappingTest {

    private ConfigurationMapper configurationMapper = new ConfigurationMapper();

    // The Set may never be null. Tests if NullPointerException is thrown
    // when constructor uses a Set that is null.
    @Test(expected = NullPointerException.class)
    public void testWithNullSet() {

        // test data
        final Set<AlarmNotification> alarmNotificationSet = null;
        new AlarmNotifications(alarmNotificationSet);

    }

    // The Set may be empty. Tests if mapping with an empty Set succeeds.
    @Test
    public void testWithEmptySet() {

        // create test data
        final Set<AlarmNotification> alarmNotificationSet = new TreeSet<AlarmNotification>();
        final AlarmNotifications alarmNotifications = new AlarmNotifications(alarmNotificationSet);

        // actual mapping
        final AlarmNotificationsDto alarmNotificationsDto = this.configurationMapper.map(alarmNotifications,
                AlarmNotificationsDto.class);

        // check if mapping was successful
        assertNotNull(alarmNotifications);
        assertNotNull(alarmNotificationsDto);

        assertNotNull(alarmNotificationSet);
        assertNotNull(alarmNotificationsDto.getAlarmNotifications());

        assertTrue(alarmNotificationSet.isEmpty());
        assertTrue(alarmNotificationsDto.getAlarmNotifications().isEmpty());
        assertEquals(alarmNotificationSet.isEmpty(), alarmNotificationsDto.getAlarmNotifications().isEmpty());
    }

    // Tests if mapping with a Set with an entry succeeds.
    @Test
    public void testWithSet() {
        // create test data
        final AlarmNotification alarmNotification = new AlarmNotification(AlarmType.CLOCK_INVALID, true);
        final Set<AlarmNotification> alarmNotificationSet = new TreeSet<AlarmNotification>();
        alarmNotificationSet.add(alarmNotification);
        final AlarmNotifications alarmNotifications = new AlarmNotifications(alarmNotificationSet);

        // actual mapping
        final AlarmNotificationsDto alarmNotificationsDto = this.configurationMapper.map(alarmNotifications,
                AlarmNotificationsDto.class);

        // check if mapping was successful
        assertNotNull(alarmNotifications);
        assertNotNull(alarmNotificationsDto);
        assertNotNull(alarmNotificationSet);
        assertNotNull(alarmNotificationsDto.getAlarmNotifications());
        assertEquals(alarmNotificationSet.size(), alarmNotificationsDto.getAlarmNotifications().size());
        assertFalse(alarmNotificationSet.isEmpty());
        assertFalse(alarmNotificationsDto.getAlarmNotifications().isEmpty());
        assertEquals(alarmNotificationSet.isEmpty(), alarmNotificationsDto.getAlarmNotifications().isEmpty());

        // To see if there is an AlarmNotifictionDto with the same variables as
        // the AlarmNotification in the Set.
        final AlarmNotificationDto alarmNotificationDto = new AlarmNotificationDto(AlarmTypeDto.CLOCK_INVALID, true);
        assertTrue(alarmNotificationsDto.getAlarmNotifications().contains(alarmNotificationDto));
    }

}
