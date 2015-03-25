package com.alliander.osgp.domain.core.valueobjects;

import java.io.Serializable;
import java.util.List;

public class ScheduleMessageDataContainer implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 280691205768966372L;

    private List<Schedule> scheduleList;

    public ScheduleMessageDataContainer(final List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public List<Schedule> getScheduleList() {
        return this.scheduleList;
    }
}
