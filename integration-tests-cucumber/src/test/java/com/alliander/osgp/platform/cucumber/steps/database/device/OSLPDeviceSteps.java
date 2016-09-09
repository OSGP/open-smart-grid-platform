/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database.device;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.platform.cucumber.steps.database.DeviceSteps;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import cucumber.api.java.en.Given;

/**
 * OSLP device specific steps.
 */
public class OSLPDeviceSteps {
	
	private String DEFAULT_DEVICE_UID = "MTIzNA==";

    @Autowired
    private OslpDeviceRepository oslpDeviceRespository;

    @Given("^an oslp device$")
    public void anOslpDevice(final Map<String, String> settings) throws Throwable {
    	
    	// First create the device itself
    	DeviceSteps deviceSteps = new DeviceSteps();
    	deviceSteps.aDevice(settings);

    	// Now create the OSLP device in the OSLP database
        final String deviceIdentification = getString(settings, "DeviceIdentification", DeviceSteps.DEFAULT_DEVICE_IDENTIFICATION);
        final OslpDevice device = new OslpDevice(
        		getString(settings, "DeviceUid", DEFAULT_DEVICE_UID), 
        		deviceIdentification, 
        		getString(settings, "DeviceType", DeviceSteps.DEFAULT_DEVICE_TYPE));
        this.oslpDeviceRespository.save(device);
    }
}
