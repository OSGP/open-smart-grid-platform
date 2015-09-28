package com.alliander.osgp.dto.valueobject.smartmetering;

import java.io.Serializable;

import org.joda.time.DateTime;

public class FindEventsQuery implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 8250454329135950249L;

    private final EventLogCategory eventLogCategory;
    private final DateTime from;
    private final DateTime until;

    public FindEventsQuery(final EventLogCategory eventLogCategory, final DateTime from, final DateTime until) {
        this.eventLogCategory = eventLogCategory;
        this.from = from;
        this.until = until;
    }

    public EventLogCategory getEventLogCategory() {
        return this.eventLogCategory;
    }

    public DateTime getFrom() {
        return this.from;
    }

    public DateTime getUntil() {
        return this.until;
    }

}
