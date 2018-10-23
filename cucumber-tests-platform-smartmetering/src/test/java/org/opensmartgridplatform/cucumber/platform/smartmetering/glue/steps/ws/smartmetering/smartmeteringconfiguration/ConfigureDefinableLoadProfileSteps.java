/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileResponse;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.ScenarioContextHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.ConfigureDefinableLoadProfileRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ConfigureDefinableLoadProfileSteps {

    @Autowired
    private SmartMeteringConfigurationClient client;

    @When("^a Configure Definable Load Profile request is received$")
    public void aConfigureDefinableLoadProfileRequestIsReceived(final Map<String, String> settings) throws Throwable {
        final ConfigureDefinableLoadProfileRequest request = ConfigureDefinableLoadProfileRequestFactory
                .fromParameterMap(settings);
        final ConfigureDefinableLoadProfileAsyncResponse asyncResponse = this.client
                .configureDefinableLoadProfile(request);

        assertNotNull(asyncResponse);
        ScenarioContextHelper.saveAsyncResponse(asyncResponse);
    }

    @Then("^the Configure Definable Load Profile response should be returned$")
    public void theConfigureDefinableLoadProfileResponseShouldBeReturned(final Map<String, String> settings)
            throws Throwable {

        final ConfigureDefinableLoadProfileAsyncRequest asyncRequest = ConfigureDefinableLoadProfileRequestFactory
                .fromParameterMapAsync(settings);

        final ConfigureDefinableLoadProfileResponse response = this.client
                .getConfigureDefinableLoadProfileResponse(asyncRequest);

        final String expectedResult = settings.get(PlatformKeys.KEY_RESULT);
        assertNotNull("Result", response.getResult());
        assertEquals("Result", expectedResult, response.getResult().name());
    }
}
