/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;

public class AlarmHelperServiceTest {

    private final AlarmHelperService alarmHelperService = new AlarmHelperService();

    @Test
    public void testConvertToLong() {
        final Set<AlarmTypeDto> alarmTypes = new HashSet<>();

        alarmTypes.add(AlarmTypeDto.CLOCK_INVALID);
        alarmTypes.add(AlarmTypeDto.PROGRAM_MEMORY_ERROR);
        alarmTypes.add(AlarmTypeDto.WATCHDOG_ERROR);
        alarmTypes.add(AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_1);
        alarmTypes.add(AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_1);
        alarmTypes.add(AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1);

        assertEquals(17895681L, (long) this.alarmHelperService.toLongValue(alarmTypes));
    }

    @Test
    public void testConvertToAlarmTypes() {
        final long registerValue = Long.parseLong("00000001000100010001000100000001", 2);

        final Set<AlarmTypeDto> alarmTypes = this.alarmHelperService.toAlarmTypes(registerValue);

        assertTrue(alarmTypes.contains(AlarmTypeDto.CLOCK_INVALID));
        assertTrue(alarmTypes.contains(AlarmTypeDto.PROGRAM_MEMORY_ERROR));
        assertTrue(alarmTypes.contains(AlarmTypeDto.WATCHDOG_ERROR));
        assertTrue(alarmTypes.contains(AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_1));
        assertTrue(alarmTypes.contains(AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_1));
        assertTrue(alarmTypes.contains(AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1));
    }

    @Test
    public void testBitPositions() {
        assertEquals(0, (int) this.alarmHelperService.toBitPosition(AlarmTypeDto.CLOCK_INVALID));
        assertEquals(8, (int) this.alarmHelperService.toBitPosition(AlarmTypeDto.PROGRAM_MEMORY_ERROR));
        assertEquals(12, (int) this.alarmHelperService.toBitPosition(AlarmTypeDto.WATCHDOG_ERROR));
        assertEquals(16, (int) this.alarmHelperService.toBitPosition(AlarmTypeDto.COMMUNICATION_ERROR_M_BUS_CHANNEL_1));
        assertEquals(20, (int) this.alarmHelperService.toBitPosition(AlarmTypeDto.FRAUD_ATTEMPT_M_BUS_CHANNEL_1));
        assertEquals(24,
                (int) this.alarmHelperService.toBitPosition(AlarmTypeDto.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1));
    }
}
