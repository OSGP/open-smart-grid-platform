/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

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
    SUCCESSFUL_SELFCHECK_AFTER_FIRMWARE_UPDATE(20);

    private int value;

    public static final EventType[] VALUES = EventType.values();

    EventType(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
