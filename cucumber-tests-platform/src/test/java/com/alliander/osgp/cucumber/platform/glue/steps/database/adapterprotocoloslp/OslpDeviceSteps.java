/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.database.adapterprotocoloslp;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.domain.repositories.OslpDeviceRepository;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.GlueBase;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.glue.steps.database.core.SsldDeviceSteps;

import cucumber.api.java.en.Given;

/**
 * OSLP device specific steps.
 */
public class OslpDeviceSteps extends GlueBase {

    public static final String DEFAULT_DEVICE_UID = "dGVzdDEyMzQ1Njc4";
    private static final String DEVICE_PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEFhUImXFJdqmputquVAc2CPdnn9Ju"
            + "00M3m/Ice7wABNN+oAYKQbw/OceqvZmFF1+r4nO/vCm/f1JO5nEorE2jNQ==";

    @Autowired
    private OslpDeviceRepository oslpDeviceRespository;

    @Autowired
    private SsldDeviceSteps ssldDeviceSteps;

    @Given("^an ssld oslp device$")
    public void anSsldOslpDevice(final Map<String, String> settings) throws Throwable {

        // First create the device itself
        this.ssldDeviceSteps.anSsldDevice(settings);

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
