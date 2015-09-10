/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.webdevicesimulator.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.webdevicesimulator.application.services.DeviceManagementService;
import com.alliander.osgp.webdevicesimulator.domain.entities.Device;

public class SwitchingServices {

    @Autowired
    private DeviceManagementService deviceManagementService;

    public void tariffSwitchHigh(final long deviceId) {
        // Find device
        Device device = this.deviceManagementService.findDevice(deviceId);
        device.setTariffOn(true);
        device = this.deviceManagementService.updateDevice(device);
    }

    public void tariffSwitchLow(final long deviceId) {
        // Find device
        Device device = this.deviceManagementService.findDevice(deviceId);
        device.setTariffOn(true);
        device = this.deviceManagementService.updateDevice(device);
    }

    public void lightSwitchOn(final long deviceId) {
        // Find device
        Device device = this.deviceManagementService.findDevice(deviceId);
        device.setLightOn(true);
        device = this.deviceManagementService.updateDevice(device);
    }

    public void lightSwitchOff(final long deviceId) {
        // Find device
        Device device = this.deviceManagementService.findDevice(deviceId);
        device.setLightOn(false);
        device = this.deviceManagementService.updateDevice(device);
    }

}
