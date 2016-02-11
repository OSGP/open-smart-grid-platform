/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.util.Date;

public class ActualMeterReads extends MeterReads implements UnitResponse {

    private static final long serialVersionUID = 4052150124072820551L;

    public ActualMeterReads(final Date logTime, final double activeEnergyImport, final double activeEnergyExport,
            final double activeEnergyImportTariffOne, final double activeEnergyImportTariffTwo,
            final double activeEnergyExportTariffOne, final double activeEnergyExportTariffTwo) {
        super(logTime, activeEnergyImport, activeEnergyExport, activeEnergyImportTariffOne, activeEnergyImportTariffTwo,
                activeEnergyExportTariffOne, activeEnergyExportTariffTwo);
    }

    @Override
    public final OsgpUnit getOsgpUnit() {
        return OsgpUnit.KWH;
    }

}
