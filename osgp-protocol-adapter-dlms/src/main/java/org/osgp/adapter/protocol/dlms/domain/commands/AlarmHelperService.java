package org.osgp.adapter.protocol.dlms.domain.commands;

import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmType;

@Service("alarmHelperService")
public class AlarmHelperService {
    /**
     * Gives the position of the alarm code as indicated by the AlarmType in the
     * bit string representation of the alarm register.
     * <p>
     * A position of 0 means the least significant bit, up to the maximum of 31
     * for the most significant bit. Since the 4 most significant bits in the
     * object are not used according to the DSMR documentation, the practical
     * meaningful most significant bit is bit 27.
     */
    private static final Map<AlarmType, Integer> ALARM_REGISTER_BIT_INDEX_PER_ALARM_TYPE;
    private static final Map<Integer, AlarmType> ALARM_TYPE_PER_REGISTER_BIT_INDEX;

    private static final int NUMBER_OF_BITS_IN_ALARM_FILTER = 32;

    static {
        final EnumMap<AlarmType, Integer> map = new EnumMap<>(AlarmType.class);

        // Bits for group: Other Alarms
        map.put(AlarmType.CLOCK_INVALID, 0);
        map.put(AlarmType.REPLACE_BATTERY, 1);
        map.put(AlarmType.POWER_UP, 2);
        // bits 3 to 7 are not used

        // Bits for group: Critical Alarms
        map.put(AlarmType.PROGRAM_MEMORY_ERROR, 8);
        map.put(AlarmType.RAM_ERROR, 9);
        map.put(AlarmType.NV_MEMORY_ERROR, 10);
        map.put(AlarmType.MEASUREMENT_SYSTEM_ERROR, 11);
        map.put(AlarmType.WATCHDOG_ERROR, 12);
        map.put(AlarmType.FRAUD_ATTEMPT, 13);
        // bits 14 and 15 are not used

        // Bits for group: M-Bus Alarms
        map.put(AlarmType.COMMUNICATION_ERROR_M_BUS_CHANNEL_1, 16);
        map.put(AlarmType.COMMUNICATION_ERROR_M_BUS_CHANNEL_2, 17);
        map.put(AlarmType.COMMUNICATION_ERROR_M_BUS_CHANNEL_3, 18);
        map.put(AlarmType.COMMUNICATION_ERROR_M_BUS_CHANNEL_4, 19);
        map.put(AlarmType.FRAUD_ATTEMPT_M_BUS_CHANNEL_1, 20);
        map.put(AlarmType.FRAUD_ATTEMPT_M_BUS_CHANNEL_2, 21);
        map.put(AlarmType.FRAUD_ATTEMPT_M_BUS_CHANNEL_3, 22);
        map.put(AlarmType.FRAUD_ATTEMPT_M_BUS_CHANNEL_4, 23);

        // Bits for group: Reserved
        map.put(AlarmType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1, 24);
        map.put(AlarmType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2, 25);
        map.put(AlarmType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3, 26);
        map.put(AlarmType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4, 27);
        // bits 28 to 31 are not used

        ALARM_REGISTER_BIT_INDEX_PER_ALARM_TYPE = Collections.unmodifiableMap(map);

        // Create a flipped version of the map.
        final HashMap<Integer, AlarmType> tempReversed = new HashMap<>();
        for (final Entry<AlarmType, Integer> val : ALARM_REGISTER_BIT_INDEX_PER_ALARM_TYPE.entrySet()) {
            tempReversed.put(val.getValue(), val.getKey());
        }

        ALARM_TYPE_PER_REGISTER_BIT_INDEX = Collections.unmodifiableMap(tempReversed);
    }

    /**
     * Returns the position of the bit value for the given AlarmType, in the
     * 4-byte register space.
     *
     * @param alarmType
     *            AlarmType
     * @return position of the bit holding the alarm type value.
     */
    public Integer toBitPosition(final AlarmType alarmType) {
        return ALARM_REGISTER_BIT_INDEX_PER_ALARM_TYPE.get(alarmType);
    }

    /**
     * Create a set of alarm types representing the active bits in the register
     * value.
     *
     * @param registerValue
     *            Value of the register.
     * @return List of active alarm types.
     */
    public Set<AlarmType> toAlarmTypes(final Long registerValue) {
        final Set<AlarmType> alarmTypes = new HashSet<>();

        final BitSet bitSet = BitSet.valueOf(new long[] { registerValue });
        for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
            alarmTypes.add(ALARM_TYPE_PER_REGISTER_BIT_INDEX.get(i));
        }

        return alarmTypes;
    }

    /**
     * Calculate the long value for the given set of AlarmTypes
     *
     * @param alarmTypes
     *            Set of AlarmTypes
     * @return Long value.
     */
    public Long toLongValue(final Set<AlarmType> alarmTypes) {
        final BitSet bitSet = new BitSet(NUMBER_OF_BITS_IN_ALARM_FILTER);
        for (final AlarmType alarmType : alarmTypes) {
            bitSet.set(this.toBitPosition(alarmType), true);
        }

        return bitSet.toLongArray()[0];
    }
}
