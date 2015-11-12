/**
 * Copyright 2015 Smart Society Services B.V.
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Collection;

public class ActivityCalendar implements Comparable<ActivityCalendar>, Serializable {

    private static final long serialVersionUID = -8278955482889960359L;

    private String logical_name;

    private String calendar_name;

    private Collection<SeasonProfile> seasonProfileCollection;

    public ActivityCalendar() {

    }

    public ActivityCalendar(final String logical_name, final String calendar_name,
            final Collection<SeasonProfile> seasonProfileCollection) {
        super();
        this.logical_name = logical_name;
        this.logical_name = calendar_name;
        this.seasonProfileCollection = seasonProfileCollection;
    }

    public String getLogicalName() {
        return this.logical_name;
    }

    public void setLogicalName(final String logical_name) {
        this.logical_name = logical_name;
    }

    public String getCalendarName() {
        return this.calendar_name;
    }

    public void setCalendarName(final String calendar_name) {
        this.calendar_name = calendar_name;
    }

    public Collection<SeasonProfile> getSeasonProfileCollection() {
        return this.seasonProfileCollection;
    }

    public void setSeasonProfileCollection(final Collection<SeasonProfile> seasonProfileCollection) {
        this.seasonProfileCollection = seasonProfileCollection;
    }

    @Override
    public String toString() {
        return "ActivityCalendar [logical_name=" + this.logical_name + ", calendar_name=" + this.calendar_name
                + ", seasonProfileCollection=" + this.seasonProfileCollection + "]";
    }

    @Override
    public int compareTo(final ActivityCalendar o) {
        // TODO Auto-generated method stub
        return 0;
    }

}