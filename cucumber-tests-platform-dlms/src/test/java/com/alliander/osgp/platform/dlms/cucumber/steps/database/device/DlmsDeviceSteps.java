/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.database.device;

import static com.alliander.osgp.platform.cucumber.steps.Defaults.SMART_METER_E;
import static com.alliander.osgp.platform.cucumber.steps.Defaults.SMART_METER_G;

import java.util.Map;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.database.core.DeviceSteps;
import com.alliander.osgp.platform.cucumber.steps.database.core.SmartMeterSteps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * DLMS device specific steps.
 */
public class DlmsDeviceSteps {

    @Autowired
    private DeviceSteps deviceSteps;
    
    @Autowired
    private SmartMeterSteps smartMeterSteps;

    @Autowired
    private com.alliander.osgp.platform.cucumber.steps.database.adapterprotocoldlms.DlmsDeviceSteps repoHelper;

    @Given("^a dlms device$")
    public void aDlmsDevice(final Map<String, String> settings) throws Throwable {
        if (this.isSmartMeter(settings)) {
            smartMeterSteps.aSmartMeter(settings);
            this.repoHelper.insertDlmsDevice(settings);
        } else {
            deviceSteps.aDevice(settings);
        }
    }

    private boolean isSmartMeter(final Map<String, String> settings) {
        final String deviceType = settings.get(Keys.KEY_DEVICE_TYPE);
        return SMART_METER_E.equals(deviceType) || SMART_METER_G.equals(deviceType);
    }

    /**
     * check that the given dlms device is inserted
     *
     * @param deviceId
     * @return
     */
    @Then("^the dlms device with id \"([^\"]*)\" exists$")
    public void theDlmsDeviceShouldExist(final String deviceIdentification) throws Throwable {
        final DlmsDevice dlmsDevice = this.repoHelper.findDlmsDevice(deviceIdentification);
        Assert.notNull(dlmsDevice);
        Assert.isTrue(dlmsDevice.getSecurityKeys().size() > 0);
    }
}
