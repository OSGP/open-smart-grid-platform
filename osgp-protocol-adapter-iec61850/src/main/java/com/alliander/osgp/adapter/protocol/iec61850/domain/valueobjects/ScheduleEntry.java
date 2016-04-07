/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects;

/**
 * Value object, containing all data that is written to a raley schedule
 */
public class ScheduleEntry {

    private final boolean enabled;
    private final TriggerType triggerType;
    private final ScheduleWeekday weekday;
    private final short time;
    private final boolean on;

    public ScheduleEntry(final boolean enabled, final TriggerType triggerType, final ScheduleWeekday weekday,
            final short time, final boolean on) {
        this.enabled = enabled;
        this.triggerType = triggerType;
        this.weekday = weekday;
        this.time = time;
        this.on = on;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public TriggerType getTriggerType() {
        return this.triggerType;
    }

    public ScheduleWeekday getWeekday() {
        return this.weekday;
    }

    public short getTime() {
        return this.time;
    }

    public boolean isOn() {
        return this.on;
    }

}