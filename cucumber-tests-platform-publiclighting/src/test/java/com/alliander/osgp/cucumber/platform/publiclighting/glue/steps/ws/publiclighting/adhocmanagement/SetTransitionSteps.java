/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.publiclighting.glue.steps.ws.publiclighting.adhocmanagement;

import static com.alliander.osgp.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.core.Helpers.getString;
import static com.alliander.osgp.cucumber.platform.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetTransitionResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.TransitionType;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.common.OsgpResultType;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.core.Wait;
import com.alliander.osgp.cucumber.platform.publiclighting.PlatformPubliclightingDefaults;
import com.alliander.osgp.cucumber.platform.publiclighting.PlatformPubliclightingKeys;
import com.alliander.osgp.cucumber.platform.publiclighting.support.ws.publiclighting.PublicLightingAdHocManagementClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the set light requests steps
 */
public class SetTransitionSteps {
    @Autowired
    private PublicLightingAdHocManagementClient client;

    private static final Logger LOGGER = LoggerFactory.getLogger(SetTransitionSteps.class);

    /**
     * Sends a Get Status request to the platform for a given device
     * identification.
     *
     * @param requestParameters
     *            The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a set transition request$")
    public void receivingASetTransitionRequest(final Map<String, String> requestParameters) throws Throwable {

        final SetTransitionRequest request = new SetTransitionRequest();
        request.setDeviceIdentification(
                getString(requestParameters, PlatformPubliclightingKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformPubliclightingDefaults.DEFAULT_DEVICE_IDENTIFICATION));

        if (requestParameters.containsKey(PlatformPubliclightingKeys.KEY_TRANSITION_TYPE)
                && !requestParameters.get(PlatformPubliclightingKeys.KEY_TRANSITION_TYPE).isEmpty()) {
            request.setTransitionType(getEnum(requestParameters, PlatformPubliclightingKeys.KEY_TRANSITION_TYPE,
                    TransitionType.class, PlatformPubliclightingDefaults.DEFAULT_TRANSITION_TYPE));
        }

        if (requestParameters.containsKey(PlatformPubliclightingKeys.KEY_TIME)
                && !requestParameters.get(PlatformPubliclightingKeys.KEY_TIME).isEmpty()) {
            final GregorianCalendar gcal = new GregorianCalendar();
            gcal.add(Calendar.HOUR,
                    Integer.parseInt(requestParameters.get(PlatformPubliclightingKeys.KEY_TIME).substring(0, 2)));
            gcal.add(Calendar.MINUTE,
                    Integer.parseInt(requestParameters.get(PlatformPubliclightingKeys.KEY_TIME).substring(2, 4)));
            gcal.add(Calendar.SECOND,
                    Integer.parseInt(requestParameters.get(PlatformPubliclightingKeys.KEY_TIME).substring(4, 6)));
            final XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
            request.setTime(xgcal);
        }

        try {
            ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE, this.client.setTransition(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.current().put(PlatformPubliclightingKeys.RESPONSE, ex);
        }
    }

    @When("^receiving a set transition request by an unknown organization$")
    public void receivingASetTransitionRequestByAnUnknownOrganization(final Map<String, String> requestParameters)
            throws Throwable {
        // Force the request being send to the platform as a given organization.
        ScenarioContext.current().put(PlatformPubliclightingKeys.KEY_ORGANIZATION_IDENTIFICATION,
                "unknown-organization");

        this.receivingASetTransitionRequest(requestParameters);
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
    @Then("^the set transition async response contains$")
    public void theSetTransitionAsyncResponseContains(final Map<String, String> expectedResponseData) throws Throwable {
        final SetTransitionAsyncResponse asyncResponse = (SetTransitionAsyncResponse) ScenarioContext.current()
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

    @Then("^the platform buffers a set transition response message for device \"([^\"]*)\"$")
    public void thePlatformBuffersASetTransitionResponseMessage(final String deviceIdentification,
            final Map<String, String> expectedResult) throws Throwable {
        final SetTransitionAsyncRequest request = new SetTransitionAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid(
                (String) ScenarioContext.current().get(PlatformPubliclightingKeys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        Wait.until(() -> {
            SetTransitionResponse response = null;
            try {
                response = this.client.getSetTransitionResponse(request);
            } catch (final Exception e) {
                // do nothing
            }
            Assert.assertNotNull(response);
            Assert.assertEquals(
                    Enum.valueOf(OsgpResultType.class, expectedResult.get(PlatformPubliclightingKeys.KEY_RESULT)),
                    response.getResult());
        });
    }

    @Then("^the platform does not buffer a set transition response message for device \"([^\"]*)\"$")
    public void thePlatformDoesNotBufferASetTransitionResponseMessage(final String deviceIdentification,
            final Map<String, String> expectedResult) throws Throwable {
        final SetTransitionAsyncRequest request = new SetTransitionAsyncRequest();
        final AsyncRequest asyncRequest = new AsyncRequest();
        asyncRequest.setDeviceId(deviceIdentification);
        asyncRequest.setCorrelationUid(
                (String) ScenarioContext.current().get(PlatformPubliclightingKeys.KEY_CORRELATION_UID));
        request.setAsyncRequest(asyncRequest);

        Wait.until(() -> {
            SetTransitionResponse response = null;
            try {
                response = this.client.getSetTransitionResponse(request);
            } catch (final Exception e) {
                // do nothing
            }
            Assert.assertNotNull(response);
            Assert.assertEquals(
                    Enum.valueOf(OsgpResultType.class, expectedResult.get(PlatformPubliclightingKeys.KEY_RESULT)),
                    response.getResult());
        });
    }

    @Then("^the set transition async response contains a soap fault$")
    public void theSetTransitionAsyncResponseContainsASoapFault(final Map<String, String> expectedResult) {
        final SoapFaultClientException response = (SoapFaultClientException) ScenarioContext.current()
                .get(PlatformPubliclightingKeys.RESPONSE);

        Assert.assertEquals(expectedResult.get(PlatformPubliclightingKeys.KEY_MESSAGE), response.getMessage());
    }
}