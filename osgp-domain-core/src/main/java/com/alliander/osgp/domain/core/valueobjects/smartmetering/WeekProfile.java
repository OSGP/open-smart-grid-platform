/**
 * Copyright 2015 Smart Society Services B.V.
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class WeekProfile implements Comparable<WeekProfile>, Serializable {

    private static final long serialVersionUID = 2838240604182800624L;

    private String weekProfileName;

    private DayProfile monday;

    private DayProfile tuesday;

    private DayProfile wednesday;

    private DayProfile thursday;

    private DayProfile friday;

    private DayProfile saturday;

    private DayProfile sunday;

    public WeekProfile(final String weekProfileName, final DayProfile monday, final DayProfile tuesday,
            final DayProfile wednesday, final DayProfile thursday, final DayProfile friday, final DayProfile saturday,
            final DayProfile sunday) {
        super();
        this.weekProfileName = weekProfileName;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }

    public String getWeekProfileName() {
        return this.weekProfileName;
    }

    public DayProfile getMonday() {
        return this.monday;
    }

    public DayProfile getTuesday() {
        return this.tuesday;
    }

    public DayProfile getWednesday() {
        return this.wednesday;
    }

    public DayProfile getThursday() {
        return this.thursday;
    }

    public DayProfile getFriday() {
        return this.friday;
    }

    public DayProfile getSaturday() {
        return this.saturday;
    }

    public DayProfile getSunday() {
        return this.sunday;
    }

    @Override
    public String toString() {
        return "WeekProfile [weekProfileName=" + this.weekProfileName + ", monday=" + this.monday + ", tuesday="
                + this.tuesday + ", wednesday=" + this.wednesday + ", thursday=" + this.thursday + ", friday="
                + this.friday + ", saturday=" + this.saturday + ", sunday=" + this.sunday + "]";
    }

    @Override
    public int compareTo(final WeekProfile o) {
        return o.weekProfileName.compareTo(this.weekProfileName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.friday == null) ? 0 : this.friday.hashCode());
        result = prime * result + ((this.monday == null) ? 0 : this.monday.hashCode());
        result = prime * result + ((this.saturday == null) ? 0 : this.saturday.hashCode());
        result = prime * result + ((this.sunday == null) ? 0 : this.sunday.hashCode());
        result = prime * result + ((this.thursday == null) ? 0 : this.thursday.hashCode());
        result = prime * result + ((this.tuesday == null) ? 0 : this.tuesday.hashCode());
        result = prime * result + ((this.wednesday == null) ? 0 : this.wednesday.hashCode());
        result = prime * result + ((this.weekProfileName == null) ? 0 : this.weekProfileName.hashCode());
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
        final WeekProfile other = (WeekProfile) obj;
        if (this.friday == null) {
            if (other.friday != null) {
                return false;
            }
        } else if (!this.friday.equals(other.friday)) {
            return false;
        }
        if (this.monday == null) {
            if (other.monday != null) {
                return false;
            }
        } else if (!this.monday.equals(other.monday)) {
            return false;
        }
        if (this.saturday == null) {
            if (other.saturday != null) {
                return false;
            }
        } else if (!this.saturday.equals(other.saturday)) {
            return false;
        }
        if (this.sunday == null) {
            if (other.sunday != null) {
                return false;
            }
        } else if (!this.sunday.equals(other.sunday)) {
            return false;
        }
        if (this.thursday == null) {
            if (other.thursday != null) {
                return false;
            }
        } else if (!this.thursday.equals(other.thursday)) {
            return false;
        }
        if (this.tuesday == null) {
            if (other.tuesday != null) {
                return false;
            }
        } else if (!this.tuesday.equals(other.tuesday)) {
            return false;
        }
        if (this.wednesday == null) {
            if (other.wednesday != null) {
                return false;
            }
        } else if (!this.wednesday.equals(other.wednesday)) {
            return false;
        }
        if (this.weekProfileName == null) {
            if (other.weekProfileName != null) {
                return false;
            }
        } else if (!this.weekProfileName.equals(other.weekProfileName)) {
            return false;
        }
        return true;
    }
}
