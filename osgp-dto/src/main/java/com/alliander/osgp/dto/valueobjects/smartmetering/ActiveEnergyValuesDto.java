/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class ActiveEnergyValuesDto implements Serializable {

    private static final long serialVersionUID = 5775074609152210861L;

    private final DlmsMeterValueDto activeEnergyImport;
    private final DlmsMeterValueDto activeEnergyExport;
    private final DlmsMeterValueDto activeEnergyImportTariffOne;
    private final DlmsMeterValueDto activeEnergyImportTariffTwo;
    private final DlmsMeterValueDto activeEnergyExportTariffOne;
    private final DlmsMeterValueDto activeEnergyExportTariffTwo;

    public ActiveEnergyValuesDto(final DlmsMeterValueDto[] dlmsMeterValues) {
        this.activeEnergyImport = dlmsMeterValues[0];
        this.activeEnergyExport = dlmsMeterValues[1];
        this.activeEnergyImportTariffOne = dlmsMeterValues[2];
        this.activeEnergyImportTariffTwo = dlmsMeterValues[3];
        this.activeEnergyExportTariffOne = dlmsMeterValues[4];
        this.activeEnergyExportTariffTwo = dlmsMeterValues[5];
    }

    @Override
    public String toString() {
        return "ActiveEnergyValues[import=" + this.activeEnergyImport + ", export=" + this.activeEnergyExport
                + ", importTariffOne=" + this.activeEnergyImportTariffOne + ", importTariffTwo="
                + this.activeEnergyImportTariffTwo + ", exportTariffOne=" + this.activeEnergyExportTariffOne
                + ", exportTariffTwo=" + this.activeEnergyExportTariffTwo + "]";
    }

    public DlmsMeterValueDto getActiveEnergyImport() {
        return this.activeEnergyImport;
    }

    public DlmsMeterValueDto getActiveEnergyExport() {
        return this.activeEnergyExport;
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
}
