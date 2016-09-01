package com.alliander.osgp.platform.cucumber.device;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.hooks.DeviceHooks;
import com.alliander.osgp.platform.cucumber.support.DeviceId;

import cucumber.api.java.en.Given;

public class Device {
    @Autowired
    private DeviceId deviceId;

    @Autowired
    private DeviceHooks deviceHooks;

    @Given("^a gas device with DeviceID \"([^\"]*)\"$")
    public void aGasDeviceWithDeviceID(final String deviceId) throws Throwable {
        this.deviceId.setDeviceIdG(deviceId);
    }

    @Given("^an active gas device with DeviceID \"([^\"]*)\"$")
    public void anActiveGasDeviceWithDeviceID(final String deviceId) throws Throwable {
        this.deviceId.setDeviceIdG(deviceId);
        this.deviceHooks.activateDevice(deviceId);
    }

    @Given("^an inactive gas device with DeviceID \"([^\"]*)\"$")
    public void anInActiveGasDeviceWithDeviceID(final String deviceId) throws Throwable {
        this.deviceId.setDeviceIdG(deviceId);
        this.deviceHooks.deactivateDevice(deviceId);
    }

    @Given("^a device with DeviceID \"([^\"]*)\"$")
    public void aDeviceWithDeviceID(final String deviceId) throws Throwable {
        this.deviceId.setDeviceIdE(deviceId);
    }

    @Given("^an inactive device with DeviceID \"([^\"]*)\"$")
    public void anInactiveDeviceWithDeviceID(final String deviceId) throws Throwable {
        this.deviceId.setDeviceIdE(deviceId);
        this.deviceHooks.deactivateDevice(deviceId);
    }

    @Given("^an active device with DeviceID \"([^\"]*)\"$")
    public void anActiveDeviceWithDeviceID(final String deviceId) throws Throwable {
        this.deviceId.setDeviceIdE(deviceId);
        this.deviceHooks.activateDevice(deviceId);
    }

    @Given("^an unknown gas device with DeviceID \"([^\"]*)\"$")
    public void anUnknownGasDeviceWithDeviceID(String gasDevice) throws Throwable {
        this.deviceId.setDeviceIdG(gasDevice);
    }

    @Given("^an unknown device with DeviceID \"([^\"]*)\"$")
    public void anUnkownDeviceWithDeviceID(String deviceIdE) throws Throwable {
        this.deviceId.setDeviceIdE(deviceIdE);
    }

}
