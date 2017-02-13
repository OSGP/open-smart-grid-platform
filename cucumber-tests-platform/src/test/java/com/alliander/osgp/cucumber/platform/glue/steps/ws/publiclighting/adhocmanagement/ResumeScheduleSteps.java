/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws.publiclighting.adhocmanagement;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getBoolean;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getInteger;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;
import static com.alliander.osgp.cucumber.platform.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.OsgpResultType;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.config.CoreDeviceConfiguration;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import com.alliander.osgp.cucumber.platform.support.ws.publiclighting.PublicLightingAdHocManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the set light requests steps
 */
public class ResumeScheduleSteps {
    @Autowired
    private CoreDeviceConfiguration configuration;

    @Autowired
    private PublicLightingAdHocManagementClient client;

    private static final Logger LOGGER = LoggerFactory.getLogger(ResumeScheduleSteps.class);

    /**
     * Sends a Resume Schedule request to the platform for a given device
     * identification.
     *
     * @param requestParameters
     *            The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a resume schedule request$")
    public void receivingAResumeScheduleRequest(final Map<String, String> requestParameters) throws Throwable {

        final ResumeScheduleRequest request = new ResumeScheduleRequest();

        request.setDeviceIdentification(getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION));
        request.setIndex(getInteger(requestParameters, Keys.KEY_INDEX, Defaults.DEFAULT_INDEX));
        request.setIsImmediate(getBoolean(requestParameters, Keys.KEY_ISIMMEDIATE, Defaults.DEFAULT_ISIMMEDIATE));

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.resumeSchedule(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    @When("^receiving a set resume schedule by an unknown organization$")
    public void receivingASetResumeScheduleByAnUnknownOrganization(final Map<String, String> requestParameters)
            throws Throwable {
        // Force the request being send to the platform as a given organization.
        ScenarioContext.Current().put(Keys.KEY_ORGANIZATION_IDENTIFICATION, "unknown-organization");

        this.receivingAResumeScheduleRequest(requestParameters);
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
    @Then("^the resume schedule async response contains$")
    public void theResumeScheduleAsyncResponseContains(final Map<String, String> expectedResponseData)
            throws Throwable {
        final ResumeScheduleAsyncResponse response = (ResumeScheduleAsyncResponse) ScenarioContext.Current()
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

    @Then("^the platform buffers a resume schedule response message for device \"([^\"]*)\"$")
    public void thenThePlatformBuffersAResumeScheduleResponseMessage(final String deviceIdentification,
            final Map<String, String> expectedResult) throws Throwable {
        final ResumeScheduleResponse response = this.getResponseWithCorrelationUID(deviceIdentification,
                (String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
        Assert.assertEquals(Enum.valueOf(OsgpResultType.class, expectedResult.get(Keys.KEY_RESULT)),
                response.getResult());
    }

    @Then("^the resume schedule async response contains soap fault$")
    public void theResumeScheduleAsyncResponseContainsSoapFault(final Map<String, String> expectedResult) {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }

    @Then("^the platform buffers a get resume schedule response message for device \"([^\"]*)\"$")
    public void thePlatformBuffersAGetResumeScheduleResponseMessageForDevice(final String deviceIdentification,
            final Map<String, String> expectedResult) throws InterruptedException {
        final ResumeScheduleAsyncResponse asyncResponse = (ResumeScheduleAsyncResponse) ScenarioContext.Current()
                .get(Keys.RESPONSE);

        try {
            this.getResponseWithCorrelationUID(deviceIdentification,
                    asyncResponse.getAsyncResponse().getCorrelationUid());
        } catch (final SoapFaultClientException ex) {
            Assert.assertEquals(getString(expectedResult, Keys.KEY_FAULTSTRING), ex.getFaultStringOrReason());
        }
    }

    private ResumeScheduleResponse getResponseWithCorrelationUID(final String deviceIdentification,
            final String correlationUID) throws InterruptedException, SoapFaultClientException {
        final ResumeScheduleAsyncRequest request = new ResumeScheduleAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid(correlationUID);
        request.setAsyncRequest(asyncRequest);

        int count = 0;
        while (true) {
            if (count > this.configuration.getTimeout()) {
                Assert.fail("Timeout");
            }

            count++;
            Thread.sleep(1000);

            try {
                return this.client.getResumeScheduleResponse(request);
            } catch (final SoapFaultClientException ex) {
                throw ex;
            } catch (final Exception ex) {
                // Do nothing
            }
        }
    }
}
