/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws.core.deviceinstallation;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;
import static com.alliander.osgp.cucumber.platform.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.core.common.AsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.GetStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.GetStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.GetStatusRequest;
import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.GetStatusResponse;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.GlueBase;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.config.CoreDeviceConfiguration;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.support.ws.core.CoreDeviceInstallationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GetStatusSteps extends GlueBase {

	@Autowired
	private CoreDeviceInstallationClient client;

	@Autowired
	private CoreDeviceConfiguration configuration;

	@When("receiving a device installation get status request")
	public void receivingADeviceInstallationGetStatusRequest(final Map<String, String> settings) throws Throwable {
		final GetStatusRequest request = new GetStatusRequest();

		request.setDeviceIdentification(
				getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));

		try {
			ScenarioContext.Current().put(Keys.RESPONSE, this.client.getStatus(request));
		} catch (final SoapFaultClientException ex) {
			ScenarioContext.Current().put(Keys.RESPONSE, ex);
		}
	}

	@Then("the device installation get status async response contains")
	public void theDeviceInstallationGetStatusAsyncResponseContains(final Map<String, String> expectedResponseData)
			throws Throwable {
		final GetStatusAsyncResponse response = (GetStatusAsyncResponse) ScenarioContext.Current().get(Keys.RESPONSE);

		Assert.assertNotNull(response.getAsyncResponse().getCorrelationUid());
		Assert.assertEquals(getString(expectedResponseData, Keys.KEY_DEVICE_IDENTIFICATION),
				response.getAsyncResponse().getDeviceId());

		saveCorrelationUidInScenarioContext(response.getAsyncResponse().getCorrelationUid(),
				getString(expectedResponseData, Keys.KEY_ORGANIZATION_IDENTIFICATION,
						Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
	}

	/**
	 *
	 * @param deviceIdentification
	 * @throws Throwable
	 */
	@Then("the platform buffers a device installation get status response message for device \"([^\"]*)\"")
	public void thePlatformBuffersADeviceInstallationGetStatusResponseMessageForDevice(
			final String deviceIdentification, final Map<String, String> expectedResult) throws Throwable {
		final GetStatusAsyncRequest request = new GetStatusAsyncRequest();
		final AsyncRequest asyncRequest = new AsyncRequest();
		asyncRequest.setDeviceId(deviceIdentification);
		asyncRequest.setCorrelationUid((String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID));
		request.setAsyncRequest(asyncRequest);

		GetStatusResponse response = null;
		boolean success = false;
		int count = 0;
		while (!success) {
			if (count > this.configuration.getTimeout()) {
				Assert.fail("Timeout");
			}

			count++;
			Thread.sleep(1000);

			try {
				response = this.client.getStatusResponse(request);

				success = true;
			} catch (final Exception ex) {
				// Do nothing
			}
		}

		Assert.assertEquals(Enum.valueOf(OsgpResultType.class, expectedResult.get(Keys.KEY_RESULT)),
				response.getResult());
	}
}
