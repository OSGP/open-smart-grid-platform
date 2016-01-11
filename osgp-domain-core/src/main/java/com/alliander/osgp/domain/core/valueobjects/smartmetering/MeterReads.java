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

public abstract class MeterReads implements Serializable {

    private static final long serialVersionUID = -156966569210717654L;

    private final Date logTime;
    private final Long activeEnergyImport;
    private final Long activeEnergyExport;
    private final Long activeEnergyImportTariffOne;
    private final Long activeEnergyImportTariffTwo;
    private final Long activeEnergyExportTariffOne;
    private final Long activeEnergyExportTariffTwo;

    public MeterReads(final Date logTime, final Long activeEnergyImport, final Long activeEnergyExport,
            final Long activeEnergyImportTariffOne, final Long activeEnergyImportTariffTwo,
            final Long activeEnergyExportTariffOne, final Long activeEnergyExportTariffTwo) {
        this.logTime = logTime;
        this.activeEnergyImport = activeEnergyImport;
        this.activeEnergyExport = activeEnergyExport;
        this.activeEnergyImportTariffOne = activeEnergyImportTariffOne;
        this.activeEnergyImportTariffTwo = activeEnergyImportTariffTwo;
        this.activeEnergyExportTariffOne = activeEnergyExportTariffOne;
        this.activeEnergyExportTariffTwo = activeEnergyExportTariffTwo;
    }

    public Long getActiveEnergyImport() {
        return this.activeEnergyImport;
    }

    public Long getActiveEnergyExport() {
        return this.activeEnergyExport;
    }

    public Long getActiveEnergyImportTariffOne() {
        return this.activeEnergyImportTariffOne;
    }

    public Long getActiveEnergyImportTariffTwo() {
        return this.activeEnergyImportTariffTwo;
    }

    public Long getActiveEnergyExportTariffOne() {
        return this.activeEnergyExportTariffOne;
    }

    public Long getActiveEnergyExportTariffTwo() {
        return this.activeEnergyExportTariffTwo;
    }

    public Date getLogTime() {
        return this.logTime;
    }

}
