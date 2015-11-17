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

public class ActualMeterReads implements Serializable {
    private static final long serialVersionUID = -297320204916085999L;

    private Date logTime;
    private long activeEnergyImportTariffOne;
    private long activeEnergyImportTariffTwo;
    private long activeEnergyExportTariffOne;
    private long activeEnergyExportTariffTwo;

    public ActualMeterReads(final Date logTime, final long activeEnergyImportTariffOne,
            final long activeEnergyImportTariffTwo, final long activeEnergyExportTariffOne,
            final long activeEnergyExportTariffTwo) {
        super();
        this.logTime = new Date(logTime.getTime());
        this.activeEnergyImportTariffOne = activeEnergyImportTariffOne;
        this.activeEnergyImportTariffTwo = activeEnergyImportTariffTwo;
        this.activeEnergyExportTariffOne = activeEnergyExportTariffOne;
        this.activeEnergyExportTariffTwo = activeEnergyExportTariffTwo;
    }

    public Date getLogTime() {
        return new Date(this.logTime.getTime());
    }

    public long getActiveEnergyImportTariffOne() {
        return this.activeEnergyImportTariffOne;
    }

    public long getActiveEnergyImportTariffTwo() {
        return this.activeEnergyImportTariffTwo;
    }

    public long getActiveEnergyExportTariffOne() {
        return this.activeEnergyExportTariffOne;
    }

    public long getActiveEnergyExportTariffTwo() {
        return this.activeEnergyExportTariffTwo;
    }

    @Override
    public String toString() {
        return "ActualMeterReads [logTime=" + this.logTime + ", activeEnergyImportTariffOne="
                + this.activeEnergyImportTariffOne + ", activeEnergyImportTariffTwo="
                + this.activeEnergyImportTariffTwo + ", activeEnergyExportTariffOne="
                + this.activeEnergyExportTariffOne + ", activeEnergyExportTariffTwo="
                + this.activeEnergyExportTariffTwo + "]";
    }
}
