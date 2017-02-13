/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws.core.adhocmanagement;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;
import static com.alliander.osgp.cucumber.platform.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.core.adhocmanagement.SetRebootAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.adhocmanagement.SetRebootAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.adhocmanagement.SetRebootRequest;
import com.alliander.osgp.adapter.ws.schema.core.adhocmanagement.SetRebootResponse;
import com.alliander.osgp.adapter.ws.schema.core.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.common.OsgpResultType;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.GlueBase;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.config.CoreDeviceConfiguration;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.support.ws.core.CoreAdHocManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the set light requests steps
 */
public class SetRebootSteps extends GlueBase {

    @Autowired
    private CoreDeviceConfiguration configuration;

    @Autowired
    private CoreAdHocManagementClient client;

    private static final Logger LOGGER = LoggerFactory.getLogger(SetRebootSteps.class);

    /**
     * Sends a Get Status request to the platform for a given device
     * identification.
     * 
     * @param requestParameters
     *            The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a set reboot request$")
    public void receivingASetRebootRequest(final Map<String, String> requestParameters) throws Throwable {
        final SetRebootRequest request = new SetRebootRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.setReboot(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    @When("^receiving a set reboot request by an unknown organization$")
    public void receivingASetRebootRequestByAnUnknownOrganization(final Map<String, String> requestParameters)
            throws Throwable {
        // Force the request being send to the platform as a given organization.
        ScenarioContext.Current().put(Keys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");

        this.receivingASetRebootRequest(requestParameters);
    }

    /**
     * The check for the response from the Platform.
     * 
     * @param expectedResponseData
     *            The table with the expected fields in the response.
     * @note The response will contain the correlation uid, so store that in the
     *       current scenario context for later use.
     * @throws Throwable
     */
    @Then("^the set reboot async response contains$")
    public void theSetRebootAsyncResponseContains(final Map<String, String> expectedResponseData) throws Throwable {
        final SetRebootAsyncResponse response = (SetRebootAsyncResponse) ScenarioContext.Current().get(Keys.RESPONSE);

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

    @Then("^the platform buffers a set reboot response message for device \"([^\"]*)\"$")
    public void thenThePlatformBuffersASetRebootResponseMessage(final String deviceIdentification,
            final Map<String, String> expectedResult) throws Throwable {
        final SetRebootAsyncRequest request = new SetRebootAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid((String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
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
                final SetRebootResponse response = this.client.getSetRebootResponse(request);

                Assert.assertEquals(Enum.valueOf(OsgpResultType.class, expectedResult.get(Keys.KEY_RESULT)),
                        response.getResult());

                success = true;
            } catch (final Exception ex) {
                // Do nothing
            }
        }
    }

    @Then("^the set reboot async response contains a soap fault$")
    public void theSetRebootAsyncResponseContainsASoapFault(final Map<String, String> expectedResult) {
        final SoapFaultClientException response = (SoapFaultClientException) ScenarioContext.Current()
                .get(Keys.RESPONSE);

        Assert.assertEquals(expectedResult.get(Keys.KEY_MESSAGE), response.getMessage());
    }
}