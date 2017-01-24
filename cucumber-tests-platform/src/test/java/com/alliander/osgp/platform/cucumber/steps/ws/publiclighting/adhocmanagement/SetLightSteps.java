/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.ws.publiclighting.adhocmanagement;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getInteger;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.saveCorrelationUidInScenarioContext;

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
import com.alliander.osgp.platform.cucumber.config.CoreDeviceConfiguration;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;
import com.alliander.osgp.platform.cucumber.steps.ws.GenericResponseSteps;
import com.alliander.osgp.platform.cucumber.support.ws.publiclighting.PublicLightingAdHocManagementClient;

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

		SetLightRequest request = new SetLightRequest();
		request.setDeviceIdentification(
				getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
		LightValue lightValue = new LightValue();
		lightValue.setIndex(getInteger(requestParameters, Keys.KEY_INDEX, Defaults.DEFAULT_INDEX));
		if (requestParameters.containsKey(Keys.KEY_DIMVALUE)
				&& !StringUtils.isEmpty(requestParameters.get(Keys.KEY_DIMVALUE))) {
			lightValue.setDimValue(getInteger(requestParameters, Keys.KEY_DIMVALUE, Defaults.DEFAULT_DIMVALUE));
		}
		lightValue.setOn(getBoolean(requestParameters, Keys.KEY_ON, Defaults.DEFAULT_ON));
		request.getLightValue().add(lightValue);

		try {
			ScenarioContext.Current().put(Keys.RESPONSE, client.setLight(request));
		} catch (SoapFaultClientException ex) {
			ScenarioContext.Current().put(Keys.RESPONSE, ex);
		}
	}

	@When("^receiving a set light request with \"([^\"]*)\" valid lightvalues and \"([^\"]*)\" invalid lightvalues$")
	public void receivingAsetLightRequestWithValidLightValuesAndInvalidLightValues(final Integer nofValidLightValues,
			final Integer nofInvalidLightValues, final Map<String, String> requestParameters)
			throws Throwable {
		SetLightRequest request = new SetLightRequest();
		request.setDeviceIdentification(
				getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

		for (int i = 0; i < nofValidLightValues; i++) {
			LightValue lightValue = new LightValue();
			lightValue.setIndex(getInteger(requestParameters, Keys.KEY_INDEX, Defaults.DEFAULT_INDEX));
			lightValue.setDimValue(getInteger(requestParameters, Keys.KEY_DIMVALUE, Defaults.DEFAULT_DIMVALUE));
			lightValue.setOn(getBoolean(requestParameters, Keys.KEY_ON, Defaults.DEFAULT_ON));
			request.getLightValue().add(lightValue);
		}

		for (int i = 0; i < nofInvalidLightValues; i++) {
			LightValue lightValue = new LightValue();
			lightValue.setIndex(getInteger(requestParameters, Keys.KEY_INDEX, Defaults.DEFAULT_INDEX));
			lightValue.setDimValue(50);
			lightValue.setOn(false);
			request.getLightValue().add(lightValue);
		}

		try {
			ScenarioContext.Current().put(Keys.RESPONSE, client.setLight(request));
		} catch (SoapFaultClientException ex) {
			ScenarioContext.Current().put(Keys.RESPONSE, ex);
		}
	}

	@When("^receiving a set light request with \"([^\"]*)\" light values$")
	public void receivingASetLightRequestWithLightValues(final Integer nofLightValues,
			final Map<String, String> requestParameters) throws Throwable {
		SetLightRequest request = new SetLightRequest();
		request.setDeviceIdentification(
				getString(requestParameters, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

		for (int i = 0; i < nofLightValues; i++) {
			LightValue lightValue = new LightValue();
			lightValue.setIndex(getInteger(requestParameters, Keys.KEY_INDEX, Defaults.DEFAULT_INDEX));
			lightValue.setDimValue(getInteger(requestParameters, Keys.KEY_DIMVALUE, Defaults.DEFAULT_DIMVALUE));
			lightValue.setOn(getBoolean(requestParameters, Keys.KEY_ON, Defaults.DEFAULT_ON));
			request.getLightValue().add(lightValue);
		}

		try {
			ScenarioContext.Current().put(Keys.RESPONSE, client.setLight(request));
		} catch (SoapFaultClientException ex) {
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

		SetLightAsyncResponse response = (SetLightAsyncResponse) ScenarioContext.Current().get(Keys.RESPONSE);

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
		SetLightAsyncRequest request = new SetLightAsyncRequest();
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
				SetLightResponse response = client.getSetLightResponse(request);

				Assert.assertEquals(Enum.valueOf(OsgpResultType.class, expectedResult.get(Keys.KEY_RESULT)),
						response.getResult());

				success = true;
			} catch (Exception ex) {
				// Do nothing
			}
		}
	}
}
