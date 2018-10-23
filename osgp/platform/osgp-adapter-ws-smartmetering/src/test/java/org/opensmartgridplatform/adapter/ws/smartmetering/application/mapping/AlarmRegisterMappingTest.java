/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.ws.smartmetering.application.mapping;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmRegister;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AlarmType;

public class AlarmRegisterMappingTest {

    private MonitoringMapper monitoringMapper = new MonitoringMapper();
    private static final AlarmType ALARMTYPE = AlarmType.CLOCK_INVALID;

    /**
     * Test to see if an AlarmRegister object is mapped correctly with a filled
     * Set.
     */
    @Test
    public void testWithFilledSet() {

        // build test data
        final Set<AlarmType> alarmTypeSet = new TreeSet<>();
        alarmTypeSet.add(ALARMTYPE);
        final AlarmRegister original = new AlarmRegister(alarmTypeSet);

        // actual mapping
        final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.AlarmRegister mapped = this.monitoringMapper
                .map(original, org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.AlarmRegister.class);

        // check mapping
        assertNotNull(mapped);
        assertNotNull(mapped.getAlarmTypes());
        assertNotNull(mapped.getAlarmTypes().get(0));
        assertEquals(original.getAlarmTypes().size(), mapped.getAlarmTypes().size());
        assertEquals(ALARMTYPE.name(), mapped.getAlarmTypes().get(0).name());
    }

    /**
     * Test to see if an AlarmRegister object is mapped correctly with an empty
     * Set.
     */
    @Test
    public void testWithEmptySet() {

        // build test data
        final Set<AlarmType> alarmTypeSet = new TreeSet<>();
        final AlarmRegister original = new AlarmRegister(alarmTypeSet);

        // actual mapping
        final org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.AlarmRegister mapped = this.monitoringMapper
                .map(original, org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.AlarmRegister.class);

        // check mapping
        assertNotNull(mapped);
        assertNotNull(mapped.getAlarmTypes());
        assertTrue(mapped.getAlarmTypes().isEmpty());

    }

}
