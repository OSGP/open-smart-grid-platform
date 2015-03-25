package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

import org.joda.time.DateTime;

public class TimePeriod implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 6977372977894642487L;

    private final DateTime startTime;
    private final DateTime endTime;

    public TimePeriod(final DateTime startTime, final DateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DateTime getStartTime() {
        return this.startTime;
    }

    public DateTime getEndTime() {
        return this.endTime;
    }
}
