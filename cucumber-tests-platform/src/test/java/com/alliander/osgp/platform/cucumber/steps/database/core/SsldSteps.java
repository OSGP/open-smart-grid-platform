/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database.core;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getBoolean;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.platform.cucumber.steps.Defaults;

import cucumber.api.java.en.Given;

@Transactional("txMgrCore")
public class SsldSteps {

    @Autowired
    private SsldRepository ssldRepository;
    
    @Autowired
    private DeviceSteps deviceSteps;

    /**
     * Generic method which adds a device using the settings.
     *
     * @param settings
     *            The settings for the device to be used.
     * @throws Throwable
     */
    @Given("^a ssl device$")
    public void aSslDevice(final Map<String, String> settings) throws Throwable {
        
        // Set the required stuff
        final String deviceIdentification = settings.get("DeviceIdentification");
        final Ssld ssld = new Ssld(deviceIdentification);
        
        ssld.setPublicKeyPresent(getBoolean(settings, "PublicKeyPresent", Defaults.DEFAULT_PUBLICKEYPRESENT));
        
        this.ssldRepository.save(ssld);
        
        deviceSteps.updateDevice(deviceIdentification, settings);
    }
}
