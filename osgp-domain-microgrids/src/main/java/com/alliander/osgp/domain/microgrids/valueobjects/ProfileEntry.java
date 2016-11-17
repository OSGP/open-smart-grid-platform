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

public class ProfileEntry implements Serializable {

    private static final long serialVersionUID = -6843459709647885422L;

    private int id;
    private DateTime time;
    private double value;

    public ProfileEntry(final int id, final DateTime time, final double value) {
        this.id = id;
        this.time = time;
        this.value = value;
    }

    public int getId() {
        return this.id;
    }

    public DateTime getTime() {
        return this.time;
    }

    public double getValue() {
        return this.value;
    }
}
