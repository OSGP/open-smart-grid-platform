/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.smartsocietyservices.osgp.domain.da.valueobjects;

import org.joda.time.DateTime;

import java.io.Serializable;

public class GetPQValuesPeriodicRequest implements Serializable {

    private static final long serialVersionUID = 4776483459295815846L;

    private final String deviceIdentifier;
    private final DateTime from;
    private final DateTime to;

    public GetPQValuesPeriodicRequest( final String deviceIdentifier, final DateTime from, final DateTime to ) {

        this.deviceIdentifier = deviceIdentifier;
        this.from = from;
        this.to = to;
    }

    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public DateTime getFrom() {
        return from;
    }

    public DateTime getTo() {
        return to;
    }
}
