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
public class PeriodicMeterReads extends AbstractEntity {

    private static final long serialVersionUID = -156966569210717654L;

    @Column
    private String correlationUid;

    @Column
    private String deviceIdentification;

    @OneToMany(mappedBy = "periodicMeterReads", targetEntity = MeterReads.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<MeterReads> meterReads;

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

    public String getCorrelationUid() {
        return this.correlationUid;
    }

    public void setCorrelationUid(final String correlationUid) {
        this.correlationUid = correlationUid;
    }

}