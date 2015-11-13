/**
 * Copyright 2015 Smart Society Services B.V.
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Collection;

public class ActivityCalendar implements Comparable<ActivityCalendar>, Serializable {

    private static final long serialVersionUID = -8278955482889960359L;

    private String logicalName;

    private String calendarName;

    private Collection<SeasonProfile> seasonProfileCollection;

    public ActivityCalendar(final String logicalName, final String calendarName,
            final Collection<SeasonProfile> seasonProfileCollection) {
        super();
        this.logicalName = logicalName;
        this.calendarName = calendarName;
        this.seasonProfileCollection = seasonProfileCollection;
    }

    public String getLogicalName() {
        return this.logicalName;
    }

    public String getCalendarName() {
        return this.calendarName;
    }

    public Collection<SeasonProfile> getSeasonProfileCollection() {
        return this.seasonProfileCollection;
    }

    @Override
    public String toString() {
        return "ActivityCalendar [logicalName=" + this.logicalName + ", calendarName=" + this.calendarName
                + ", seasonProfileCollection=" + this.seasonProfileCollection + "]";
    }

    @Override
    public int compareTo(final ActivityCalendar o) {
        return o.logicalName.compareTo(this.logicalName);
    }
}
