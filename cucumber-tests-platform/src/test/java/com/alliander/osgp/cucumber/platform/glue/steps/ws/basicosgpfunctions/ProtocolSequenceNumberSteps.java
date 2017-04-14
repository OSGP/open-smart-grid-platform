/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws.basicosgpfunctions;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getBoolean;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.core.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestResponse;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.support.ws.core.CoreDeviceInstallationClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the AuthorizeDeviceFunctions steps
 */
public class ProtocolSequenceNumberSteps {

    @Autowired
    private CoreDeviceInstallationClient client;

    @When("^the device adds \"([^\"]*)\" to the sequencenumber in the \"([^\"]*)\" response$")
    public void receivingAConfirmRequest(final Integer number, final String protocol) throws Throwable {
        ScenarioContext.Current().put(Keys.NUMBER_TO_ADD_TO_SEQUENCE_NUMBER, number);
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