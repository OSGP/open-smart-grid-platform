/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

import lombok.Getter;
import org.joda.time.DateTime;

@Getter
public class EventDto implements Serializable {

    private static final long serialVersionUID = 5484936946786037616L;
    private final DateTime timestamp;
    private final Integer eventCode;
    private final String eventLogCategoryName;
    private final Integer eventCounter;
    private final DateTime startTime;
    private final Long duration;

    public EventDto(final DateTime timestamp, final Integer eventCode, final Integer eventCounter,
            final String eventLogCategoryName) {
        assert EventLogCategoryDto.valueOf(eventLogCategoryName) != EventLogCategoryDto.POWER_FAILURE_EVENT_LOG;
        this.timestamp = timestamp;
        this.eventCode = eventCode;
        this.eventCounter = eventCounter;
        this.eventLogCategoryName = eventLogCategoryName;
        this.startTime = null;
        this.duration = null;
    }

    public EventDto(final DateTime endTime, final Integer eventCode, final String eventLogCategoryName,
            final DateTime startTime, final Long duration) {
        assert EventLogCategoryDto.valueOf(eventLogCategoryName) == EventLogCategoryDto.POWER_FAILURE_EVENT_LOG;
        this.timestamp = endTime;
        this.eventCode = eventCode;
        this.eventCounter = null;
        this.eventLogCategoryName = eventLogCategoryName;
        this.startTime = startTime;
        this.duration = duration;
    }
}
