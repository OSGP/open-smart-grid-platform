/**
 * Copyright 2015 Smart Society Services B.V.
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityCalendar implements Comparable<ActivityCalendar>, Serializable {

    private static final long serialVersionUID = -8278955482889960359L;

    private String logicalName;

    private String calendarName;

    private List<SeasonProfile> seasonProfileList;

    public ActivityCalendar(final String logicalName, final String calendarName,
            final List<SeasonProfile> seasonProfileList) {
        super();
        this.logicalName = logicalName;
        this.calendarName = calendarName;
        this.seasonProfileList = new ArrayList<SeasonProfile>(seasonProfileList);
    }

    public String getLogicalName() {
        return this.logicalName;
    }

    public String getCalendarName() {
        return this.calendarName;
    }

    public List<SeasonProfile> getSeasonProfileList() {
        return Collections.unmodifiableList(this.seasonProfileList);
    }

    @Override
    public String toString() {
        return "ActivityCalendar [logicalName=" + this.logicalName + ", calendarName=" + this.calendarName
                + ", seasonProfileList=" + this.seasonProfileList + "]";
    }

    @Override
    public int compareTo(final ActivityCalendar other) {
        return other.logicalName.compareTo(this.logicalName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.calendarName == null) ? 0 : this.calendarName.hashCode());
        result = prime * result + ((this.logicalName == null) ? 0 : this.logicalName.hashCode());
        result = prime * result + ((this.seasonProfileList == null) ? 0 : this.seasonProfileList.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ActivityCalendar other = (ActivityCalendar) obj;
        if (this.calendarName == null) {
            if (other.calendarName != null) {
                return false;
            }
        } else if (!this.calendarName.equals(other.calendarName)) {
            return false;
        }
        if (this.logicalName == null) {
            if (other.logicalName != null) {
                return false;
            }
        } else if (!this.logicalName.equals(other.logicalName)) {
            return false;
        }
        if (this.seasonProfileList == null) {
            if (other.seasonProfileList != null) {
                return false;
            }
        } else if (!this.seasonProfileList.equals(other.seasonProfileList)) {
            return false;
        }
        return true;
    }

}
