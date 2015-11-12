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

    private final Date logTime;
    private final long activeEnergyImportTariffOne;
    // will be empty for INTERVAL
    private final Long activeEnergyImportTariffTwo;
    private final long activeEnergyExportTariffOne;
    // will be empty for INTERVAL
    private final Long activeEnergyExportTariffTwo;
    private final PeriodType periodType;

    public PeriodicMeterReads(Date logTime, long activeEnergyImportTariffOne, Long activeEnergyImportTariffTwo, long activeEnergyExportTariffOne, Long activeEnergyExportTariffTwo, PeriodType periodType) {
        this.logTime = logTime;
        this.activeEnergyImportTariffOne = activeEnergyImportTariffOne;
        this.activeEnergyImportTariffTwo = activeEnergyImportTariffTwo;
        this.activeEnergyExportTariffOne = activeEnergyExportTariffOne;
        this.activeEnergyExportTariffTwo = activeEnergyExportTariffTwo;
        this.periodType = periodType;
    }

    public long getActiveEnergyImportTariffOne() {
        return this.activeEnergyImportTariffOne;
    }

    /**
     * will be empty for INTERVAL
     *
     * @return the value of ActiveEnergyImportTariffTwo
     */
    public Long getActiveEnergyImportTariffTwo() {
        return this.activeEnergyImportTariffTwo;
    }

    public long getActiveEnergyExportTariffOne() {
        return this.activeEnergyExportTariffOne;
    }

    /**
     * will be empty for INTERVAL
     *
     * @return the value of ActiveEnergyExportTariffTwo
     */
    public Long getActiveEnergyExportTariffTwo() {
        return this.activeEnergyExportTariffTwo;
    }

    public Date getLogTime() {
        return this.logTime;
    }

    public PeriodType getPeriodType() {
        return this.periodType;
    }

}
