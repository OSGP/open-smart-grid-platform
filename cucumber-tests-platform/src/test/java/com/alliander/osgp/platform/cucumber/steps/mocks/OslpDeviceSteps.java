/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.mocks;

import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.oslp.Oslp.Message;
import com.alliander.osgp.oslp.Oslp.Status;
import com.alliander.osgp.platform.cucumber.mocks.oslpdevice.MockOslpServer;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class which holds all the OSLP device mock steps in order to let the device
 * mock behave correctly for the automatic test.
 */
public class OslpDeviceSteps {

    @Autowired
    private MockOslpServer oslpMockServer;

    /**
     * Setup method to set the firmware which should be returned by the mock.
     * @param firmwareVersion The firmware to respond.
     * @throws Throwable
     */
    @When("^the device returns a set light \"([^\"]*)\" over OSLP$")
    public void the_device_returns_a_set_light_over_OSLP(final String result) throws Throwable {
    	Oslp.Status oslpStatus = Status.OK;
    	
    	switch (result){
    	case "OK":
    		oslpStatus = Status.OK;
    		// TODO: Implement other possible status
    	}
    	
        this.oslpMockServer.mockSetLightResponse(oslpStatus);
    }

    /**
     * Setup method to set the firmware which should be returned by the mock.
     * @param firmwareVersion The firmware to respond.
     * @throws Throwable
     */
    @Given("^the device returns firmware version \"([^\"]*)\" over OSLP$")
    public void the_device_returns_firmware_version_over_OSLP(final String firmwareVersion) throws Throwable {
        this.oslpMockServer.mockFirmwareResponse(firmwareVersion);
    }

    /**
     * Verify that a get firmware version OSLP message is sent to the device.
     *
     * @param deviceIdentification
     *            The device identification expected in the message to the
     *            device.
     * @throws Throwable
     */
    @Then("^a get firmware version OSLP message is sent to device \"([^\"]*)\"$")
    public void a_get_firmware_version_OSLP_message_is_sent_to_device(final String deviceIdentification)
            throws Throwable {
        final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.GET_FIRMWARE_VERSION);
        Assert.notNull(message);
        Assert.isTrue(message.hasGetFirmwareVersionRequest());
        // TODO: Check actual message for the correct firmware(s).
    }

    /**
     * Verify that a set light OSLP message is sent to the device.
     *
     * @param deviceIdentification
     *            The device identification expected in the message to the
     *            device.
     * @throws Throwable
     */
    @Then("^a set light OSLP message is sent to device \"([^\"]*)\"$")
    public void a_get_set_light_OSLP_message_is_sent_to_device(final String deviceIdentification)
            throws Throwable {
        final Message message = this.oslpMockServer.waitForRequest(DeviceRequestMessageType.SET_LIGHT);
        Assert.notNull(message);
        Assert.isTrue(message.hasSetLightRequest());
        // TODO: Check actual message for the correct firmware(s).
    }
}
