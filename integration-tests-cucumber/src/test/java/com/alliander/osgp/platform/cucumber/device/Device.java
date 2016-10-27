/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
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

    @Given("^a mbus device with DeviceID \"([^\"]*)\"$")
    public void aMbusDeviceWithDeviceID(final String deviceId) throws Throwable {
        this.deviceId.setDeviceIdG(deviceId);
    }

    @Given("^an active mbus device with DeviceID \"([^\"]*)\"$")
    public void anActiveMbusDeviceWithDeviceID(final String deviceId) throws Throwable {
        this.deviceId.setDeviceIdG(deviceId);
        this.deviceHooks.activateDevice(deviceId);
    }

    @Given("^an inactive mbus device with DeviceID \"([^\"]*)\"$")
    public void anInActiveMbusDeviceWithDeviceID(final String deviceId) throws Throwable {
        this.deviceId.setDeviceIdG(deviceId);
        this.deviceHooks.deactivateDevice(deviceId);
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

    @Given("^an active device with DeviceID \"([^\"]*)\" in debug mode$")
    public void anActiveDeviceWithDeviceIdInDebugMode(final String deviceId) throws Throwable {
        this.anActiveDeviceWithDeviceID(deviceId);
        this.deviceHooks.debugDevice(deviceId, true);
    }

    @Given("^an active device with DeviceID \"([^\"]*)\" not in debug mode$")
    public void anActiveDeviceWithDeviceIdNotInDebugMode(final String deviceId) throws Throwable {
        this.anActiveDeviceWithDeviceID(deviceId);
        this.deviceHooks.debugDevice(deviceId, false);
    }

    @Given("^an unknown mbus device with DeviceID \"([^\"]*)\"$")
    public void anUnknownMbusDeviceWithDeviceID(final String mbusDevice) throws Throwable {
        this.deviceId.setDeviceIdG(mbusDevice);
    }

    @Given("^an unknown device with DeviceID \"([^\"]*)\"$")
    public void anUnkownDeviceWithDeviceID(final String deviceIdE) throws Throwable {
        this.deviceId.setDeviceIdE(deviceIdE);
    }

}
