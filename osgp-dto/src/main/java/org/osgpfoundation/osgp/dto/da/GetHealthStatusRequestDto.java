package org.osgpfoundation.osgp.dto.da;

import java.io.Serializable;

public class GetHealthStatusRequestDto implements Serializable {
    private final String deviceIdentifier;

    public GetHealthStatusRequestDto( final String deviceIdentifier ) {
        this.deviceIdentifier = deviceIdentifier;
    }

    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }
}
