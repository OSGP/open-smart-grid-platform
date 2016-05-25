package com.alliander.osgp.platform.cucumber.device;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.support.DeviceId;

import cucumber.api.java.en.Given;

public class Device {
    @Autowired
    private DeviceId deviceId;

    @Given("^a device with DeviceID \"([^\"]*)\"$")
    public void aDeviceWithDeviceID(final String deviceId) throws Throwable {
        this.deviceId.setDeviceIdE(deviceId);
    }

    @Given("^a gas device with DeviceID \"([^\"]*)\"$")
    public void aGasDeviceWithDeviceID(final String deviceId) throws Throwable {
        this.deviceId.setDeviceIdG(deviceId);
    }
}
