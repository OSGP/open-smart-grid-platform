/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.joda.time.DateTime;

import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.ActionTimeType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.LightValue;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.Schedule;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.WeekDayType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.schedulemanagement.WindowType;

public class ScheduleBuilder {

    private WeekDayType weekDay;
    private DateTime startDay;
    private DateTime endDay;
    private ActionTimeType actionTime;
    private String time;
    private WindowType triggerWindow;
    private List<LightValue> lightValues;

    public ScheduleBuilder() {
        this.weekDay = WeekDayType.MONDAY;
        this.actionTime = ActionTimeType.SUNRISE;
        this.lightValues = new ArrayList<LightValue>();
    }

    public ScheduleBuilder withWeekDay(final WeekDayType value) {
        this.weekDay = value;
        return this;
    }

    public ScheduleBuilder withStartDay(final DateTime value) {
        this.startDay = value;
        return this;
    }

    public ScheduleBuilder withEndDay(final DateTime value) {
        this.endDay = value;
        return this;
    }

    public ScheduleBuilder withActionTime(final ActionTimeType value) {
        this.actionTime = value;
        return this;
    }

    public ScheduleBuilder withTriggerWindow(final WindowType value) {
        this.triggerWindow = value;
        return this;
    }

    public ScheduleBuilder withLightValue(final List<LightValue> values) {
        this.lightValues = values;
        return this;
    }

    public Schedule build() throws DatatypeConfigurationException {
        final Schedule schedule = new Schedule();
        schedule.setWeekDay(this.weekDay);
        if (this.startDay != null) {
            schedule.setStartDay(DatatypeFactory.newInstance().newXMLGregorianCalendar(this.startDay.toGregorianCalendar()));
        }
        if (this.endDay != null) {
            schedule.setEndDay(DatatypeFactory.newInstance().newXMLGregorianCalendar(this.endDay.toGregorianCalendar()));
        }
        schedule.setActionTime(this.actionTime);
        if (this.time != null) {
            schedule.setTime(this.time);
        }
        schedule.setTriggerWindow(this.triggerWindow);
        schedule.getLightValue().addAll(this.lightValues);
        return schedule;
    }
}