package com.smartsocietyservices.osgp.domain.da.valueobjects;

import java.io.Serializable;

public class GetHealthStatusResponse implements Serializable {
    private String healthStatus;

    public GetHealthStatusResponse( final String healthStatus ) {
        this.healthStatus = healthStatus;
    }

    public String getHealthStatus() {
        return healthStatus;
    }
}
