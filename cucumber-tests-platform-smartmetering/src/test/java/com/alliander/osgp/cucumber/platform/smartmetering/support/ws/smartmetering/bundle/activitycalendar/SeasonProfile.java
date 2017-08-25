package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.activitycalendar;

public class SeasonProfile {
    private String name;
    private String start;
    private String weekName;

    public SeasonProfile(final String name, final String start, final String weekName) {
        this.name = name;
        this.start = start;
        this.weekName = weekName;
    }

    public String getName() {
        return this.name;
    }

    public String getStart() {
        return this.start;
    }

    public String getWeekName() {
        return this.weekName;
    }
}
