/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.util.Date;

public class PeriodicMeterReads extends MeterReads {

    private static final long serialVersionUID = 2123390296585369208L;

    final AmrProfileStatusCode amrProfileStatusCode;

    /**
     * Constructor taking all data. Use for conversion purposes, when all fields
     * need to be copied.
     *
     * @param logTime
     * @param activeEnergyImport
     * @param activeEnergyExport
     * @param activeEnergyImportTariffOne
     * @param activeEnergyImportTariffTwo
     * @param activeEnergyExportTariffOne
     * @param activeEnergyExportTariffTwo
     * @param amrProfileStatusCode
     */
    public PeriodicMeterReads(final Date logTime, final DlmsMeterValue activeEnergyImport,
            final DlmsMeterValue activeEnergyExport, final DlmsMeterValue activeEnergyImportTariffOne,
            final DlmsMeterValue activeEnergyImportTariffTwo, final DlmsMeterValue activeEnergyExportTariffOne,
            final DlmsMeterValue activeEnergyExportTariffTwo, final AmrProfileStatusCode amrProfileStatusCode) {
        super(logTime, activeEnergyImport, activeEnergyExport, activeEnergyImportTariffOne,
                activeEnergyImportTariffTwo, activeEnergyExportTariffOne, activeEnergyExportTariffTwo);

        this.amrProfileStatusCode = amrProfileStatusCode;
    }

    /**
     * Constructor for monthly reads. Does not hold a AMR profile status.
     *
     * @param logTime
     * @param activeEnergyImportTariffOne
     * @param activeEnergyImportTariffTwo
     * @param activeEnergyExportTariffOne
     * @param activeEnergyExportTariffTwo
     */
    public PeriodicMeterReads(final Date logTime, final DlmsMeterValue activeEnergyImportTariffOne,
            final DlmsMeterValue activeEnergyImportTariffTwo, final DlmsMeterValue activeEnergyExportTariffOne,
            final DlmsMeterValue activeEnergyExportTariffTwo) {

        this(logTime, null, null, activeEnergyImportTariffOne, activeEnergyImportTariffTwo,
                activeEnergyExportTariffOne, activeEnergyExportTariffTwo, null);
    }

    /**
     * Constructor for daily reads. Holds tariff values and AMR profile status.
     *
     * @param logTime
     * @param activeEnergyImportTariffOne
     * @param activeEnergyImportTariffTwo
     * @param activeEnergyExportTariffOne
     * @param activeEnergyExportTariffTwo
     * @param amrProfileStatusCode
     */
    public PeriodicMeterReads(final Date logTime, final DlmsMeterValue activeEnergyImportTariffOne,
            final DlmsMeterValue activeEnergyImportTariffTwo, final DlmsMeterValue activeEnergyExportTariffOne,
            final DlmsMeterValue activeEnergyExportTariffTwo, final AmrProfileStatusCode amrProfileStatusCode) {
        this(logTime, null, null, activeEnergyImportTariffOne, activeEnergyImportTariffTwo,
                activeEnergyExportTariffOne, activeEnergyExportTariffTwo, amrProfileStatusCode);
    }

    /**
     * Constructor for interval reads.
     *
     * @param logTime
     * @param activeEnergyImport
     * @param activeEnergyExport
     * @param amrProfileStatusCode
     */
    public PeriodicMeterReads(final Date logTime, final DlmsMeterValue activeEnergyImport,
            final DlmsMeterValue activeEnergyExport, final AmrProfileStatusCode amrProfileStatusCode) {
        super(logTime, activeEnergyImport, activeEnergyExport, null, null, null, null);

        this.amrProfileStatusCode = amrProfileStatusCode;
    }

    public AmrProfileStatusCode getAmrProfileStatusCode() {
        return this.amrProfileStatusCode;
    }

}
