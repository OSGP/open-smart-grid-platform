/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

import lombok.Getter;
import org.joda.time.DateTime;

@Getter
public class Event implements Serializable {

    private static final long serialVersionUID = 4482313912422705642L;
    private final DateTime timestamp;
    private final Integer eventCode;
    private final EventLogCategory eventLogCategory;
    private final Integer eventCounter;
    private final DateTime startTime;
    private final Long duration;

    public Event(final DateTime timestamp, final Integer eventCode, final Integer eventCounter,
            final EventLogCategory eventLogCategory, final DateTime startTime, final Long duration) {
        this.timestamp = timestamp;
        this.eventCode = eventCode;
        this.eventCounter = eventCounter;
        this.eventLogCategory = eventLogCategory;
        this.startTime = startTime;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return String.format("Event[time=%s, code=%s, category=%s, type=%s%s%s%s]", this.timestamp, this.eventCode,
                this.eventLogCategory.name(), this.eventCode == null ? null : EventType.getByEventCode(this.eventCode),
                this.eventCounter == null ? "" : ", counter=" + this.eventCounter,
                this.startTime == null ? "" : ", startTime=" + this.startTime,
                this.duration == null ? "" : ". duration=" + this.duration);
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

    public EventLogCategory getEventLogCategory() {
        return this.eventLogCategory;
    }
}
