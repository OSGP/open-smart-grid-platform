/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.microgrids.glue.steps.database.core;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.core.BaseDeviceSteps;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;

import cucumber.api.java.en.Given;

/**
 * RTU device specific steps.
 */
public class RtuDeviceSteps extends BaseDeviceSteps {

    @Autowired
    private RtuDeviceRepository rtuDeviceRespository;

    @Autowired
    private DeviceRepository deviceRespository;

    @Given("^an rtu device$")
    @Transactional("txMgrCoreMicrogrids")
    public RtuDevice anRtuDevice(final Map<String, String> settings) throws Throwable {

        final String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION);
        final RtuDevice rtuDevice = new RtuDevice(deviceIdentification);
        return this.rtuDeviceRespository.save(rtuDevice);
    }

    @Transactional("txMgrCore")
    public Device updateRtuDevice(final Map<String, String> settings) throws Throwable {
        return this.updateDevice(this.deviceRespository
                .findByDeviceIdentification(getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION)), settings);
    }
}
