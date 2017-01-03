/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.core.deviceinstallation;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.FindRecentDevicesResponse;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.support.ws.core.CoreDeviceInstallationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class FindRecentDeviceSteps {

    @Autowired
    private CoreDeviceInstallationClient client;

    @When("receiving a find recent devices request")
    public void receiving_a_find_recent_devices_request() throws Throwable {
        final FindRecentDevicesRequest request = new FindRecentDevicesRequest();

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.findRecentDevices(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    @Then("the find recent devices response contains \"([^\"]*)\" devices?")
    public void the_find_recent_devices_response_contains(final Integer numberOfDevices) {
        final FindRecentDevicesResponse response = (FindRecentDevicesResponse) ScenarioContext.Current()
                .get(Keys.RESPONSE);

        // TODO: The code: "response.getDevices()" can return null
        Assert.assertEquals((int) numberOfDevices, response.getDevices().size());
    }

    @Then("the find recent devices response contains at index \"([^\"]*)\"")
    public void the_find_recent_devices_response_contains_at_index(final Integer index,
            final Map<String, String> expectedDevice) throws Throwable {
        final FindRecentDevicesResponse response = (FindRecentDevicesResponse) ScenarioContext.Current()
                .get(Keys.RESPONSE);

        DeviceSteps.checkDevice(expectedDevice, response.getDevices().get(index - 1));
    }
}
