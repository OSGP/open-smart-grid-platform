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

public class MeterReads implements Serializable {

    private static final long serialVersionUID = -156966569210717654L;

    // TODO add status

    private PeriodicMeterReads periodicMeterReads;
    private Date logTime;
    private long activeEnergyImportTariffOne;
    private long activeEnergyImportTariffTwo;
    private long activeEnergyExportTariffOne;
    private long activeEnergyExportTariffTwo;

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

    public PeriodicMeterReads getPeriodicMeterReads() {
        return this.periodicMeterReads;
    }

    public void setPeriodicMeterReads(final PeriodicMeterReads periodicMeterReads) {
        this.periodicMeterReads = periodicMeterReads;
    }

    public Date getLogTime() {
        return this.logTime;
    }

    public void setLogTime(final Date logTime) {
        this.logTime = logTime;
    }

}