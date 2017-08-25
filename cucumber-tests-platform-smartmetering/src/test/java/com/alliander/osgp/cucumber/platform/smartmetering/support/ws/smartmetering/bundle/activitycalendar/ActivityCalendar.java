package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.activitycalendar;

import java.util.HashMap;
import java.util.Map;

public class ActivityCalendar {
    private String name;
    private String activatePassiveCalendarTime;
    private Map<String, SeasonProfile> SeasonProfiles;
    private Map<String, WeekProfile> WeekProfiles;
    private Map<Integer, DayProfile> DayProfiles;

    public ActivityCalendar(final String name, final String activatePassiveCalendarTime) {
        this.name = name;
        this.activatePassiveCalendarTime = activatePassiveCalendarTime;
        this.SeasonProfiles = new HashMap<>();
        this.WeekProfiles = new HashMap<>();
        this.DayProfiles = new HashMap<>();
    }

    public String getName() {
        return this.name;
    }

    public String getActivatePassiveCalendarTime() {
        return this.activatePassiveCalendarTime;
    }

    public Map<String, SeasonProfile> getSeasonProfiles() {
        return this.SeasonProfiles;
    }

    public Map<String, WeekProfile> getWeekProfiles() {
        return this.WeekProfiles;
    }

    public Map<Integer, DayProfile> getDayProfiles() {
        return this.DayProfiles;
    }

}
