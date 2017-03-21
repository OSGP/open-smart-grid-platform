/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationResponse;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.Helpers;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.configuration.SetClockConfigurationRequestFactory;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SetClockConfiguration {

    @Autowired
    private SmartMeteringConfigurationClient client;

    @When("^the SetClockConfiguration request is received$")
    public void theSetClockConfigurationRequestIsReceived(final Map<String, String> settings) throws Throwable {
        final SetClockConfigurationRequest request = SetClockConfigurationRequestFactory.fromParameterMap(settings);
        final SetClockConfigurationAsyncResponse asyncResponse = this.client.setClockConfiguration(request);

        assertNotNull(asyncResponse);
        Helpers.saveAsyncResponse(asyncResponse);
    }

    @Then("^the set clock configuration response should be returned$")
    public void theSetClockConfigurationResponseShouldBeReturned(final Map<String, String> settings) throws Throwable {

        final SetClockConfigurationAsyncRequest asyncRequest = SetClockConfigurationRequestFactory
                .fromParameterMapAsync(settings);

        final SetClockConfigurationResponse response = this.client.getSetClockConfigurationResponse(asyncRequest);

        final String expectedResult = settings.get(Keys.KEY_RESULT);
        assertNotNull("Result", response.getResult());
        assertEquals("Result", expectedResult, response.getResult().name());
    }
}
