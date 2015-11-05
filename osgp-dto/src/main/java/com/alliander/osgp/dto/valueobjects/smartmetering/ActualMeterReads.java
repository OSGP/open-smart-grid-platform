package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class ActualMeterReads implements Serializable {
    private static final long serialVersionUID = -297320204916085999L;

    private long activeEnergyImportTariffOne;
    private long activeEnergyImportTariffTwo;
    private long activeEnergyExportTariffOne;
    private long activeEnergyExportTariffTwo;

    public long getActiveEnergyImportTariffOne() {
        return this.activeEnergyImportTariffOne;
    }

    public void setActiveEnergyImportTariffOne(final long activeEnergyImportTariffOne) {
        this.activeEnergyImportTariffOne = activeEnergyImportTariffOne;
    }

    public long getActiveEnergyImportTariffTwo() {
        return this.activeEnergyImportTariffTwo;
    }

    public void setActiveEnergyImportTariffTwo(final long activeEnergyImportTariffTwo) {
        this.activeEnergyImportTariffTwo = activeEnergyImportTariffTwo;
    }

    public long getActiveEnergyExportTariffOne() {
        return this.activeEnergyExportTariffOne;
    }

    public void setActiveEnergyExportTariffOne(final long activeEnergyExportTariffOne) {
        this.activeEnergyExportTariffOne = activeEnergyExportTariffOne;
    }

    public long getActiveEnergyExportTariffTwo() {
        return this.activeEnergyExportTariffTwo;
    }

    public void setActiveEnergyExportTariffTwo(final long activeEnergyExportTariffTwo) {
        this.activeEnergyExportTariffTwo = activeEnergyExportTariffTwo;
    }

}
