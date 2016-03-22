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

public class MeterReadsDto implements Serializable {
    private static final long serialVersionUID = -297320204916085999L;

    private final Date logTime;

    private final DlmsMeterValueDto activeEnergyImport;
    private final DlmsMeterValueDto activeEnergyExport;
    private final DlmsMeterValueDto activeEnergyImportTariffOne;
    // may be null
    private final DlmsMeterValueDto activeEnergyImportTariffTwo;
    private final DlmsMeterValueDto activeEnergyExportTariffOne;
    // may be null
    private final DlmsMeterValueDto activeEnergyExportTariffTwo;

    public MeterReadsDto(final Date logTime, final DlmsMeterValueDto activeEnergyImport,
            final DlmsMeterValueDto activeEnergyExport, final DlmsMeterValueDto activeEnergyImportTariffOne,
            final DlmsMeterValueDto activeEnergyImportTariffTwo, final DlmsMeterValueDto activeEnergyExportTariffOne,
            final DlmsMeterValueDto activeEnergyExportTariffTwo) {
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

    public DlmsMeterValueDto getActiveEnergyImportTariffOne() {
        return this.activeEnergyImportTariffOne;
    }

    public DlmsMeterValueDto getActiveEnergyImportTariffTwo() {
        return this.activeEnergyImportTariffTwo;
    }

    public DlmsMeterValueDto getActiveEnergyExportTariffOne() {
        return this.activeEnergyExportTariffOne;
    }

    public DlmsMeterValueDto getActiveEnergyExportTariffTwo() {
        return this.activeEnergyExportTariffTwo;
    }

    public DlmsMeterValueDto getActiveEnergyImport() {
        return this.activeEnergyImport;
    }

    public DlmsMeterValueDto getActiveEnergyExport() {
        return this.activeEnergyExport;
    }

    @Override
    public String toString() {
        return "MeterReads [logTime=" + this.logTime + ", activeEnergyImport=" + this.activeEnergyImport
                + ", activeEnergyExport=" + this.activeEnergyExport + ", activeEnergyImportTariffOne="
                + this.activeEnergyImportTariffOne + ", activeEnergyImportTariffTwo="
                + this.activeEnergyImportTariffTwo + ", activeEnergyExportTariffOne="
                + this.activeEnergyExportTariffOne + ", activeEnergyExportTariffTwo="
                + this.activeEnergyExportTariffTwo + "]";
    }

}
