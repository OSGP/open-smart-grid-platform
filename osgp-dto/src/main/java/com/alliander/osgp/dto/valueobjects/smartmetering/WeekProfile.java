/**
 * Copyright 2015 Smart Society Services B.V.
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class WeekProfile implements Comparable<WeekProfile>, Serializable {

    private static final long serialVersionUID = 2838240604182800624L;

    private String week_profile_name;

    private DayProfile monday;

    private DayProfile tuesday;

    private DayProfile wednesday;

    private DayProfile thursday;

    private DayProfile friday;

    private DayProfile saturday;

    private DayProfile sunday;

    public WeekProfile() {

    }

    public WeekProfile(final String week_profile_name, final DayProfile monday, final DayProfile tuesday,
            final DayProfile wednesday, final DayProfile thursday, final DayProfile friday, final DayProfile saturday,
            final DayProfile sunday) {
        super();
        this.week_profile_name = week_profile_name;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }

    public String getWeekProfileName() {
        return this.week_profile_name;
    }

    public void setWeekProfileName(final String week_profile_name) {
        this.week_profile_name = week_profile_name;
    }

    public DayProfile getMonday() {
        return this.monday;
    }

    public void setMonday(final DayProfile monday) {
        this.monday = monday;
    }

    public DayProfile getTuesday() {
        return this.tuesday;
    }

    public void setTuesday(final DayProfile tuesday) {
        this.tuesday = tuesday;
    }

    public DayProfile getWednesday() {
        return this.wednesday;
    }

    public void setWednesday(final DayProfile wednesday) {
        this.wednesday = wednesday;
    }

    public DayProfile getThursday() {
        return this.thursday;
    }

    public void setThursday(final DayProfile thursday) {
        this.thursday = thursday;
    }

    public DayProfile getFriday() {
        return this.friday;
    }

    public void setFriday(final DayProfile friday) {
        this.friday = friday;
    }

    public DayProfile getSaturday() {
        return this.saturday;
    }

    public void setSaturday(final DayProfile saturday) {
        this.saturday = saturday;
    }

    public DayProfile getSunday() {
        return this.sunday;
    }

    public void setSunday(final DayProfile sunday) {
        this.sunday = sunday;
    }

    @Override
    public String toString() {
        return "WeekProfile [week_profile_name=" + this.week_profile_name + ", monday=" + this.monday + ", tuesday="
                + this.tuesday + ", wednesday=" + this.wednesday + ", thursday=" + this.thursday + ", friday="
                + this.friday + ", saturday=" + this.saturday + ", sunday=" + this.sunday + "]";
    }

    @Override
    public int compareTo(final WeekProfile other) {
        return other.week_profile_name.compareTo(this.week_profile_name);
    }
}
