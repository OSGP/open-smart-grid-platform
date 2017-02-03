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

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.core.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestAsyncRequest;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.mocks.oslpdevice.MockOslpServer;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.mocks.OslpDeviceSteps;
import com.alliander.osgp.platform.cucumber.steps.ws.core.deviceinstallation.StartDeviceSteps;
import com.alliander.osgp.platform.cucumber.support.ws.core.CoreDeviceInstallationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the AuthorizeDeviceFunctions steps
 */
public class ProtocolSequenceNumber {

    @Autowired
    private StartDeviceSteps startDeviceTestSteps;

    @Autowired
    private OslpDeviceSteps oslpDeviceSteps;

    @Autowired
    private CoreDeviceInstallationClient client;

    @Autowired
    private MockOslpServer mockOslpServer;

    // When receiving a confirm request
    // | CurrentSequenceNumber | <CurrentSequenceNumber> |
    // | NewSequenceNumber | <NewSequenceNumber> |
    // | SequenceWindow | <SequenceWindow> |
    // Then the confirm response contains
    // | IsUpdated | <IsUpdated> |

    @When("^receiving a confirm request$")
    public void aValidConfirmDeviceRegistrationOslpMessageWithSequenceNumber(
            final Map<String, String> requestParameters) throws Throwable {

        final int currSequenceNumber = getInteger(requestParameters, "CurrentSequenceNumber"),
                newSequenceNumber = getInteger(requestParameters, "NewSequenceNumber"),
                sequenceWindow = getInteger(requestParameters, "SequenceWindow");

        // TODO: Check how to check if the SequenceNumber has a to high value
        ScenarioContext.Current().put("NumberToAddToSequenceNumber", newSequenceNumber - currSequenceNumber);

        this.oslpDeviceSteps.theDeviceReturnsAStartDeviceResponseOverOSLP("OK");
        this.startDeviceTestSteps.receivingAStartDeviceTestRequest(requestParameters);
        this.startDeviceTestSteps.theStartDeviceAsyncResponseContains(requestParameters);
        this.oslpDeviceSteps.aStartDeviceOSLPMessageIsSentToDevice(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        final StartDeviceTestAsyncRequest request = new StartDeviceTestAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        asyncRequest.setCorrelationUid((String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        this.client.getStartDeviceTestResponse(request);

        // this.startDeviceTestSteps.thePlatformBuffersAStartDeviceResponseMessageForDevice(
        // getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION,
        // Defaults.DEFAULT_DEVICE_IDENTIFICATION),
        // requestParameters);
    }

    @Then("^the confirm response contains$")
    public void anExistingOsgpDeviceWithSequenceNumber(final Map<String, String> expectedResponse) {
        final Object response = ScenarioContext.Current().get(Keys.RESPONSE);
        System.out.println(response);

        // Assert.assertEquals(getBoolean(expectedResponse, "IsUpdated"),
        // ScenarioContext.Current().get("IsUpdated"));
    }

    // @Given("an existing device with initial sequence number")
    // public void anExistingDeviceWithInitialSequenceNumber(final Map<String,
    // String> requestParameters) {
    //
    // }

    // @When("receiving a register device request")
    // public void receivingARegisterDeviceRequest(final Map<String, String>
    // requestParameters)
    // throws WebServiceSecurityException, GeneralSecurityException, IOException
    // {
    // final StartDeviceTestRequest request = new StartDeviceTestRequest();
    // request.setDeviceIdentification(
    // getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION,
    // Defaults.DEFAULT_DEVICE_IDENTIFICATION));
    //
    // try {
    // ScenarioContext.Current().put(Keys.RESPONSE,
    // this.client.startDeviceTest(request));
    // } catch (final SoapFaultClientException ex) {
    // ScenarioContext.Current().put(Keys.RESPONSE, ex);
    // }
    // }
    //
    // @Then("the device should contain an expected - equal to init - sequence
    // number")
    // public void theDeviceShouldContainAnExpectedEqualToInitSequenceNumber(
    // final Map<String, String> expectedResponseData) throws Throwable {
    // final StartDeviceTestAsyncResponse response =
    // (StartDeviceTestAsyncResponse) ScenarioContext.Current()
    // .get(Keys.RESPONSE);
    //
    // Assert.assertNotNull(response.getAsyncResponse().getCorrelationUid());
    // Assert.assertEquals(getString(expectedResponseData,
    // Keys.KEY_DEVICE_IDENTIFICATION),
    // response.getAsyncResponse().getDeviceId());
    //
    // // Save the returned CorrelationUid in the Scenario related context for
    // // further use.
    // saveCorrelationUidInScenarioContext(response.getAsyncResponse().getCorrelationUid(),
    // getString(expectedResponseData, Keys.KEY_ORGANIZATION_IDENTIFICATION,
    // Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
    // }
    //
    // @Then("the device should have both random values set")
    // public void theDeviceShouldHaveBothRandomValuesSet(final Map<String,
    // String> requestParameters) {
    // this.oslpDeviceSteps.theDeviceReturnsAStartDeviceResponseOverOSLP("OK");
    // this.oslpDeviceSteps.aStartDeviceOSLPMessageIsSentToDevice(
    // getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION,
    // Defaults.DEFAULT_DEVICE_IDENTIFICATION));
    // }
}