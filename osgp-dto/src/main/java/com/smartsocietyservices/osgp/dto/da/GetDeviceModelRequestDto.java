package com.smartsocietyservices.osgp.dto.da;

import java.io.Serializable;

public class GetDeviceModelRequestDto implements Serializable {
    private final String deviceIdentifier;

    public GetDeviceModelRequestDto( final String deviceIdentifier ) {

        this.deviceIdentifier = deviceIdentifier;
    }

    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }
}
