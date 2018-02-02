/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.common.glue.steps.ws.core.deviceinstallation;

import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getEnum;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getString;
import static com.alliander.osgp.cucumber.platform.core.CorrelationUidHelper.saveCorrelationUidInScenarioContext;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.core.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StartDeviceTestResponse;
import com.alliander.osgp.cucumber.core.GlueBase;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.core.Wait;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.common.support.ws.core.CoreDeviceInstallationClient;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class StartDeviceSteps extends GlueBase {

    @Autowired
    private CoreDeviceInstallationClient client;

    /**
     *
     * @param requestParameters
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws WebServiceSecurityException
     * @throws Throwable
     */
    @When("receiving a start device request")
    public void receivingAStartDeviceRequest(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final StartDeviceTestRequest request = new StartDeviceTestRequest();
        request.setDeviceIdentification(getString(requestParameters, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, this.client.startDeviceTest(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, ex);
        }
    }

    /**
     *
     * @param deviceIdentification
     * @throws InterruptedException
     */
    @Then("^the platform buffers a start device response message for device \"([^\"]*)\"$")
    public void thePlatformBuffersAStartDeviceResponseMessageForDevice(final String deviceIdentification,
            final Map<String, String> expectedResult) throws InterruptedException {
        final StartDeviceTestAsyncRequest request = new StartDeviceTestAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid((String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        Wait.until(() -> {
            StartDeviceTestResponse response = null;
            try {
                response = this.client.getStartDeviceTestResponse(request);
            } catch (final Exception e) {
                // do nothing
            }
            Assert.assertNotNull(response);
            Assert.assertEquals(getEnum(expectedResult, PlatformKeys.KEY_RESULT, OsgpResultType.class),
                    response.getResult());
        });

    }

    @Then("^the platform buffers no start device test response message for device \"([^\"]*)\"$")
    public void thePlatformBuffersNoStartDeviceTestResponseMessageForDevice(final String deviceIdentification)
            throws InterruptedException {
        final StartDeviceTestAsyncRequest request = new StartDeviceTestAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid((String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        Wait.until(() -> {
            StartDeviceTestResponse response = null;
            try {
                response = this.client.getStartDeviceTestResponse(request);
            } catch (final Exception e) {
                // do nothing
            }
            Assert.assertNotNull(response);
            Assert.assertNotEquals(OsgpResultType.NOT_FOUND, response.getResult());
        });
    }

    /**
     *
     * @param expectedResponseData
     * @throws Throwable
     */
    @Then("the start device async response contains")
    public void theStartDeviceAsyncResponseContains(final Map<String, String> expectedResponseData) throws Throwable {
        final StartDeviceTestAsyncResponse asyncResponse = (StartDeviceTestAsyncResponse) ScenarioContext.current()
                .get(PlatformKeys.RESPONSE);

        Assert.assertNotNull(asyncResponse.getAsyncResponse().getCorrelationUid());
        Assert.assertEquals(getString(expectedResponseData, PlatformKeys.KEY_DEVICE_IDENTIFICATION),
                asyncResponse.getAsyncResponse().getDeviceId());

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        saveCorrelationUidInScenarioContext(asyncResponse.getAsyncResponse().getCorrelationUid(),
                getString(expectedResponseData, PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION,
                        PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
    }

    @Then("^the start device response contains soap fault$")
    public void theStartDeviceResponseContainsSoapFault(final Map<String, String> expectedResult) {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }
}
