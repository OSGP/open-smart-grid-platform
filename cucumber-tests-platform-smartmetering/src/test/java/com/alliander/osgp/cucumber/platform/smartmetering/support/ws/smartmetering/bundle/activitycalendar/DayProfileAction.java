package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.activitycalendar;

public class DayProfileAction {
    private String startTime;
    private int scriptId;

    public DayProfileAction(final String startTime, final int scriptId) {
        this.startTime = startTime;
        this.scriptId = scriptId;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public int getScriptId() {
        return this.scriptId;
    }

}
