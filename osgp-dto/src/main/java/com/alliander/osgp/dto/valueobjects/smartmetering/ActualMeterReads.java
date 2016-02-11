/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.util.Date;

public class ActualMeterReads extends MeterReads {

    private static final long serialVersionUID = 4052150124072820551L;

    public ActualMeterReads(final Date logTime, final long activeEnergyImport, final long activeEnergyExport,
            final long activeEnergyImportTariffOne, final long activeEnergyImportTariffTwo,
            final long activeEnergyExportTariffOne, final long activeEnergyExportTariffTwo, final ScalerUnit scalerUnit) {
        super(logTime, activeEnergyImport, activeEnergyExport, activeEnergyImportTariffOne,
                activeEnergyImportTariffTwo, activeEnergyExportTariffOne, activeEnergyExportTariffTwo, scalerUnit);
    }

}
