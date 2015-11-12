/**
 * Copyright 2015 Smart Society Services B.V.
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

public class DayProfileAction implements Comparable<DayProfileAction>, Serializable {

    private static final long serialVersionUID = 3913348299915167189L;

    private String script_logical_name;

    private Integer script_selector;

    private Date start_time;

    private Collection<DayProfile> dayProfileCollection;

    public DayProfileAction() {

    }

    public DayProfileAction(final String script_logical_name, final Integer script_selector, final Date start_time,
            final Collection<DayProfile> dayProfileCollection) {
        super();
        this.script_logical_name = script_logical_name;
        this.script_selector = script_selector;
        this.start_time = start_time;
        this.dayProfileCollection = dayProfileCollection;
    }

    public String getScriptLogicalName() {
        return this.script_logical_name;
    }

    public void setScriptLogicalName(final String script_logical_name) {
        this.script_logical_name = script_logical_name;
    }

    public Integer getScript_selector() {
        return this.script_selector;
    }

    public void setScript_selector(final Integer script_selector) {
        this.script_selector = script_selector;
    }

    public Date getStart_time() {
        return this.start_time;
    }

    public void setStart_time(final Date start_time) {
        this.start_time = start_time;
    }

    public Collection<DayProfile> getDayProfileCollection() {
        return this.dayProfileCollection;
    }

    public void setDayProfileCollection(final Collection<DayProfile> dayProfileCollection) {
        this.dayProfileCollection = dayProfileCollection;
    }

    @Override
    public String toString() {
        return "DayProfileAction [script_logical_name=" + this.script_logical_name + ", script_selector="
                + this.script_selector + ", start_time=" + this.start_time + ", dayProfileCollection="
                + this.dayProfileCollection + "]";
    }

    @Override
    public int compareTo(final DayProfileAction o) {
        // TODO Auto-generated method stub
        return 0;
    }

}