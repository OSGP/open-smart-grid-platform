/**
 * Copyright 2015 Smart Society Services B.V.
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;

public class SeasonProfile implements Comparable<SeasonProfile>, Serializable {

    private static final long serialVersionUID = -9110599718005128216L;

    private String season_profile_name;

    private Date season_start;

    private WeekProfile weekProfile;

    private ActivityCalendar activityCalendar;

    public SeasonProfile() {

    }

    public SeasonProfile(final String season_profile_name, final Date season_start, final WeekProfile weekProfile,
            final ActivityCalendar activityCalendar) {
        super();
        this.season_profile_name = season_profile_name;
        this.season_start = season_start;
        this.weekProfile = weekProfile;
        this.activityCalendar = activityCalendar;
    }

    public String getSeasonProfileName() {
        return this.season_profile_name;
    }

    public void setSeasonProfileName(final String season_profile_name) {
        this.season_profile_name = season_profile_name;
    }

    public Date getSeasonStart() {
        return this.season_start;
    }

    public void setSeasonStart(final Date season_start) {
        this.season_start = season_start;
    }

    public WeekProfile getWeekProfile() {
        return this.weekProfile;
    }

    public void setWeekProfile(final WeekProfile weekProfile) {
        this.weekProfile = weekProfile;
    }

    public ActivityCalendar getActivityCalendar() {
        return this.activityCalendar;
    }

    public void setActivityCalendar(final ActivityCalendar activityCalendar) {
        this.activityCalendar = activityCalendar;
    }

    @Override
    public String toString() {
        return "SeasonProfile [season_profile_name=" + this.season_profile_name + ", season_start=" + this.season_start
                + ", weekProfile=" + this.weekProfile + ", activityCalendar=" + this.activityCalendar + "]";
    }

    @Override
    public int compareTo(final SeasonProfile other) {
        return other.season_profile_name.compareTo(this.season_profile_name);
    }
}
