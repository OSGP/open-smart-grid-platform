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

import org.junit.Test;

import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmRegister;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmType;

public class AlarmRegisterMappingTest {

    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    // Test to see if a AlarmRegisterObject is mapped correctly with a filled
    // set (1 entry)
    @Test
    public void testWithFilledSet() {

        // build test data
        final AlarmType alarmType = AlarmType.CLOCK_INVALID;
        final Set<AlarmType> alarmTypeSet = new TreeSet<>();
        alarmTypeSet.add(alarmType);
        final AlarmRegister original = new AlarmRegister(alarmTypeSet);

        // actual mapping
        final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AlarmRegister mapped = this.mapperFactory
                .getMapperFacade().map(original,
                        com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AlarmRegister.class);

        // check mapping
        assertNotNull(mapped);
        assertEquals(original.getAlarmTypes().size(), mapped.getAlarmTypes().size());
        assertEquals(alarmType.name(), mapped.getAlarmTypes().get(0).name());
    }

    // Test to see if a AlarmRegisterObject is mapped correctly with an empty
    // Set.
    @Test
    public void testWithEmptySet() {

        // build test data
        final Set<AlarmType> alarmTypeSet = new TreeSet<>();
        final AlarmRegister original = new AlarmRegister(alarmTypeSet);

        // actual mapping
        final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AlarmRegister mapped = this.mapperFactory
                .getMapperFacade().map(original,
                        com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AlarmRegister.class);

        // check mapping
        assertNotNull(mapped);
        assertTrue(mapped.getAlarmTypes().isEmpty());

    }

}
