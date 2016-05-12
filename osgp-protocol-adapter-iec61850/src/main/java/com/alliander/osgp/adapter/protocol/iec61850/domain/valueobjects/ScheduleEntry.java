/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects;

import org.joda.time.DateTime;

/**
 * Value object, containing all data that is written to a relay schedule
 */
public class ScheduleEntry {

    private final boolean enabled;
    private final TriggerType triggerType;
    private final int day;
    private final short time;
    private final boolean on;

    public ScheduleEntry(final boolean enabled, final TriggerType triggerType, final ScheduleWeekday weekday,
            final short time, final boolean on) {
        this.enabled = enabled;
        this.triggerType = triggerType;
        this.day = weekday.getIndex();
        this.time = time;
        this.on = on;
    }

    public ScheduleEntry(final boolean enabled, final TriggerType triggerType, final DateTime specialDate,
            final short time, final boolean on) {
        this.enabled = enabled;
        this.triggerType = triggerType;
        // make weekday the int value corresponding with yyyyMMdd
        this.day = specialDate.getDayOfMonth() + 100 * specialDate.getMonthOfYear() + 10000 * specialDate.getYear();
        this.time = time;
        this.on = on;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public TriggerType getTriggerType() {
        return this.triggerType;
    }

    public int getDay() {
        return this.day;
    }

    public short getTime() {
        return this.time;
    }

    public boolean isOn() {
        return this.on;
    }

}