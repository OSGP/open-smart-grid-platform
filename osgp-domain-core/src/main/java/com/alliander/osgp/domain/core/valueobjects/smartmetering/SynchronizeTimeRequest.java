/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class SynchronizeTimeRequest implements Serializable {

    private static final long serialVersionUID = -2394318355946737102L;

    private String deviceIdentification;
    private List<SynchronizeTimeRequestData> synchronizeTimeRequestData;

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public List<SynchronizeTimeRequestData> getSynchronizeTimeRequestData() {
        return this.synchronizeTimeRequestData;
    }

    public void setSynchronizeTimeRequestData(
            final List<SynchronizeTimeRequestData> synchronizeTimeRequestData) {
        this.synchronizeTimeRequestData = synchronizeTimeRequestData;
    }
}
