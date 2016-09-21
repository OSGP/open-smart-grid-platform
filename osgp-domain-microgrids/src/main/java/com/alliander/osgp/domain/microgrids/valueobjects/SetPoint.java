/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.microgrids.valueobjects;

import java.io.Serializable;

import org.joda.time.DateTime;

public class SetPoint implements Serializable {

    private static final long serialVersionUID = -8781688280636819412L;

    private final String node;
    private final double value;
    private final DateTime startTime;
    private final DateTime endTime;

    public SetPoint(final String node, final double value, final DateTime startTime, final DateTime endTime) {
        super();
        this.node = node;
        this.value = value;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getNode() {
        return this.node;
    }

    public double getValue() {
        return this.value;
    }

    public DateTime getStartTime() {
        return this.startTime;
    }

    public DateTime getEndTime() {
        return this.endTime;
    }
}
