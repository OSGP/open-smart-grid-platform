/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.basicosgpfunctions;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getInteger;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.core.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestResponse;
import com.alliander.osgp.platform.cucumber.config.CoreDeviceConfiguration;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.mocks.OslpDeviceSteps;
import com.alliander.osgp.platform.cucumber.steps.ws.GenericResponseSteps;
import com.alliander.osgp.platform.cucumber.steps.ws.core.deviceinstallation.StartDeviceSteps;
import com.alliander.osgp.platform.cucumber.support.ws.core.CoreDeviceInstallationClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;

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

    @Autowired
    private CoreDeviceInstallationClient client;

    @Autowired
    private CoreDeviceConfiguration configuration;

    @When("^receiving a confirm request$")
    public void receivingAConfirmRequest(final Map<String, String> requestParameters) throws Throwable {

        ScenarioContext.Current().put("NumberToAddAsNextSequenceNumber",
                getInteger(requestParameters, "AddNumberToSequenceNumber"));
    }

    @When("^receiving a confirm request for unknown device$")
    public void receivingAConfirmRequestForUnknownDevice(final Map<String, String> requestParameters) throws Throwable {
        this.receivingAConfirmRequestWithEmptyDeviceIdentification(requestParameters);
    }

    @When("^receiving a confirm request with empty device identification$")
    public void receivingAConfirmRequestWithEmptyDeviceIdentification(final Map<String, String> requestParameters)
            throws Throwable {
        final StartDeviceTestRequest request = new StartDeviceTestRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.startDeviceTest(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    @Then("^the confirm response contains soap fault$")
    public void theConfirmResponseContainsSoapFault(final Map<String, String> expectedResponse) {
        GenericResponseSteps.verifySoapFault(expectedResponse);
    }

    /**
     *
     * @param deviceIdentification
     * @param expectedResult
     * @throws InterruptedException
     * @throws WebServiceSecurityException
     * @throws GeneralSecurityException
     * @throws IOException
     */
    @Then("^the platform buffers a protocol sequence number response message for device \"([^\"]*)\"$")
    public void thePlatformBuffersAStartDeviceResponseMessageForDevice(final String deviceIdentification,
            final Map<String, String> expectedResult)
            throws InterruptedException, WebServiceSecurityException, GeneralSecurityException, IOException {

        final StartDeviceTestAsyncRequest request = new StartDeviceTestAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid((String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        Object response = null;
        try {
            response = this.client.getStartDeviceTestResponse(request);
        } catch (final SoapFaultClientException ex) {
            response = ex;
        }

        Assert.assertEquals(getBoolean(expectedResult, "IsUpdated"), response instanceof StartDeviceTestResponse);
    }
}