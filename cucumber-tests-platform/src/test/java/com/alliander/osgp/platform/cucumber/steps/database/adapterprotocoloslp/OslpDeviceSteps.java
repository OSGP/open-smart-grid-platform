/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database.adapterprotocoloslp;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.database.core.DeviceSteps;

import cucumber.api.java.en.Given;

/**
 * OSLP device specific steps.
 */
public class OslpDeviceSteps {

    public static final String DEFAULT_DEVICE_UID = "dGVzdDEyMzQ1Njc4";
    private static final String DEVICE_PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEFhUImXFJdqmputquVAc2CPdnn9Ju"
            + "00M3m/Ice7wABNN+oAYKQbw/OceqvZmFF1+r4nO/vCm/f1JO5nEorE2jNQ==";

    @Autowired
    private OslpDeviceRepository oslpDeviceRespository;

    @Autowired
    private DeviceSteps deviceSteps;

    @Given("^an oslp device$")
    public void anOslpDevice(final Map<String, String> settings) throws Throwable {

        // First create the device itself
        this.deviceSteps.aDevice(settings);
        
        // Now create the OSLP device in the OSLP database
        final String deviceIdentification = getString(settings, Keys.KEY_DEVICE_IDENTIFICATION,
                Defaults.DEFAULT_DEVICE_IDENTIFICATION);
        final OslpDevice device = new OslpDevice(getString(settings, Keys.KEY_DEVICE_UID, DEFAULT_DEVICE_UID),
                deviceIdentification, getString(settings, Keys.KEY_DEVICE_TYPE, Defaults.DEFAULT_DEVICE_TYPE));
        device.setSequenceNumber(0);
        device.setRandomDevice(0);
        device.setRandomPlatform(0);
        device.updatePublicKey(DEVICE_PUBLIC_KEY);
        this.oslpDeviceRespository.save(device);
    }
}
