package com.alliander.osgp.platform.cucumber.support;

import org.springframework.stereotype.Component;

@Component
public class ServiceEndpoint {
    private String serviceEndpoint;

    public void setServiceEndpoint(final String serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
    }

    public String getServiceEndpoint() {
        return this.serviceEndpoint;
    }
}
