/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ActivityCalendar implements Comparable<ActivityCalendar>, Serializable {

    private static final long serialVersionUID = -8278955482889960359L;

    private String calendarName;

    private Date activatePassiveCalendarTime;

    private List<SeasonProfile> seasonProfileList;

    public ActivityCalendar(final String calendarName, final Date activatePassiveCalendarTime,
            final List<SeasonProfile> seasonProfileList) {
        this.calendarName = calendarName;
        this.activatePassiveCalendarTime = new Date(activatePassiveCalendarTime.getTime());
        this.seasonProfileList = new ArrayList<SeasonProfile>(seasonProfileList);
        Collections.sort(this.seasonProfileList);
    }

    public String getCalendarName() {
        return this.calendarName;
    }

    public Date getActivatePassiveCalendarTime() {
        return new Date(this.activatePassiveCalendarTime.getTime());
    }

    public List<SeasonProfile> getSeasonProfileList() {
        return Collections.unmodifiableList(this.seasonProfileList);
    }

    @Override
    public String toString() {
        return "\nActivityCalendar [\n\tcalendarName=" + this.calendarName + ", \n\tseasonProfileList=\n\t\t"
                + this.seasonProfileList + "\n]";
    }

    @Override
    public int compareTo(final ActivityCalendar other) {
        return this.calendarName.compareTo(other.calendarName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.calendarName.hashCode();
        result = prime * result + this.activatePassiveCalendarTime.hashCode();
        result = prime * result + this.seasonProfileList.hashCode();
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
        if (!this.calendarName.equals(other.calendarName)) {
            return false;
        }
        if (!this.activatePassiveCalendarTime.equals(other.activatePassiveCalendarTime)) {
            return false;
        }
        if (!this.seasonProfileList.equals(other.seasonProfileList)) {
            return false;
        }
        return true;
    }

}
