/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

import org.opensmartgridplatform.domain.core.validation.TimePeriodConstraints;

@TimePeriodConstraints
public class TimePeriod implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -1279128399397536492L;

    @NotNull
    private final DateTime startTime;

    @NotNull
    private final DateTime endTime;

    public TimePeriod(final DateTime startTime, final DateTime endTime) {
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
