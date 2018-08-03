/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.activitycalendar;

public class DayProfileAction {
    private String startTime;
    private int scriptId;

    public DayProfileAction(final String startTime, final int scriptId) {
        this.startTime = startTime;
        this.scriptId = scriptId;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public int getScriptId() {
        return this.scriptId;
    }

}
