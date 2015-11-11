/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;

public class PeriodicMeterReads implements Serializable {

    private static final long serialVersionUID = -156966569210717654L;

    // TODO add status

    private PeriodicMeterReadsContainer periodicMeterReadsContainer;
    private Date logTime;
    private long activeEnergyImportTariffOne;
    // will be empty for INTERVAL
    private Long activeEnergyImportTariffTwo;
    private long activeEnergyExportTariffOne;
    // will be empty for INTERVAL
    private Long activeEnergyExportTariffTwo;
    private PeriodType periodType;

    public long getActiveEnergyImportTariffOne() {
        return this.activeEnergyImportTariffOne;
    }

    public void setActiveEnergyImportTariffOne(final long activeEnergyImportTariffOne) {
        this.activeEnergyImportTariffOne = activeEnergyImportTariffOne;
    }

    /**
     * will be empty for INTERVAL
     *
     * @return
     */
    public Long getActiveEnergyImportTariffTwo() {
        return this.activeEnergyImportTariffTwo;
    }

    public void setActiveEnergyImportTariffTwo(final Long activeEnergyImportTariffTwo) {
        this.activeEnergyImportTariffTwo = activeEnergyImportTariffTwo;
    }

    public long getActiveEnergyExportTariffOne() {
        return this.activeEnergyExportTariffOne;
    }

    public void setActiveEnergyExportTariffOne(final long activeEnergyExportTariffOne) {
        this.activeEnergyExportTariffOne = activeEnergyExportTariffOne;
    }

    /**
     * will be empty for INTERVAL
     *
     * @return
     */
    public Long getActiveEnergyExportTariffTwo() {
        return this.activeEnergyExportTariffTwo;
    }

    public void setActiveEnergyExportTariffTwo(final Long activeEnergyExportTariffTwo) {
        this.activeEnergyExportTariffTwo = activeEnergyExportTariffTwo;
    }

    public PeriodicMeterReadsContainer getPeriodicMeterReads() {
        return this.periodicMeterReadsContainer;
    }

    public void setPeriodicMeterReads(final PeriodicMeterReadsContainer periodicMeterReadsContainer) {
        this.periodicMeterReadsContainer = periodicMeterReadsContainer;
    }

    public Date getLogTime() {
        return this.logTime;
    }

    public void setLogTime(final Date logTime) {
        this.logTime = logTime;
    }

    public PeriodType getPeriodType() {
        return this.periodType;
    }

    public void setPeriodType(final PeriodType periodType) {
        this.periodType = periodType;
    }

}