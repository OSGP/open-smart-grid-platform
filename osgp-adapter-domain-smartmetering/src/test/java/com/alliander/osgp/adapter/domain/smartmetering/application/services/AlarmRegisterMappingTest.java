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
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmRegister;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmType;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmRegisterResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmTypeDto;

public class AlarmRegisterMappingTest {

    private MonitoringMapper monitoringMapper = new MonitoringMapper();

    // Constructor for AlarmRegister(Dto) doesn´t allow a null Set.
    @Test(expected = NullPointerException.class)
    public void testWithNullSet() {

        final Set<AlarmTypeDto> alarmTypes = null;
        new AlarmRegisterResponseDto(alarmTypes);

    }

    // Test if mapping with an empty set succeeds
    @Test
    public void testWithEmptySet() {
        // build test data
        final Set<AlarmTypeDto> alarmTypes = new TreeSet<>();
        final AlarmRegisterResponseDto alarmRegisterDto = new AlarmRegisterResponseDto(alarmTypes);
        // actual mapping
        final AlarmRegister alarmRegister = this.monitoringMapper.map(alarmRegisterDto, AlarmRegister.class);
        // test mapping
        assertNotNull(alarmRegister);
        assertTrue(alarmRegister.getAlarmTypes().isEmpty());

    }

    // Test if mapping with a non-empty set succeeds
    @Test
    public void testWithNonEmptySet() {
        // build test data
        final Set<AlarmTypeDto> alarmTypes = new TreeSet<>();
        alarmTypes.add(AlarmTypeDto.CLOCK_INVALID);
        final AlarmRegisterResponseDto alarmRegisterDto = new AlarmRegisterResponseDto(alarmTypes);
        // actual mapping
        final AlarmRegister alarmRegister = this.monitoringMapper.map(alarmRegisterDto, AlarmRegister.class);
        // test mapping
        assertNotNull(alarmRegister);
        assertEquals(alarmRegisterDto.getAlarmTypes().size(), alarmRegister.getAlarmTypes().size());
        assertTrue(alarmRegister.getAlarmTypes().contains(AlarmType.CLOCK_INVALID));
    }

}
