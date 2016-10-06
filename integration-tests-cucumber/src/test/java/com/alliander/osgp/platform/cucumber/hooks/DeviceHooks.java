/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.platform.cucumber.ApplicationConfig;

/**
 * helper class for devices to provide database access. It is used to prepare
 * the database beforehand, and test for the existence of specific records
 * afterwards.
 *
 */
@Component
public class DeviceHooks {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private DeviceRepository deviceRepository;

    public void deactivateDevice(final String deviceId) {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceId);
        device.setActive(false);
        this.deviceRepository.save(device);
    }

    public void activateDevice(final String deviceId) {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceId);
        device.setActive(true);
        this.deviceRepository.save(device);
    }
}