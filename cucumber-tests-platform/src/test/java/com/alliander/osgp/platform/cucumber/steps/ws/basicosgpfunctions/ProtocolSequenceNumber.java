/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.basicosgpfunctions;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesResponse;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.support.ws.WebServiceSecurityException;
import com.alliander.osgp.platform.cucumber.support.ws.core.CoreDeviceInstallationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the AuthorizeDeviceFunctions steps
 */
public class ProtocolSequenceNumber {

    @Autowired
    private CoreDeviceInstallationClient coreDeviceInstallationClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolSequenceNumber.class);

    // @Given("an existing device with initial sequence number")
    // public void anExistingDeviceWithInitialSequenceNumber(final Map<String,
    // String> requestParameters) {
    //
    // }

    @When("receiving a register device request")
    public void receivingARegisterDeviceRequest(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        ScenarioContext.Current().put(Keys.RESPONSE,
                this.coreDeviceInstallationClient.findRecentDevices(new FindRecentDevicesRequest()));

        final FindRecentDevicesResponse response = (FindRecentDevicesResponse) ScenarioContext.Current()
                .get(Keys.RESPONSE);
        System.out.println(response);
    }

    @Then("the device should contain an expected - equal to init - sequence number")
    public void theDeviceShouldContainAnExpectedEqualToInitSequenceNumber(final Map<String, String> requestParameters) {

    }

    @Then("the device should have both random values set")
    public void theDeviceShouldHaveBothRandomValuesSet(final Map<String, String> requestParameters) {

    }
}