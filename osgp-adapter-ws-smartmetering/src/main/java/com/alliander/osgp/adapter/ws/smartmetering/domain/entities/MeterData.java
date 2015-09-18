/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.domain.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

/**
 * An entity class which contains the information of a shipment file that was
 * imported
 */
@Entity
public class MeterData extends AbstractEntity {

    private static final long serialVersionUID = -156966569210717654L;

    // TODO add status

    @ManyToOne()
    @JoinColumn(name = "periodic_meter_data_id", nullable = false)
    private PeriodicMeterData periodicMeterData;

    @Column
    private Date logTime;

    @Column
    private long activeEnergyImportTariffOne;

    @Column
    private long activeEnergyImportTariffTwo;

    @Column
    private long activeEnergyExportTariffOne;

    @Column
    private long activeEnergyExportTariffTwo;

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

    public long getActiveEnergyImportTariffOne() {
        return this.activeEnergyImportTariffOne;
    }

    public void setActiveEnergyImportTariffOne(final long activeEnergyImportTariffOne) {
        this.activeEnergyImportTariffOne = activeEnergyImportTariffOne;
    }

    public long getActiveEnergyImportTariffTwo() {
        return this.activeEnergyImportTariffTwo;
    }

    public void setActiveEnergyImportTariffTwo(final long activeEnergyImportTariffTwo) {
        this.activeEnergyImportTariffTwo = activeEnergyImportTariffTwo;
    }

    public long getActiveEnergyExportTariffOne() {
        return this.activeEnergyExportTariffOne;
    }

    public void setActiveEnergyExportTariffOne(final long activeEnergyExportTariffOne) {
        this.activeEnergyExportTariffOne = activeEnergyExportTariffOne;
    }

    public long getActiveEnergyExportTariffTwo() {
        return this.activeEnergyExportTariffTwo;
    }

    public void setActiveEnergyExportTariffTwo(final long activeEnergyExportTariffTwo) {
        this.activeEnergyExportTariffTwo = activeEnergyExportTariffTwo;
    }

}