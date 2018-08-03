/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;

public class MeterReads extends ActionResponse implements Serializable {
    private static final long serialVersionUID = -297320204916085999L;

    private final Date logTime;

    private final OsgpMeterValue activeEnergyImport;
    private final OsgpMeterValue activeEnergyExport;
    private final OsgpMeterValue activeEnergyImportTariffOne;
    // may be null
    private final OsgpMeterValue activeEnergyImportTariffTwo;
    private final OsgpMeterValue activeEnergyExportTariffOne;
    // may be null
    private final OsgpMeterValue activeEnergyExportTariffTwo;

    public MeterReads(final Date logTime, final OsgpMeterValue activeEnergyImport,
            final OsgpMeterValue activeEnergyExport, final OsgpMeterValue activeEnergyImportTariffOne,
            final OsgpMeterValue activeEnergyImportTariffTwo, final OsgpMeterValue activeEnergyExportTariffOne,
            final OsgpMeterValue activeEnergyExportTariffTwo) {
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

    public OsgpMeterValue getActiveEnergyImportTariffOne() {
        return this.activeEnergyImportTariffOne;
    }

    public OsgpMeterValue getActiveEnergyImportTariffTwo() {
        return this.activeEnergyImportTariffTwo;
    }

    public OsgpMeterValue getActiveEnergyExportTariffOne() {
        return this.activeEnergyExportTariffOne;
    }

    public OsgpMeterValue getActiveEnergyExportTariffTwo() {
        return this.activeEnergyExportTariffTwo;
    }

    public OsgpMeterValue getActiveEnergyImport() {
        return this.activeEnergyImport;
    }

    public OsgpMeterValue getActiveEnergyExport() {
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
