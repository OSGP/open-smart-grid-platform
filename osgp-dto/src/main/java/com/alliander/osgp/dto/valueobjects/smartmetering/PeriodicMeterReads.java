/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PeriodicMeterReads implements Serializable {

    private static final long serialVersionUID = -156966569210717654L;

    private String deviceIdentification;
    private List<MeterReads> meterReads;

    public PeriodicMeterReads() {
        this.meterReads = new ArrayList<>();
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public List<MeterReads> getMeterReads() {
        return this.meterReads;
    }

    public void setMeterReads(final List<MeterReads> meterReads) {
        this.meterReads = meterReads;
    }

    public void addMeterReads(final MeterReads meterReads) {
        this.meterReads.add(meterReads);
    }

}