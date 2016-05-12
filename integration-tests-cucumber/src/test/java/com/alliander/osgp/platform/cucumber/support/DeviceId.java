package com.alliander.osgp.platform.cucumber.support;

import org.springframework.stereotype.Component;

@Component
public class DeviceId {
    private String deviceId;

    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return this.deviceId;
    }
}
