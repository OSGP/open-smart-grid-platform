/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;

public class MeterData implements Serializable {

    private static final long serialVersionUID = -156966569210717654L;

    // TODO add status

    private PeriodicMeterData periodicMeterData;
    private Date logTime;
    private Date captureTime;
    private long meterValue;

    public PeriodicMeterData getPeriodicMeterData() {
        return this.periodicMeterData;
    }

    public void setPeriodicMeterData(final PeriodicMeterData periodicMeterData) {
        this.periodicMeterData = periodicMeterData;
    }

    public Date getLogTime() {
        return this.logTime;
    }

    public void setLogTime(final Date logTime) {
        this.logTime = logTime;
    }

    public Date getCaptureTime() {
        return this.captureTime;
    }

    public void setCaptureTime(final Date captureTime) {
        this.captureTime = captureTime;
    }

    public long getMeterValue() {
        return this.meterValue;
    }

    public void setMeterValue(final long meterValue) {
        this.meterValue = meterValue;
    }

}