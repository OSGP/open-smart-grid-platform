package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class EventMessageDataContainer implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 3279405192677864979L;
    private List<Event> events;

    public EventMessageDataContainer(final List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return this.events;
    }

}
