/**
 * Copyright 2014-2016 Smart Society Services B.V.
 */
package com.alliander.osgp.domain.core.valueobjects;

import java.util.List;

public class FirmwareHistory {

    private String deviceIdentification;

    private List<Firmware> firmwares;

    public FirmwareHistory() {
        // Default constructor
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public List<Firmware> getFirmwares() {
        return this.firmwares;
    }

    public void setFirmwares(final List<Firmware> firmwares) {
        this.firmwares = firmwares;
    }
}
