package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class ActualMeterReadsRequest implements Serializable {
    private static final long serialVersionUID = 3751586818507193990L;

    private String deviceIdentification;

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }
}
