/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;
import java.util.List;

import org.joda.time.DateTime;

public class Schedule implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 7562344341386762082L;

    private WeekDayType weekDay;

    private DateTime startDay;

    private DateTime endDay;

    private ActionTimeType actionTime;

    private String time;

    private WindowType triggerWindow;

    private List<LightValue> lightValue;

    private TriggerType triggerType;

    private Integer index;

    private Boolean isEnabled;

    public WeekDayType getWeekDay() {
        return this.weekDay;
    }

    public void setWeekDay(final WeekDayType value) {
        this.weekDay = value;
    }

    public void setStartDay(final DateTime value) {
        this.startDay = value;
    }

    public DateTime getStartDay() {
        return this.startDay;
    }

    public void setEndDay(final DateTime value) {
        this.endDay = value;
    }

    public DateTime getEndDay() {
        return this.endDay;
    }

    public void setActionTime(final ActionTimeType value) {
        this.actionTime = value;
    }

    public ActionTimeType getActionTime() {
        return this.actionTime;
    }

    public void setTime(final String value) {
        this.time = value;
    }

    public String getTime() {
        return this.time;
    }

    public void setTriggerWindow(final WindowType value) {
        this.triggerWindow = value;
    }

    public WindowType getTriggerWindow() {
        return this.triggerWindow;
    }

    public void setLightValue(final List<LightValue> value) {
        this.lightValue = value;
    }

    public List<LightValue> getLightValue() {
        return this.lightValue;
    }

    public void setTriggerType(final TriggerType triggerType) {
        this.triggerType = triggerType;
    }

    public TriggerType getTriggerType() {
        return this.triggerType;
    }

    public Integer getIndex() {
        return this.index;
    }

    public void setIndex(final Integer index) {
        this.index = index;
    }

    public Boolean getIsEnabled() {
        return this.isEnabled;
    }

    public void setIsEnabled(final Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}
