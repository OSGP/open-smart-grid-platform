/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.hooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;

/**
 * helper class for devices to provide database access. It is used to prepare
 * the database beforehand, and test for the existence of specific records
 * afterwards.
 *
 */
@Component
@Configuration
@PropertySource("file:/etc/osp/osgp-cucumber-response-data-smart-metering.properties")
public class DeviceHooks {

    @Autowired
    private DeviceRepository deviceRepository;

    public void inactivateDevice(String mbusDeviceId) {
        final Device mbusDevice = this.deviceRepository.findByDeviceIdentification(mbusDeviceId);
        mbusDevice.setActive(false);
        this.deviceRepository.save(mbusDevice);
    }

    public void activateDevice(String mbusDeviceId) {
        final Device mbusDevice = this.deviceRepository.findByDeviceIdentification(mbusDeviceId);
        mbusDevice.setActive(true);
        this.deviceRepository.save(mbusDevice);
    }
}
