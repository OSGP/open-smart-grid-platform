/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.support.ws.microgrids.adhocmanagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import com.alliander.osgp.adapter.ws.schema.microgrids.adhocmanagement.SetPoint;
import com.alliander.osgp.platform.cucumber.steps.Keys;

import com.alliander.osgp.platform.cucumber.inputparsers.DateInputParser;


public class SetPointBuilder {// implements CucumberBuilder<SetPoint>{
    protected double value;
    protected XMLGregorianCalendar startTime;
    protected XMLGregorianCalendar endTime;
    protected Integer id;
    protected String node;

    protected List<SetPoint> setPoints = new ArrayList<>();
    
    public SetPointBuilder() {
    }

    public SetPointBuilder withStartTime(XMLGregorianCalendar startTime) {
        this.startTime = startTime;
        return this;
    }

    public SetPointBuilder withEndTime(XMLGregorianCalendar endTime) {
        this.endTime = endTime;
        return this;
    }

    public SetPointBuilder withId(Integer id) {
        this.id = id;
        return this;
    }

    public SetPointBuilder withNode(String node) {
        this.node = node;
        return this;
    }

    public SetPointBuilder withValue(double value) {
        this.value = value;
        return this;
    }

    // @Override
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
    
 // @Override
    public SetPointBuilder withSettings(final Map<String, String> settings) {
        for (int i = 1; i <= this.count(settings, Keys.KEY_SETPOINT_ID); i++) {
            this.setPoints.add(this.withSettings(settings, i).build());
        }

        return this;
    }
    
    private SetPointBuilder withSettings(final Map<String, String> settings, final int index) {
        if (this.hasKey(settings, Keys.KEY_SETPOINT_START_TIME, index)) {
            this.withStartTime(DateInputParser.parse(getStringValue(settings, Keys.KEY_SETPOINT_START_TIME, index)));
        } 
        if (this.hasKey(settings, Keys.KEY_SETPOINT_END_TIME, index)) {
            this.withEndTime(DateInputParser.parse(getStringValue(settings, Keys.KEY_SETPOINT_END_TIME, index)));
        } 
        if (this.hasKey(settings, Keys.KEY_SETPOINT_ID, index)) {
            this.withId(Integer.parseInt(getStringValue(settings, Keys.KEY_SETPOINT_ID, index)));
        } 
        if (this.hasKey(settings, Keys.KEY_SETPOINT_NODE, index)) {
            this.withNode(getStringValue(settings, Keys.KEY_SETPOINT_NODE, index));
        } 
        if (this.hasKey(settings, Keys.KEY_SETPOINT_VALUE, index)) {
            this.withValue(Double.parseDouble(getStringValue(settings, Keys.KEY_SETPOINT_VALUE, index)));
        } 

        return this;
    }

    private int count(final Map<String, String> settings, final String keyPrefix) {
        for (int i = 10; i > 0; i--) {
            if (this.hasKey(settings, keyPrefix, i)) {
                return i;
            }
        }
        return 0;
    }
    
    private boolean hasKey(final Map<String, String> settings, final String keyPrefix, final int index) {
        return settings.containsKey(makeKey(keyPrefix, index));
    }

    private String makeKey(final String keyPrefix, int index) {
        return keyPrefix + "_" + index;
    }

    private String getStringValue(final Map<String, String> settings, final String keyPrefix, final int index) {
        String key = makeKey(keyPrefix, index);
        return settings.get(key);
    }

}
