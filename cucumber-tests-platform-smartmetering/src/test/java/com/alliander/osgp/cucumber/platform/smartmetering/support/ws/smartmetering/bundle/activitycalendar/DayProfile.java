package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.activitycalendar;

import java.util.ArrayList;
import java.util.List;

public class DayProfile {
    private int dayId;
    private List<DayProfileAction> dayProfileActions;

    public DayProfile(final int dayId) {
        this.dayId = dayId;
        this.dayProfileActions = new ArrayList<>();
    }

    public int getDayId() {
        return this.dayId;
    }

    public List<DayProfileAction> getDayProfileActions() {
        return this.dayProfileActions;
    }
}
