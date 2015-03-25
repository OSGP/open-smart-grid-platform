package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

public class EventNotification implements Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 5665838352689024852L;

    private final String deviceUid;
    private final EventType eventType;
    private final String description;
    private final Integer index;

    public EventNotification(final String deviceUid, final EventType eventType, final String description,
            final Integer index) {
        this.deviceUid = deviceUid;
        this.eventType = eventType;
        this.description = description;
        this.index = index;
    }

    // TODO: remove this one?
    public String getDeviceUid() {
        return this.deviceUid;
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public String getDescription() {
        return this.description;
    }

    public Integer getIndex() {
        return this.index;
    }
}
