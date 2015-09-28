package com.alliander.osgp.domain.core.valueobjects;

import java.io.Serializable;

import org.joda.time.DateTime;

public class Event implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 4482313912422705642L;
    private DateTime timestamp;
    private Integer eventCode;

    public Event(final DateTime timestamp, final Integer eventCode) {
        this.timestamp = timestamp;
        this.eventCode = eventCode;
    }

    public DateTime getTimestamp() {
        return this.timestamp;
    }

    public Integer getEventCode() {
        return this.eventCode;
    }

}
