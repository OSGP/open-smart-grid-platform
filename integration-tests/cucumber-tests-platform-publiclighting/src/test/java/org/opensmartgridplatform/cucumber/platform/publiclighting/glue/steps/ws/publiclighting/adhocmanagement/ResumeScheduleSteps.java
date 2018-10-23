/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.ws.publiclighting.adhocmanagement;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.platform.core.CorrelationUidHelper.saveCorrelationUidInScenarioContext;

import java.util.Map;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.ResumeScheduleResponse;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.AsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.common.OsgpResultType;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.glue.steps.ws.GenericResponseSteps;
import org.opensmartgridplatform.cucumber.platform.publiclighting.PlatformPubliclightingDefaults;
import org.opensmartgridplatform.cucumber.platform.publiclighting.PlatformPubliclightingKeys;
import org.opensmartgridplatform.cucumber.platform.publiclighting.support.ws.publiclighting.PublicLightingAdHocManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the set light requests steps
 */
public class ResumeScheduleSteps {
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

        request.setDeviceIdentification(
                getString(requestParameters, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION));
        request.setIndex(getInteger(requestParameters, PlatformPubliclightingKeys.KEY_INDEX,
                PlatformPubliclightingDefaults.DEFAULT_INDEX));
        request.setIsImmediate(getBoolean(requestParameters, PlatformPubliclightingKeys.KEY_ISIMMEDIATE,
                PlatformPubliclightingDefaults.DEFAULT_ISIMMEDIATE));

        try {
            ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE, this.client.resumeSchedule(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE, ex);
        }
    }

    @When("^receiving a set resume schedule by an unknown organization$")
    public void receivingASetResumeScheduleByAnUnknownOrganization(final Map<String, String> requestParameters)
            throws Throwable {
        // Force the request being send to the platform as a given organization.
        ScenarioContext.current().put(PlatformPubliclightingKeys.KEY_ORGANIZATION_IDENTIFICATION,
                "unknown-organization");

        this.receivingAResumeScheduleRequest(requestParameters);
    }

    /**
     * The check for the response from the Platform.
     *
     * @param expectedResponseData
     *            The table with the expected fields in the response.
     * @apiNote The response will contain the correlation uid, so store that in the
     *       current scenario context for later use.
     * @throws Throwable
     */
    @Then("^the resume schedule async response contains$")
    public void theResumeScheduleAsyncResponseContains(final Map<String, String> expectedResponseData)
            throws Throwable {
        final ResumeScheduleAsyncResponse asyncResponse = (ResumeScheduleAsyncResponse) ScenarioContext.current()
                .get(PlatformPubliclightingKeys.RESPONSE);

        Assert.assertNotNull(asyncResponse.getAsyncResponse().getCorrelationUid());
        Assert.assertEquals(getString(expectedResponseData, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION),
                asyncResponse.getAsyncResponse().getDeviceId());

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        saveCorrelationUidInScenarioContext(asyncResponse.getAsyncResponse().getCorrelationUid(),
                getString(expectedResponseData, PlatformPubliclightingKeys.KEY_ORGANIZATION_IDENTIFICATION,
                        PlatformPubliclightingDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION));

        LOGGER.info("Got CorrelationUid: ["
                + ScenarioContext.current().get(PlatformPubliclightingKeys.KEY_CORRELATION_UID) + "]");
    }

    @Then("^the platform buffers a resume schedule response message for device \"([^\"]*)\"$")
    public void thenThePlatformBuffersAResumeScheduleResponseMessage(final String deviceIdentification,
            final Map<String, String> expectedResult) throws Throwable {
        Wait.until(() -> {
            ResumeScheduleResponse response = null;
            try {
                response = this.getResponseWithCorrelationUID(deviceIdentification,
                        (String) ScenarioContext.current().get(PlatformPubliclightingKeys.KEY_CORRELATION_UID));
            } catch (final Exception e) {
                // do nothing
            }
            Assert.assertNotNull(response);
            Assert.assertEquals(
                    Enum.valueOf(OsgpResultType.class, expectedResult.get(PlatformPubliclightingKeys.KEY_RESULT)),
                    response.getResult());
        });

    }

    @Then("^the resume schedule async response contains soap fault$")
    public void theResumeScheduleAsyncResponseContainsSoapFault(final Map<String, String> expectedResult) {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }

    @Then("^the platform buffers a get resume schedule response message for device \"([^\"]*)\"$")
    public void thePlatformBuffersAGetResumeScheduleResponseMessageForDevice(final String deviceIdentification,
            final Map<String, String> expectedResult) throws InterruptedException {
        final ResumeScheduleAsyncResponse asyncResponse = (ResumeScheduleAsyncResponse) ScenarioContext.current()
                .get(PlatformPubliclightingKeys.RESPONSE);

        try {
            this.getResponseWithCorrelationUID(deviceIdentification,
                    asyncResponse.getAsyncResponse().getCorrelationUid());
        } catch (final SoapFaultClientException ex) {
            Assert.assertEquals(getString(expectedResult, PlatformPubliclightingKeys.KEY_FAULTSTRING),
                    ex.getFaultStringOrReason());
        }
    }

    private ResumeScheduleResponse getResponseWithCorrelationUID(final String deviceIdentification,
            final String correlationUID) throws InterruptedException, SoapFaultClientException {
        final ResumeScheduleAsyncRequest request = new ResumeScheduleAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid(correlationUID);
        request.setAsyncRequest(asyncRequest);

        return Wait.untilAndReturn(() -> {
            final ResumeScheduleResponse retval = this.client.getResumeScheduleResponse(request);
            Assert.assertNotNull(retval);
            return retval;
        });
    }
}
