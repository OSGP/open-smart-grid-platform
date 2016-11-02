package com.alliander.osgp.platform.dlms.cucumber.support;

import org.springframework.stereotype.Component;

@Component
public class DeviceId {
    private String deviceIdE;
    private String deviceIdG;
    private Short mbusChannel;

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

    public Short getMbusChannel() {
        return this.mbusChannel;
    }

    public void setMbusChannel(Short mbusChannel) {
        this.mbusChannel = mbusChannel;
    }
}
