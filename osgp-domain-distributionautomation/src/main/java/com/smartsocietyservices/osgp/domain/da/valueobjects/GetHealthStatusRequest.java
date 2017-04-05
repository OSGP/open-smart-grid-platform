package com.smartsocietyservices.osgp.domain.da.valueobjects;

import java.io.Serializable;

public class GetHealthStatusRequest implements Serializable {
    private final String deviceIdentifier;

    public GetHealthStatusRequest( final String deviceIdentifier ) {
        this.deviceIdentifier = deviceIdentifier;
    }

    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }
}
