package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class GetAdministration implements Serializable {

    private static final long serialVersionUID = 2969090682230580484L;

    private final String deviceIdentification;

    public GetAdministration(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }
}
