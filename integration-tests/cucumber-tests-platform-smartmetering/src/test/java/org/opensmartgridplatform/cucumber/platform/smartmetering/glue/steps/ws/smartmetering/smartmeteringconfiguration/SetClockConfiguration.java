/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationResponse;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.ScenarioContextHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetClockConfigurationRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SetClockConfiguration {

    @Autowired
    private SmartMeteringConfigurationClient client;

    @When("^the SetClockConfiguration request is received$")
    public void theSetClockConfigurationRequestIsReceived(final Map<String, String> settings) throws Throwable {
        final SetClockConfigurationRequest request = SetClockConfigurationRequestFactory.fromParameterMap(settings);
        final SetClockConfigurationAsyncResponse asyncResponse = this.client.setClockConfiguration(request);

        assertThat(asyncResponse).isNotNull();
        ScenarioContextHelper.saveAsyncResponse(asyncResponse);
    }

    @Then("^the set clock configuration response should be returned$")
    public void theSetClockConfigurationResponseShouldBeReturned(final Map<String, String> settings) throws Throwable {

        final SetClockConfigurationAsyncRequest asyncRequest = SetClockConfigurationRequestFactory
                .fromParameterMapAsync(settings);

        final SetClockConfigurationResponse response = this.client.getSetClockConfigurationResponse(asyncRequest);

        final String expectedResult = settings.get(PlatformKeys.KEY_RESULT);
        assertThat(response.getResult()).as("Result").isNotNull();
        assertThat(response.getResult().name()).as("Result").isEqualTo(expectedResult);
    }
}
