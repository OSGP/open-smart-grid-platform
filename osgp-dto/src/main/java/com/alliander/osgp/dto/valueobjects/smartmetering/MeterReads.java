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

public abstract class MeterReads implements Serializable {
    private static final long serialVersionUID = -297320204916085999L;

    private Date logTime;

    private final Long activeEnergyImport;
    private final Long activeEnergyExport;
    private Long activeEnergyImportTariffOne;
    // may be null
    private Long activeEnergyImportTariffTwo;
    private Long activeEnergyExportTariffOne;
    // may be null
    private Long activeEnergyExportTariffTwo;

    MeterReads(final Date logTime, final Long activeEnergyImport, final Long activeEnergyExport,
            final Long activeEnergyImportTariffOne, final Long activeEnergyImportTariffTwo,
            final Long activeEnergyExportTariffOne, final Long activeEnergyExportTariffTwo) {
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

    public Long getActiveEnergyImport() {
        return this.activeEnergyImport;
    }

    public Long getActiveEnergyExport() {
        return this.activeEnergyExport;
    }

    @Override
    public String toString() {
        return "MeterReads [logTime=" + this.logTime + ", activeEnergyImportTariffOne="
                + this.activeEnergyImportTariffOne + ", activeEnergyImportTariffTwo="
                + this.activeEnergyImportTariffTwo + ", activeEnergyExportTariffOne="
                + this.activeEnergyExportTariffOne + ", activeEnergyExportTariffTwo="
                + this.activeEnergyExportTariffTwo + "]";
    }
}
