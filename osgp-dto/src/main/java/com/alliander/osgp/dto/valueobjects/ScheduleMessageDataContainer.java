package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;
import java.util.List;

public class ScheduleMessageDataContainer implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 6516779611853805357L;

    private List<Schedule> scheduleList;

    public ScheduleMessageDataContainer(final List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public List<Schedule> getScheduleList() {
        return this.scheduleList;
    }
}
