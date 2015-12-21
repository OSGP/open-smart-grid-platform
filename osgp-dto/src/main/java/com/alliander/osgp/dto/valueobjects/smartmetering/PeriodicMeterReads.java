package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.util.Date;

public class PeriodicMeterReads extends MeterReads {

    private static final long serialVersionUID = 2123390296585369208L;

    final AmrProfileStatusCode amrProfileStatusCode;

    public PeriodicMeterReads(final Date logTime, final long activeEnergyImportTariffOne,
            final Long activeEnergyImportTariffTwo, final long activeEnergyExportTariffOne,
            final Long activeEnergyExportTariffTwo) {
        super(logTime, activeEnergyImportTariffOne, activeEnergyImportTariffTwo, activeEnergyExportTariffOne,
                activeEnergyExportTariffTwo);

        this.amrProfileStatusCode = null;
    }

    public PeriodicMeterReads(final Date logTime, final long activeEnergyImportTariffOne,
            final Long activeEnergyImportTariffTwo, final long activeEnergyExportTariffOne,
            final Long activeEnergyExportTariffTwo, final AmrProfileStatusCode amrProfileStatusCode) {
        super(logTime, activeEnergyImportTariffOne, activeEnergyImportTariffTwo, activeEnergyExportTariffOne,
                activeEnergyExportTariffTwo);

        this.amrProfileStatusCode = amrProfileStatusCode;
    }

    public AmrProfileStatusCode getAmrProfileStatusCode() {
        return this.amrProfileStatusCode;
    }
}
