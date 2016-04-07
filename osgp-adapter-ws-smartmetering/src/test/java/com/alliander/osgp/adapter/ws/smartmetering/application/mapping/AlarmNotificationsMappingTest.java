/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.junit.Before;
import org.junit.Test;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.AlarmType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotification;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotifications;

public class AlarmNotificationsMappingTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    // Mapping a List to a Set needs a Converter?
    // List and set have a different name/getter. Can I change it?
    // Changed it before, got a lot of refactoring trouble.
    @Before
    public void init() {
        this.mapperFactory.getConverterFactory().registerConverter(new AlarmNotificationsConverter());
    }

    // Test to see if AlarmNotifications can be mapped.
    @Test
    public void testAlarmNotificationsMapping() {
        // build test data
        final AlarmNotification alarmNotification = new AlarmNotification();
        alarmNotification.setAlarmType(AlarmType.CLOCK_INVALID);
        alarmNotification.setEnabled(true);
        final AlarmNotifications alarmNotificationsOriginal = new AlarmNotifications();
        alarmNotificationsOriginal.getAlarmNotification().add(alarmNotification);
        // actual mapping
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications alarmNotificationsMapped = this.mapperFactory
                .getMapperFacade().map(alarmNotificationsOriginal,
                        com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications.class);
        // check mapping
        assertNotNull(alarmNotificationsMapped);
        assertNotNull(alarmNotificationsMapped.getAlarmNotificationsSet());
        assertFalse(alarmNotificationsMapped.getAlarmNotificationsSet().isEmpty());
        assertEquals(alarmNotificationsOriginal.getAlarmNotification().size(), alarmNotificationsMapped
                .getAlarmNotificationsSet().size());
        final com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotification alarmNotificationMapped = new com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotification(
                com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmType.CLOCK_INVALID, true);
        assertTrue(alarmNotificationsMapped.getAlarmNotificationsSet().contains(alarmNotificationMapped));

    }
}
