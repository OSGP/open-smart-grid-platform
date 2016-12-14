/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.microgrids.entities.RtuDevice;
import com.alliander.osgp.domain.microgrids.repositories.RtuDeviceRepository;
import com.alliander.osgp.platform.cucumber.helpers.SettingsHelper;
import com.alliander.osgp.platform.cucumber.steps.Keys;

import cucumber.api.java.en.Given;

/**
 * RTU device specific steps.
 */
@Transactional("txMgrCore")
public class RtuDeviceSteps {

    private static final String DEFAULT_DEVICE_IDENTIFICATION = "RTU00001";
    private static final String DEFAULT_DEVICE_TYPE = "RTU";
    private static final String DEFAULT_PROTOCOL = "IEC61850";
    private static final String DEFAULT_PROTOCOL_VERSION = "1.0";

    private static final Map<String, String> RTU_DEFAULT_SETTINGS = Collections
            .unmodifiableMap(new HashMap<String, String>() {
                private static final long serialVersionUID = 1L;
                {
                    this.put(Keys.KEY_DEVICE_IDENTIFICATION, DEFAULT_DEVICE_IDENTIFICATION);
                    this.put(Keys.KEY_DEVICE_TYPE, DEFAULT_DEVICE_TYPE);
                    this.put(Keys.KEY_PROTOCOL, DEFAULT_PROTOCOL);
                    this.put(Keys.KEY_PROTOCOL_VERSION, DEFAULT_PROTOCOL_VERSION);
                }
            });

    @Autowired
    private RtuDeviceRepository rtuDeviceRespository;

    @Autowired
    private DeviceSteps deviceSteps;

    @Given("^an rtu device$")
    public void anRtuDevice(final Map<String, String> settings) throws Throwable {

        final Map<String, String> rtuSettings = SettingsHelper.addAsDefaults(settings, RTU_DEFAULT_SETTINGS);
        final String deviceIdentification = rtuSettings.get(Keys.KEY_DEVICE_IDENTIFICATION);

        final RtuDevice rtuDevice = new RtuDevice(deviceIdentification);
        this.rtuDeviceRespository.save(rtuDevice);

        this.deviceSteps.updateDevice(deviceIdentification, rtuSettings);
    }
}
