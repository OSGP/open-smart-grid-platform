/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

import org.joda.time.DateTime;

public class TimePeriodDto implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 6977372977894642487L;

    private final DateTime startTime;
    private final DateTime endTime;

    public TimePeriodDto(final DateTime startTime, final DateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DateTime getStartTime() {
        return this.startTime;
    }

    public DateTime getEndTime() {
        return this.endTime;
    }
}
