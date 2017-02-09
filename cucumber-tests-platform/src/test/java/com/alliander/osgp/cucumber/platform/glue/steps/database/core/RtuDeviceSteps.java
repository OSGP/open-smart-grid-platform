/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.database.core;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.microgrids.entities.RtuDevice;
import com.alliander.osgp.domain.microgrids.repositories.RtuDeviceRepository;

import cucumber.api.java.en.Given;

/**
 * RTU device specific steps.
 */
public class RtuDeviceSteps extends BaseDeviceSteps {

    @Autowired
    private RtuDeviceRepository rtuDeviceRespository;

    @Given("^an rtu device$")
    @Transactional("txMgrCore")
    public RtuDevice anRtuDevice(final Map<String, String> settings) throws Throwable {

        final String deviceIdentification = getString(settings, Keys.KEY_DEVICE_IDENTIFICATION);
        final RtuDevice rtuDevice = new RtuDevice(deviceIdentification);
        this.rtuDeviceRespository.save(rtuDevice);

        Device device = this.rtuDeviceRespository.findByDeviceIdentification(deviceIdentification);
        device = this.updateDevice(device, settings);

        return this.rtuDeviceRespository.findByDeviceIdentification(deviceIdentification);
    }
}
