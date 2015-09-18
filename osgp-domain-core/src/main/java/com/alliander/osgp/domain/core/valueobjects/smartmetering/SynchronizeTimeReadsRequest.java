package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class SynchronizeTimeReadsRequest implements Serializable {

	private static final long serialVersionUID = -4368585364171307327L;

	private String deviceIdentification;
    private List<PeriodicMeterReadsRequestData> periodicMeterReadsRequestData;

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public List<PeriodicMeterReadsRequestData> getPeriodicMeterReadsRequestData() {
        return this.periodicMeterReadsRequestData;
    }

    public void setPeriodicMeterReadsRequestData(final List<PeriodicMeterReadsRequestData> periodicMeterReadsRequestData) {
        this.periodicMeterReadsRequestData = periodicMeterReadsRequestData;
    }

}
