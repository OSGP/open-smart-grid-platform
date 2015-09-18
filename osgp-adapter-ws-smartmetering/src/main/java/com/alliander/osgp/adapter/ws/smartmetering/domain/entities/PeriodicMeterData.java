/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.domain.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

/**
 * An entity class which contains the information of a shipment file that was
 * imported
 */
@Entity
public class PeriodicMeterData extends AbstractEntity {

    private static final long serialVersionUID = -156966569210717654L;

    @Column
    private String deviceIdentification;

    @OneToMany(mappedBy = "periodicMeterData", targetEntity = MeterData.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<MeterData> meterData;

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public List<MeterData> getMeterData() {
        return this.meterData;
    }

    public void setMeterData(final List<MeterData> meterData) {
        this.meterData = meterData;
    }

    public void addMeterData(final MeterData meterData) {
        this.meterData.add(meterData);
    }

}