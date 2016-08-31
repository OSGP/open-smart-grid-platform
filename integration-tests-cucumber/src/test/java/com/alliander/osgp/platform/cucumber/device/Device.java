package com.alliander.osgp.platform.cucumber.device;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.cucumber.hooks.ScenarioHooks;
import com.alliander.osgp.platform.cucumber.support.DeviceId;

import cucumber.api.java.en.Given;

public class Device {
    @Autowired
    private DeviceId deviceId;

    @Autowired
    private ScenarioHooks scenarioHooks;

    @Given("^a device with DeviceID \"([^\"]*)\"$")
    public void aDeviceWithDeviceID(final String deviceId) throws Throwable {
        this.deviceId.setDeviceIdE(deviceId);
    }

    @Given("^a gas device with DeviceID \"([^\"]*)\"$")
    public void aGasDeviceWithDeviceID(final String deviceId) throws Throwable {
        this.deviceId.setDeviceIdG(deviceId);
    }

    @Given("^an inactive device with DeviceID \"([^\"]*)\"$")
    public void anInactiveDeviceWithDeviceID(final String deviceId) throws Throwable {
        this.deviceId.setDeviceIdE(deviceId);
        this.scenarioHooks.setDeviceIsActivateState(this.deviceId.getDeviceIdE(), false);
    }

    @Given("^an active device with DeviceID \"([^\"]*)\"$")
    public void anActiveDeviceWithDeviceID(final String deviceId) throws Throwable {
        this.deviceId.setDeviceIdE(deviceId);
        this.scenarioHooks.setDeviceIsActivateState(this.deviceId.getDeviceIdE(), true);
    }
}
