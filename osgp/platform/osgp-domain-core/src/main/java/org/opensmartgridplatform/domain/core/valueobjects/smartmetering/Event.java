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
            final EventLogCategory eventLogCategory) {
        this.timestamp = timestamp;
        this.eventCode = eventCode;
        this.eventCounter = eventCounter;
        this.eventLogCategory = eventLogCategory;
        this.startTime = null;
        this.duration = null;
    }

    public Event(final DateTime endTime, final Integer eventCode, final EventLogCategory eventLogCategory,
            final DateTime startTime, final Long duration) {
        this.timestamp = endTime;
        this.eventCode = eventCode;
        this.eventCounter = null;
        this.eventLogCategory = eventLogCategory;
        this.startTime = startTime;
        this.duration = duration;
    }

    @Override
    public String toString() {
        if (eventLogCategory == EventLogCategory.POWER_FAILURE_EVENT_LOG) {
            return String.format("Event[startTime, endTime=%s, duration=%s, category=%s, type=%s%s]", this.startTime,
                    this.timestamp, this.duration, this.eventLogCategory.name(),
                    this.eventCode == null ? null : EventType.getByEventCode(this.eventCode),
                    this.eventCounter == null ? "" : ", counter=" + this.eventCounter);
        } else {
            return String.format("Event[time=%s, code=%s, category=%s, type=%s%s]", this.timestamp, this.eventCode,
                    this.eventLogCategory.name(),
                    this.eventCode == null ? null : EventType.getByEventCode(this.eventCode),
                    this.eventCounter == null ? "" : ", counter=" + this.eventCounter);
        }
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
