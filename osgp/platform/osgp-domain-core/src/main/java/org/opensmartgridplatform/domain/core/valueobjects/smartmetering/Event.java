/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

import org.joda.time.DateTime;

public class Event implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 4482313912422705642L;
    private DateTime timestamp;
    private Integer eventCode;
    private Integer eventCounter;

    public Event(final DateTime timestamp, final Integer eventCode, final Integer eventCounter) {
        this.timestamp = timestamp;
        this.eventCode = eventCode;
        this.eventCounter = eventCounter;
    }

    @Override
    public String toString() {
        return String.format("Event[time=%s, code=%s, type=%s%s]", this.timestamp, this.eventCode,
                this.eventCode == null ? null : EventType.getValue(this.eventCode.intValue()),
                        this.eventCounter == null ? "" : ", counter=" + this.eventCounter);
    }

    public DateTime getTimestamp() {
        return this.timestamp;
    }

    public Integer getEventCode() {
        return this.eventCode;
    }

    public Integer getEventCounter() {
        return this.eventCounter;
    }
}
