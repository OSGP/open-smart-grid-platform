package com.alliander.osgp.platform.cucumber.support;

import org.springframework.stereotype.Component;

@Component
public class DeviceId {
    private String deviceIdE;
    private String deviceIdG;
    private Short gasDeviceChannel;

    public void setDeviceIdE(final String deviceIdE) {
        this.deviceIdE = deviceIdE;
    }

    public String getDeviceIdE() {
        return this.deviceIdE;
    }

    public void setDeviceIdG(final String deviceIdG) {
        this.deviceIdG = deviceIdG;
    }

    public String getDeviceIdG() {
        return this.deviceIdG;
    }

    public Short getGasDeviceChannel() {
        return this.gasDeviceChannel;
    }

    public void setGasDeviceChannel(Short gasDeviceChannel) {
        this.gasDeviceChannel = gasDeviceChannel;
    }
}
