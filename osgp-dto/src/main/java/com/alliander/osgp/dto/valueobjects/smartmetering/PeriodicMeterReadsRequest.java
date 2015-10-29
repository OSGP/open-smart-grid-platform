/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class PeriodicMeterReadsRequest implements Serializable {

    private static final long serialVersionUID = 2120695527724031374L;

    private String deviceIdentification;
    private List<PeriodicMeterReadsRequestData> periodicMeterReadsRequestData;

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public List<PeriodicMeterReadsRequestData> getPeriodicMeterReadsRequestData() {
        return this.periodicMeterReadsRequestData;
    }

    public void setPeriodicMeterReadsRequestData(final List<PeriodicMeterReadsRequestData> periodicMeterReadsRequestData) {
        this.periodicMeterReadsRequestData = periodicMeterReadsRequestData;
    }

}
