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

public class PeriodicMeterReads implements Serializable {

    private static final long serialVersionUID = -156966569210717654L;

    // TODO add status

    private final Date logTime;
    private final long activeEnergyImportTariffOne;
    private final Long activeEnergyImportTariffTwo;
    private final long activeEnergyExportTariffOne;
    private final Long activeEnergyExportTariffTwo;
    private final PeriodType periodType;
    private final AmrProfileStatusCode amrProfileStatusses;

    public PeriodicMeterReads(final Date logTime, final long activeEnergyImportTariffOne,
            final Long activeEnergyImportTariffTwo, final long activeEnergyExportTariffOne,
            final Long activeEnergyExportTariffTwo, final PeriodType periodType,
            final AmrProfileStatusCode amrProfileStatusses) {
        this.logTime = logTime;
        this.activeEnergyImportTariffOne = activeEnergyImportTariffOne;
        this.activeEnergyImportTariffTwo = activeEnergyImportTariffTwo;
        this.activeEnergyExportTariffOne = activeEnergyExportTariffOne;
        this.activeEnergyExportTariffTwo = activeEnergyExportTariffTwo;
        this.periodType = periodType;
        this.amrProfileStatusses = amrProfileStatusses;
    }

    public long getActiveEnergyImportTariffOne() {
        return this.activeEnergyImportTariffOne;
    }

    public Long getActiveEnergyImportTariffTwo() {
        return this.activeEnergyImportTariffTwo;
    }

    public long getActiveEnergyExportTariffOne() {
        return this.activeEnergyExportTariffOne;
    }

    public Long getActiveEnergyExportTariffTwo() {
        return this.activeEnergyExportTariffTwo;
    }

    public Date getLogTime() {
        return this.logTime;
    }

    public PeriodType getPeriodType() {
        return this.periodType;
    }

    public AmrProfileStatusCode getAmrProfileStatusses() {
        return this.amrProfileStatusses;
    }

}
