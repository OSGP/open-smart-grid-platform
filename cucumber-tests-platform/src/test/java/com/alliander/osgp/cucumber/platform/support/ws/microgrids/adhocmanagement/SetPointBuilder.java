/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.support.ws.microgrids.adhocmanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SetPoint;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.helpers.SettingsHelper;


public class SetPointBuilder {

    private List<SetPoint> setPoints = new ArrayList<>();

    private double value;
    private XMLGregorianCalendar startTime;
    private XMLGregorianCalendar endTime;
    private Integer id;
    private String node;

    public SetPointBuilder() {
    }

    public SetPointBuilder withStartTime(final XMLGregorianCalendar startTime) {
        this.startTime = startTime;
        return this;
    }

    public SetPointBuilder withEndTime(final XMLGregorianCalendar endTime) {
        this.endTime = endTime;
        return this;
    }

    public SetPointBuilder withId(final Integer id) {
        this.id = id;
        return this;
    }

    public SetPointBuilder withNode(final String node) {
        this.node = node;
        return this;
    }

    public SetPointBuilder withValue(final double value) {
        this.value = value;
        return this;
    }

    public SetPoint build() {
        final SetPoint setPoint = new SetPoint();
        setPoint.setStartTime(this.startTime);
        setPoint.setEndTime(this.endTime);
        setPoint.setId(this.id);
        setPoint.setNode(this.node);
        setPoint.setValue(this.value);
        return setPoint;
    }

    public List<SetPoint> buildList() {
        return this.setPoints;
    }

    public SetPointBuilder withSettings(final Map<String, String> settings, final int systemIndex) {
        if (!SettingsHelper.hasKey(settings, Keys.KEY_NUMBER_OF_SET_POINTS, systemIndex)) {
            throw new AssertionError("The Step DataTable must contain the number of set points for key \""
                    + SettingsHelper.makeKey(Keys.KEY_NUMBER_OF_SET_POINTS, systemIndex)
                    + "\" when creating a set data request.");
        }
        final int numberOfSetPoints = SettingsHelper.getIntegerValue(settings, Keys.KEY_NUMBER_OF_SET_POINTS,
                systemIndex);
        for (int i = 1; i <= numberOfSetPoints; i++) {
            this.setPoints.add(this.withSettings(settings, systemIndex, i).build());
        }

        return this;
    }

    private SetPointBuilder withSettings(final Map<String, String> settings, final int systemIndex, final int index) {
        final int[] indexes = { systemIndex, index };
        this.withStartTime(
                SettingsHelper.getXmlGregorianCalendarValue(settings, Keys.KEY_SETPOINT_START_TIME, indexes));
        this.withEndTime(
                SettingsHelper.getXmlGregorianCalendarValue(settings, Keys.KEY_SETPOINT_END_TIME, indexes));
        this.withId(SettingsHelper.getIntegerValue(settings, Keys.KEY_SETPOINT_ID, indexes));
        this.withNode(SettingsHelper.getStringValue(settings, Keys.KEY_SETPOINT_NODE, indexes));
        this.withValue(SettingsHelper.getDoubleValue(settings, Keys.KEY_SETPOINT_VALUE, indexes));

        return this;
    }
}
