/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EventType {
    EVENTLOG_CLEARED(255),
    POWER_FAILURE(1),
    POWER_RETURNED(2),
    CLOCK_UPDATE(3),
    CLOCK_ADJUSTED_OLD_TIME(4),
    CLOCK_ADJUSTED_NEW_TIME(5),
    CLOCK_INVALID(6),
    REPLACE_BATTERY(7),
    BATTERY_VOLTAGE_LOW(8),
    TARIFF_ACTIVATED(9),
    ERROR_REGISTER_CLEARED(10),
    ALARM_REGISTER_CLEARED(11),
    HARDWARE_ERROR_PROGRAM_MEMORY(12),
    HARDWARE_ERROR_RAM(13),
    HARDWARE_ERROR_NV_MEMORY(14),
    WATCHDOG_ERROR(15),
    HARDWARE_ERROR_MEASUREMENT_SYSTEM(16),
    FIRMWARE_READY_FOR_ACTIVATION(17),
    FIRMWARE_ACTIVATED(18),
    PASSIVE_TARIFF_UPDATED(19),
    SUCCESSFUL_SELFCHECK_AFTER_FIRMWARE_UPDATE(20),
    TERMINAL_COVER_REMOVED(40),
    TERMINAL_COVER_CLOSED(41),
    STRONG_DC_FIELD_DETECTED(42),
    NO_STRONG_DC_FIELD_ANYMORE(43),
    METER_COVER_REMOVED(44),
    METER_COVER_CLOSED(45),
    FAILED_LOGIN_ATTEMPT(46),
    CONFIGURATION_CHANGE(47),
    METROLOGICAL_MAINTENANCE(71),
    TECHNICAL_MAINTENANCE(72),
    RETRIEVE_METER_READINGS_E(73),
    RETRIEVE_METER_READINGS_G(74),
    RETRIEVE_INTERVAL_DATA_E(75),
    RETRIEVE_INTERVAL_DATA_G(76),
    COMMUNICATION_ERROR_M_BUS_CHANNEL_1(100),
    COMMUNICATION_OK_M_BUS_CHANNEL_1(101),
    REPLACE_BATTERY_M_BUS_CHANNEL_1(102),
    FRAUD_ATTEMPT_M_BUS_CHANNEL_1(103),
    CLOCK_ADJUSTED_M_BUS_CHANNEL_1(104),
    NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1(105),
    PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_1(106),
    COMMUNICATION_ERROR_M_BUS_CHANNEL_2(110),
    COMMUNICATION_OK_M_BUS_CHANNEL_2(111),
    REPLACE_BATTERY_M_BUS_CHANNEL_2(112),
    FRAUD_ATTEMPT_M_BUS_CHANNEL_2(113),
    CLOCK_ADJUSTED_M_BUS_CHANNEL_2(114),
    NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2(115),
    PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_2(116),
    COMMUNICATION_ERROR_M_BUS_CHANNEL_3(120),
    COMMUNICATION_OK_M_BUS_CHANNEL_3(121),
    REPLACE_BATTERY_M_BUS_CHANNEL_3(122),
    FRAUD_ATTEMPT_M_BUS_CHANNEL_3(123),
    CLOCK_ADJUSTED_M_BUS_CHANNEL_3(124),
    NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3(125),
    PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_3(126),
    COMMUNICATION_ERROR_M_BUS_CHANNEL_4(130),
    COMMUNICATION_OK_M_BUS_CHANNEL_4(131),
    REPLACE_BATTERY_M_BUS_CHANNEL_4(132),
    FRAUD_ATTEMPT_M_BUS_CHANNEL_4(133),
    CLOCK_ADJUSTED_M_BUS_CHANNEL_4(134),
    NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4(135),
    PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_4(136);

    private int value;

    private static final Map<Integer, EventType> lookup = new HashMap<Integer, EventType>();

    static {
        for (final EventType e : EnumSet.allOf(EventType.class)) {
            lookup.put(e.getValue(), e);
        }
    }

    public static EventType getValue(final int intValue) {
        return lookup.get(intValue);
    }

    private static final EventType[] VALUES = EventType.values();

    EventType(final int value) {
        this.value = value;
    }

    public static EventType[] getValues() {
        return VALUES;
    }

    public int getValue() {
        return this.value;
    }
}
