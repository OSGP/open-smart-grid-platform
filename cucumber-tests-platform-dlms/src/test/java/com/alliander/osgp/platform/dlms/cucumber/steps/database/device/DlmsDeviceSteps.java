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
import com.alliander.osgp.platform.cucumber.steps.database.DeviceSteps;
import com.alliander.osgp.platform.cucumber.steps.database.RepoHelper;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * DLMS device specific steps.
 */
public class DlmsDeviceSteps {

    private static final String DEFAULT_COMMUNICATION_METHOD = "GPRS";
	private static final Boolean DEFAULT_IP_ADDRESS_IS_STATIC = true;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;
    
    @Autowired
    private DeviceSteps deviceSteps;

    @Autowired
    private RepoHelper repoHelper;

    @Given("^a device$")
    public void aDevice(final Map<String, String> settings) throws Throwable {
        if (this.isSmartMeter(settings)) {
            this.repoHelper.insertSmartMeter(settings);
            this.repoHelper.insertDlmsDevice(settings);
        } else {
            this.repoHelper.insertDevice(settings);
        }
    }
>>>>>>> development

    private boolean isSmartMeter(final Map<String, String> settings) {
        final String deviceType = settings.get(Keys.KEY_DEVICE_TYPE);
        return SMART_METER_E.equals(deviceType) || SMART_METER_G.equals(deviceType);
    }

<<<<<<< HEAD
        // First create the device itself
        this.deviceSteps.aSmartMeter(settings);
        
        // Now create the DLMS device in the DLMS database
        final String deviceIdentification = getString(settings, "DeviceIdentification",
                DeviceSteps.DEFAULT_DEVICE_IDENTIFICATION);
        final DlmsDevice dlmsDevice = new DlmsDevice(deviceIdentification);
        dlmsDevice.setCommunicationMethod(
        		getString(settings, "CommunicationMethod", DEFAULT_COMMUNICATION_METHOD));
        dlmsDevice.setIpAddressIsStatic(
        		getBoolean(settings, "IpAddressIsStatic", DEFAULT_IP_ADDRESS_IS_STATIC));

        // TODO: Set dlms specific device settings
        this.dlmsDeviceRepository.save(dlmsDevice);
        
        // TODO: Set the devicegateway
=======
    @Then("^the device with the id \"([^\"]*)\" exists$")
    public void theDeviceWithTheIdExists(final String deviceIdentification) throws Throwable {
        this.deviceSteps.theDeviceWithIdExists(deviceIdentification);
>>>>>>> development
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
