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
    private static final long serialVersionUID = -297320204916085999L;

    private final Date logTime;

    private final Double activeEnergyImport;
    private final Double activeEnergyExport;
    private final Double activeEnergyImportTariffOne;
    // may be null
    private final Double activeEnergyImportTariffTwo;
    private final Double activeEnergyExportTariffOne;
    // may be null
    private final Double activeEnergyExportTariffTwo;

    protected MeterReads(final Date logTime, final Double activeEnergyImport, final Double activeEnergyExport,
            final Double activeEnergyImportTariffOne, final Double activeEnergyImportTariffTwo,
            final Double activeEnergyExportTariffOne, final Double activeEnergyExportTariffTwo) {
        super();
        this.logTime = new Date(logTime.getTime());
        this.activeEnergyImportTariffOne = activeEnergyImportTariffOne;
        this.activeEnergyImportTariffTwo = activeEnergyImportTariffTwo;
        this.activeEnergyExportTariffOne = activeEnergyExportTariffOne;
        this.activeEnergyExportTariffTwo = activeEnergyExportTariffTwo;
        this.activeEnergyImport = activeEnergyImport;
        this.activeEnergyExport = activeEnergyExport;
    }

    public Date getLogTime() {
        return new Date(this.logTime.getTime());
    }

    public Double getActiveEnergyImportTariffOne() {
        return this.activeEnergyImportTariffOne;
    }

    public Double getActiveEnergyImportTariffTwo() {
        return this.activeEnergyImportTariffTwo;
    }

    public Double getActiveEnergyExportTariffOne() {
        return this.activeEnergyExportTariffOne;
    }

    public Double getActiveEnergyExportTariffTwo() {
        return this.activeEnergyExportTariffTwo;
    }

    public Double getActiveEnergyImport() {
        return this.activeEnergyImport;
    }

    public Double getActiveEnergyExport() {
        return this.activeEnergyExport;
    }

    @Override
    public String toString() {
        return "MeterReads [logTime=" + this.logTime + ", activeEnergyImport=" + this.activeEnergyImport
                + ", activeEnergyExport=" + this.activeEnergyExport + ", activeEnergyImportTariffOne="
                + this.activeEnergyImportTariffOne + ", activeEnergyImportTariffTwo=" + this.activeEnergyImportTariffTwo
                + ", activeEnergyExportTariffOne=" + this.activeEnergyExportTariffOne + ", activeEnergyExportTariffTwo="
                + this.activeEnergyExportTariffTwo + "]";
    }
}
