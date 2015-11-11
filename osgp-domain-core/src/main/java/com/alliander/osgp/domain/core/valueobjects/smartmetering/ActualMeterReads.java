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
        this.logTime = logTime;
        this.activeEnergyImportTariffOne = activeEnergyImportTariffOne;
        this.activeEnergyImportTariffTwo = activeEnergyImportTariffTwo;
        this.activeEnergyExportTariffOne = activeEnergyExportTariffOne;
        this.activeEnergyExportTariffTwo = activeEnergyExportTariffTwo;
    }

    public Date getLogTime() {
        return this.logTime;
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
