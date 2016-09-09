/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceFirmware;
import com.alliander.osgp.domain.core.entities.Firmware;
import com.alliander.osgp.domain.core.repositories.DeviceFirmwareRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;

import cucumber.api.java.en.Given;

public class DeviceFirmwareSteps {
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private DeviceFirmwareRepository deviceFirmwareRepository;
    
    /**
     * Generic method which adds a device firmware using the settings.
     * 
     * @param settings The settings for the device to be used.
     * @throws Throwable
     */
    @Given("^a device firmware$")
    public void aDeviceFirmware(final Map<String, String> settings) throws Throwable {
    	
    	// Get the device 
    	Device device = deviceRepository.findByDeviceIdentification(settings.get("DeviceIdentification"));
	
		Firmware firmware = new Firmware();
		firmware.setVersion(Long.parseLong(settings.get("FirmwareVersion")));
		
		DeviceFirmware deviceFirmware = new DeviceFirmware();
		
		deviceFirmware.setDevice(device);
		deviceFirmware.setFirmware(firmware);
		
		deviceFirmwareRepository.save(deviceFirmware);
	}
}
