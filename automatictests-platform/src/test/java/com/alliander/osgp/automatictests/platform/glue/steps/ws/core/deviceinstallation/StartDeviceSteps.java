/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.automatictests.platform.glue.steps.ws.core.deviceinstallation;

import static com.alliander.osgp.automatictests.platform.core.Helpers.getString;
import static com.alliander.osgp.automatictests.platform.core.Helpers.saveCorrelationUidInScenarioContext;

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
import com.alliander.osgp.automatictests.platform.Defaults;
import com.alliander.osgp.automatictests.platform.Keys;
import com.alliander.osgp.automatictests.platform.StepsBase;
import com.alliander.osgp.automatictests.platform.config.CoreDeviceConfiguration;
import com.alliander.osgp.automatictests.platform.core.ScenarioContext;
import com.alliander.osgp.automatictests.platform.glue.steps.ws.GenericResponseSteps;
import com.alliander.osgp.automatictests.platform.support.ws.core.CoreDeviceInstallationClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class StartDeviceSteps extends StepsBase {

    @Autowired
    private CoreDeviceConfiguration configuration;

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
    @When("receiving a start device test request")
    public void receivingAStartDeviceTestRequest(final Map<String, String> requestParameters)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final StartDeviceTestRequest request = new StartDeviceTestRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.DEVICE_IDENTIFICATION, Defaults.DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.startDeviceTest(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    /**
     *
     * @param expectedResponseData
     * @throws Throwable
     */
    @Then("the start device async response contains")
    public void theStartDeviceAsyncResponseContains(final Map<String, String> expectedResponseData) throws Throwable {
        final StartDeviceTestAsyncResponse response = (StartDeviceTestAsyncResponse) ScenarioContext.Current()
                .get(Keys.RESPONSE);

        Assert.assertNotNull(response.getAsyncResponse().getCorrelationUid());
        Assert.assertEquals(getString(expectedResponseData, Keys.DEVICE_IDENTIFICATION),
                response.getAsyncResponse().getDeviceId());

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        saveCorrelationUidInScenarioContext(response.getAsyncResponse().getCorrelationUid(),
                getString(expectedResponseData, Keys.ORGANIZATION_IDENTIFICATION,
                        Defaults.ORGANIZATION_IDENTIFICATION));
    }

    @Then("^the start device response contains soap fault$")
    public void theStartDeviceResponseContainsSoapFault(final Map<String, String> expectedResult) {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }

    /**
     *
     * @param deviceIdentification
     * @throws InterruptedException
     */
    @Then("the platform buffers a start device response message for device \"([^\"]*)\"")
    public void thePlatformBuffersAStartDeviceResponseMessageForDevice(final String deviceIdentification,
            final Map<String, String> expectedResult) throws InterruptedException {
        final StartDeviceTestAsyncRequest request = new StartDeviceTestAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid((String) ScenarioContext.Current().get(Keys.CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        boolean success = false;
        int count = 0;
        while (!success) {
            if (count > this.configuration.getTimeout()) {
                Assert.fail("Timeout");
            }

            count++;
            Thread.sleep(1000);

            try {
                final StartDeviceTestResponse response = this.client.getStartDeviceTestResponse(request);

                Assert.assertEquals(Enum.valueOf(OsgpResultType.class, expectedResult.get(Keys.RESULT)),
                        response.getResult());

                success = true;
            } catch (final Exception ex) {
                // Do nothing
            }
        }
    }
}
