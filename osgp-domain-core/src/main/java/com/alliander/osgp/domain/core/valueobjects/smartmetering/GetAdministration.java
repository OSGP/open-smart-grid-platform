package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class GetAdministration implements Serializable {

    private static final long serialVersionUID = -1399391398920839144L;

    private String deviceIdentification;

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }
}
