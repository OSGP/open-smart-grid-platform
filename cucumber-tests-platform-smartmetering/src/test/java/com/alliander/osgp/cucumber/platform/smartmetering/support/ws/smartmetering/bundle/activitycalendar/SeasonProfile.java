/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.activitycalendar;

public class SeasonProfile {
    private String name;
    private String start;
    private String weekName;

    public SeasonProfile(final String name, final String start, final String weekName) {
        this.name = name;
        this.start = start;
        this.weekName = weekName;
    }

    public String getName() {
        return this.name;
    }

    public String getStart() {
        return this.start;
    }

    public String getWeekName() {
        return this.weekName;
    }
}
