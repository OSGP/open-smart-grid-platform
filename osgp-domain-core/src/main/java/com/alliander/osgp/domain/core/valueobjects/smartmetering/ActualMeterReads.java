/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.util.Date;

public class ActualMeterReads extends MeterReads {

    private static final long serialVersionUID = -5022361232673339316L;

    private final long activeEnergyImport;
    private final long activeEnergyExport;

    public ActualMeterReads(final Date logTime, final long activeEnergyImport, final long activeEnergyExport,
            final long activeEnergyImportTariffOne, final Long activeEnergyImportTariffTwo,
            final long activeEnergyExportTariffOne, final Long activeEnergyExportTariffTwo) {
        super(logTime, activeEnergyImportTariffOne, activeEnergyImportTariffTwo, activeEnergyExportTariffOne,
                activeEnergyExportTariffTwo);

        this.activeEnergyImport = activeEnergyImport;
        this.activeEnergyExport = activeEnergyExport;
    }

    public long getActiveEnergyImport() {
        return this.activeEnergyImport;
    }

    public long getActiveEnergyExport() {
        return this.activeEnergyExport;
    }
}
