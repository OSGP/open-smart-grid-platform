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
import lombok.ToString;
import org.joda.time.DateTime;

@Getter
@ToString
public class Event implements Serializable {

    private static final long serialVersionUID = 4482313912422705642L;
    private final DateTime timestamp;
    private final Integer eventCode;
    private final Integer eventCounter;
    private final EventLogCategory eventLogCategory;

    public Event(final DateTime timestamp, final Integer eventCode, final Integer eventCounter,
            final EventLogCategory eventLogCategory) {
        this.timestamp = timestamp;
        this.eventCode = eventCode;
        this.eventCounter = eventCounter;
        this.eventLogCategory = eventLogCategory;
    }
}
