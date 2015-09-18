package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class SynchronizeTimeReadsRequestData implements Serializable {

	private static final long serialVersionUID = -1483665562035897062L;

    private String deviceIdentification;

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }
}
