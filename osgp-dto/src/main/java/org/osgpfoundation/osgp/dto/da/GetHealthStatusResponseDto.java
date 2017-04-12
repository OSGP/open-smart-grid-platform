package org.osgpfoundation.osgp.dto.da;

import java.io.Serializable;

public class GetHealthStatusResponseDto implements Serializable {
    private String healthStatus;

    public GetHealthStatusResponseDto( final String healthStatus ) {
        this.healthStatus = healthStatus;
    }

    public String getHealthStatus() {
        return healthStatus;
    }
}
