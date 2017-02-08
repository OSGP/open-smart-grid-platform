/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.basicosgpfunctions;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getInteger;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.mocks.OslpDeviceSteps;
import com.alliander.osgp.platform.cucumber.steps.ws.core.deviceinstallation.StartDeviceSteps;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the AuthorizeDeviceFunctions steps
 */
public class ProtocolSequenceNumberSteps {

    @Autowired
    private StartDeviceSteps startDeviceTestSteps;

    @Autowired
    private OslpDeviceSteps oslpDeviceSteps;

    @When("^receiving a confirm request$")
    public void aValidConfirmDeviceRegistrationOslpMessageWithSequenceNumber(
            final Map<String, String> requestParameters) throws Throwable {

        ScenarioContext.Current().put("NumberToAddAsNextSequenceNumber",
                getInteger(requestParameters, "NextSequenceNumber"));

        this.oslpDeviceSteps.theDeviceReturnsAStartDeviceResponseOverOSLP("OK");
        this.startDeviceTestSteps.receivingAStartDeviceTestRequest(requestParameters);
        this.oslpDeviceSteps.aStartDeviceOSLPMessageIsSentToDevice(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        // final StartDeviceTestAsyncRequest request = new
        // StartDeviceTestAsyncRequest();
        // final AsyncRequest asyncRequest = new AsyncRequest();
        // asyncRequest.setDeviceId(
        // getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION,
        // Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        // asyncRequest.setCorrelationUid((String)
        // ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
        // request.setAsyncRequest(asyncRequest);
        //
        // this.client.getStartDeviceTestResponse(request);

        // this.startDeviceTestSteps.thePlatformBuffersAStartDeviceResponseMessageForDevice(
        // getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION,
        // Defaults.DEFAULT_DEVICE_IDENTIFICATION),
        // requestParameters);
    }

    @Then("^the confirm response contains$")
    public void anExistingOsgpDeviceWithSequenceNumber(final Map<String, String> expectedResponse) {
        final Object response = ScenarioContext.Current().get(Keys.RESPONSE);

        Assert.assertTrue(!(response instanceof SoapFaultClientException));

        // Assert.assertEquals(getBoolean(expectedResponse, "IsUpdated"),
        // ScenarioContext.Current().get("IsUpdated"));
    }
}