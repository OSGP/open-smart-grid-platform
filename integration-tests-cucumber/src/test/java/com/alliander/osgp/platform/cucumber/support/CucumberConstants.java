/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.support;

public interface CucumberConstants {

    String ORGANISATION_ID = "OrganisationIdentification";
    String DEVICE_ID = "DeviceIdentificationE";

    /**
     * The values below can be used to increase or decrease the maximum polling
     * time to the response database. the total polling time =
     * laptime*maxlapcount (where laptime = time in milisecs.
     *
     * So for example if the feature 'FastFeature' normally finishes within 10
     * seconds, the in FastFeature.java these lines could be added:
     * PROPERTIES_MAP.put(LAP_TIME, "500"); PROPERTIES_MAP.put(MAX_LAPCOUNT,
     * "100"); Hence instead of polling every 5 second, now we poll every half
     * second, and this should be finished within 50 seconds (500*100 = 50000
     * msecs)
     *
     */
    String LAP_TIME = "LapTime";
    String MAX_LAPCOUNT = "MaxLapCount";
}
