/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc;

import java.util.Map;

import org.joda.time.DateTimeZone;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.SynchronizeTimeRequestData;

public class SynchronizeTimeRequestDataFactory {

    private SynchronizeTimeRequestDataFactory() {
        // Private constructor for utility class
    }

    public static SynchronizeTimeRequestData fromParameterMap(final Map<String, String> parameters) {

        final SynchronizeTimeRequestData requestData = new SynchronizeTimeRequestData();

        /*
         * Setup of deviation and DST information, that will make
         * SynchronizeTime configure a meter for time zone Europe/Amsterdam.
         *
         * This assumes the server time that will be synchronized is about the
         * same as the system time where this test code is executed and
         * configures deviation and DST according to the proper values for
         * Europe/Amsterdam at the time of execution.
         */
        final int deviation;
        final boolean dst;
        if (DateTimeZone.forID("Europe/Amsterdam").isStandardOffset(System.currentTimeMillis())) {
            // normal time / winter time, GMT+1
            deviation = -60;
            dst = false;
        } else {
            // summer time (DST), daylight savings active, GMT+2
            deviation = -120;
            dst = true;
        }

        requestData.setDeviation(deviation);
        requestData.setDst(dst);
        return requestData;
    }
}
