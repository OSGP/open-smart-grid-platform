/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

import lombok.Getter;
import lombok.ToString;
import org.joda.time.DateTime;

@ToString
@Getter
public class OutageDto implements Serializable {

    private static final long serialVersionUID = 3450617767283546874L;

    private final DateTime timestamp;
    private final Integer eventCode;
    private final String eventLogCategoryName;
    private final Long duration;

    public OutageDto(final DateTime timestamp, final Integer eventCode,
            final String eventLogCategoryName, final Long duration) {
        this.timestamp = timestamp;
        this.eventCode = eventCode;
        this.eventLogCategoryName = eventLogCategoryName;
        this.duration = duration;
    }
}
