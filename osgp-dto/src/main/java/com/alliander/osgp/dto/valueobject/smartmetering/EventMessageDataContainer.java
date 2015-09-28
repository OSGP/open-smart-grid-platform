package com.alliander.osgp.dto.valueobject.smartmetering;

import java.io.Serializable;
import java.util.List;

public class EventMessageDataContainer implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 1050716134214636543L;
    private List<Event> events;

    public EventMessageDataContainer(final List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return this.events;
    }
}
