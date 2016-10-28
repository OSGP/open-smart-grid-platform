/**
 * Copyright 2016 Smart Society Services B.V.
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.database.device;

import java.util.Map;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alliander.osgp.platform.cucumber.steps.database.DeviceSteps;
import com.alliander.osgp.platform.cucumber.steps.database.RepoHelper;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

/**
 * DLMS device specific steps.
 */
public class DlmsDeviceSteps {

    @Autowired
    private DeviceSteps deviceSteps;

    @Autowired
    private RepoHelper repoHelper;

    @Given("^a device$")
    public void aDevice(final Map<String, String> settings) throws Throwable {
        this.deviceSteps.aDevice(settings);
    }

    @Given("^a dlms device$")
    public void a_dlms_device(final Map<String, String> settings) throws Throwable {
        // First create the device itself
        this.deviceSteps.aSmartMeter(settings);
        this.repoHelper.insertDlmsDevice(settings);
    }

    @Then("^the device with the id \"([^\"]*)\" exists$")
    public void theDeviceWithTheIdExists(final String deviceIdentification) throws Throwable {
        this.deviceSteps.theDeviceWithIdExists(deviceIdentification);
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
