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

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.LightValue;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetLightAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetLightAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetLightRequest;
import com.alliander.osgp.adapter.ws.schema.publiclighting.adhocmanagement.SetLightResponse;
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
public class SetLightSteps {

    @Autowired
    private CoreDeviceConfiguration configuration;

    @Autowired
    private PublicLightingAdHocManagementClient client;

    private static final Logger LOGGER = LoggerFactory.getLogger(SetLightSteps.class);

    /**
     * Sends a Set Light request to the platform for a given device
     * identification.
     *
     * @param requestParameters
     *            The table with the request parameters.
     * @throws Throwable
     */
    @When("^receiving a set light request$")
    public void receivingASetLightRequest(final Map<String, String> requestParameters) throws Throwable {

        final SetLightRequest request = new SetLightRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        final LightValue lightValue = new LightValue();
        lightValue.setIndex(getInteger(requestParameters, Keys.KEY_INDEX, Defaults.DEFAULT_INDEX));
        if (requestParameters.containsKey(Keys.KEY_DIMVALUE)
                && !StringUtils.isEmpty(requestParameters.get(Keys.KEY_DIMVALUE))) {
            lightValue.setDimValue(getInteger(requestParameters, Keys.KEY_DIMVALUE, Defaults.DEFAULT_DIMVALUE));
        }
        lightValue.setOn(getBoolean(requestParameters, Keys.KEY_ON, Defaults.DEFAULT_ON));
        request.getLightValue().add(lightValue);

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.setLight(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    @When("^receiving a set light request with \"([^\"]*)\" valid lightvalues and \"([^\"]*)\" invalid lightvalues$")
    public void receivingAsetLightRequestWithValidLightValuesAndInvalidLightValues(final Integer nofValidLightValues,
            final Integer nofInvalidLightValues, final Map<String, String> requestParameters) throws Throwable {
        final SetLightRequest request = new SetLightRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        for (int i = 0; i < nofValidLightValues; i++) {
            final LightValue lightValue = new LightValue();
            lightValue.setIndex(getInteger(requestParameters, Keys.KEY_INDEX, Defaults.DEFAULT_INDEX));
            lightValue.setDimValue(getInteger(requestParameters, Keys.KEY_DIMVALUE, Defaults.DEFAULT_DIMVALUE));
            lightValue.setOn(getBoolean(requestParameters, Keys.KEY_ON, Defaults.DEFAULT_ON));
            request.getLightValue().add(lightValue);
        }

        for (int i = 0; i < nofInvalidLightValues; i++) {
            final LightValue lightValue = new LightValue();
            lightValue.setIndex(getInteger(requestParameters, Keys.KEY_INDEX, Defaults.DEFAULT_INDEX));
            lightValue.setDimValue(50);
            lightValue.setOn(false);
            request.getLightValue().add(lightValue);
        }

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.setLight(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
    }

    @When("^receiving a set light request with \"([^\"]*)\" light values$")
    public void receivingASetLightRequestWithLightValues(final Integer nofLightValues,
            final Map<String, String> requestParameters) throws Throwable {
        final SetLightRequest request = new SetLightRequest();
        request.setDeviceIdentification(
                getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

        for (int i = 0; i < nofLightValues; i++) {
            final LightValue lightValue = new LightValue();
            lightValue.setIndex(getInteger(requestParameters, Keys.KEY_INDEX, Defaults.DEFAULT_INDEX));
            lightValue.setDimValue(getInteger(requestParameters, Keys.KEY_DIMVALUE, Defaults.DEFAULT_DIMVALUE));
            lightValue.setOn(getBoolean(requestParameters, Keys.KEY_ON, Defaults.DEFAULT_ON));
            request.getLightValue().add(lightValue);
        }

        try {
            ScenarioContext.Current().put(Keys.RESPONSE, this.client.setLight(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current().put(Keys.RESPONSE, ex);
        }
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
    @Then("^the set light async response contains$")
    public void theSetLightResponseContains(final Map<String, String> expectedResponseData) throws Throwable {

        final SetLightAsyncResponse response = (SetLightAsyncResponse) ScenarioContext.Current().get(Keys.RESPONSE);

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

    @Then("^the set light response contains soap fault$")
    public void theSetLightResponseContainsSoapFault(final Map<String, String> expectedResult) {
        GenericResponseSteps.verifySoapFault(expectedResult);
    }

    @Then("^the platform buffers a set light response message for device \"([^\"]*)\"$")
    public void thePlatformBuffersASetLightResponseMessage(final String deviceIdentification,
            final Map<String, String> expectedResult) throws Throwable {
        final SetLightAsyncRequest request = new SetLightAsyncRequest();
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
                final SetLightResponse response = this.client.getSetLightResponse(request);

                Assert.assertEquals(Enum.valueOf(OsgpResultType.class, expectedResult.get(Keys.KEY_RESULT)),
                        response.getResult());

                success = true;
            } catch (final Exception ex) {
                // Do nothing
            }
        }
    }
}
