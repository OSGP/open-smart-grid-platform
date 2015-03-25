package com.alliander.osgp.domain.core.valueobjects;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

import com.alliander.osgp.domain.core.validation.TimePeriodConstraints;

@TimePeriodConstraints
public class TimePeriod implements Serializable {

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
