package org.osgp.adapter.protocol.dlms.domain.commands;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmType;

public class AlarmHelperServiceTest {

    private final AlarmHelperService alarmHelperService = new AlarmHelperService();

    @Test
    public void testConvertToLong() {
        final Set<AlarmType> alarmTypes = new HashSet<>();

        alarmTypes.add(AlarmType.CLOCK_INVALID);
        alarmTypes.add(AlarmType.PROGRAM_MEMORY_ERROR);
        alarmTypes.add(AlarmType.WATCHDOG_ERROR);
        alarmTypes.add(AlarmType.COMMUNICATION_ERROR_M_BUS_CHANNEL_1);
        alarmTypes.add(AlarmType.FRAUD_ATTEMPT_M_BUS_CHANNEL_1);
        alarmTypes.add(AlarmType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1);

        assertEquals(17895681L, (long) this.alarmHelperService.toLongValue(alarmTypes));
    }

    @Test
    public void testConvertToAlarmTypes() {
        final long registerValue = Long.parseLong("00000001000100010001000100000001", 2);

        final Set<AlarmType> alarmTypes = this.alarmHelperService.toAlarmTypes(registerValue);

        assertTrue(alarmTypes.contains(AlarmType.CLOCK_INVALID));
        assertTrue(alarmTypes.contains(AlarmType.PROGRAM_MEMORY_ERROR));
        assertTrue(alarmTypes.contains(AlarmType.WATCHDOG_ERROR));
        assertTrue(alarmTypes.contains(AlarmType.COMMUNICATION_ERROR_M_BUS_CHANNEL_1));
        assertTrue(alarmTypes.contains(AlarmType.FRAUD_ATTEMPT_M_BUS_CHANNEL_1));
        assertTrue(alarmTypes.contains(AlarmType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1));
    }

    @Test
    public void testBitPositions() {
        assertEquals(0, (int) this.alarmHelperService.toBitPosition(AlarmType.CLOCK_INVALID));
        assertEquals(8, (int) this.alarmHelperService.toBitPosition(AlarmType.PROGRAM_MEMORY_ERROR));
        assertEquals(12, (int) this.alarmHelperService.toBitPosition(AlarmType.WATCHDOG_ERROR));
        assertEquals(16, (int) this.alarmHelperService.toBitPosition(AlarmType.COMMUNICATION_ERROR_M_BUS_CHANNEL_1));
        assertEquals(20, (int) this.alarmHelperService.toBitPosition(AlarmType.FRAUD_ATTEMPT_M_BUS_CHANNEL_1));
        assertEquals(24, (int) this.alarmHelperService.toBitPosition(AlarmType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1));
    }
}