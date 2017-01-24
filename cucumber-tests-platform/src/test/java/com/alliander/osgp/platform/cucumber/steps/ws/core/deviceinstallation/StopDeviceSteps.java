/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.core.deviceinstallation;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.core.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StopDeviceTestAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StopDeviceTestAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StopDeviceTestRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.StopDeviceTestResponse;
import com.alliander.osgp.platform.cucumber.config.CoreDeviceConfiguration;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.ws.GenericResponseSteps;
import com.alliander.osgp.platform.cucumber.support.ws.core.CoreDeviceInstallationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class StopDeviceSteps {

    @Autowired
    private CoreDeviceConfiguration configuration;

    @Autowired
    private CoreDeviceInstallationClient client;

    private static final Logger LOGGER = LoggerFactory.getLogger(StopDeviceSteps.class);

    /**
     *
     * @param requestParameters
     * @throws Throwable
     */
    @When("receiving a stop device test request")
    public void receivingAStopDeviceRequest(final Map<String, String> requestParameters) throws Throwable {
        final StopDeviceTestRequest request = new StopDeviceTestRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.stopDeviceTest(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    /**
     *
     * @param expectedResponseData
     * @throws Throwable
     */
    @Then("the stop device async response contains")
    public void theStopDeviceAsyncResponseContains(final Map<String, String> expectedResponseData) throws Throwable {
        final StopDeviceTestAsyncResponse response = (StopDeviceTestAsyncResponse) ScenarioContext.Current()
                .get(Keys.RESPONSE);

        Assert.assertNotNull(response.getAsyncResponse().getCorrelationUid());
        Assert.assertEquals(getString(expectedResponseData, Keys.KEY_DEVICE_IDENTIFICATION),
                response.getAsyncResponse().getDeviceId());

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        saveCorrelationUidInScenarioContext(response.getAsyncResponse().getCorrelationUid(),
                getString(expectedResponseData, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                        Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        LOGGER.info("Got CorrelationUid: [" + ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID) + "]");
    }

    @Then("^the stop device response contains soap fault$")
    public void theStopDeviceResponseContainsSoapFault(final Map<String, String> expectedResult) throws Throwable {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }

    /**
     *
     * @param deviceIdentification
     * @throws Throwable
     */
    @Then("the platform buffers a stop device response message for device \"([^\"]*)\"")
    public void thePlatformBuffersAStopDeviceResponseMessageForDevice(final String deviceIdentification,
            final Map<String, String> expectedResult) throws Throwable {
        StopDeviceTestAsyncRequest request = new StopDeviceTestAsyncRequest();
        AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid((String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        boolean success = false;
        int count = 0;
        while (!success) {
            if (count > configuration.getTimeout()) {
                Assert.fail("Timeout");
            }

            count++;
            Thread.sleep(1000);

            try {
                StopDeviceTestResponse response = client.getStopDeviceTestResponse(request);

                Assert.assertEquals(Enum.valueOf(OsgpResultType.class, expectedResult.get(Keys.KEY_RESULT)),
                        response.getResult());

                success = true;
            } catch (Exception ex) {
                // Do nothing
            }
        }
    }
}
