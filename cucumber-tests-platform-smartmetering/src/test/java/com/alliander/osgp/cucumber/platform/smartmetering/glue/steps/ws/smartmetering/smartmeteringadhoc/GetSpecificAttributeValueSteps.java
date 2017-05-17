/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringadhoc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.GetSpecificAttributeValueRequestBuilder;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocClient;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GetSpecificAttributeValueSteps {

    private static final String REQUEST = "REQUEST";
    private static final String ASYNC_RESPONSE = "ASYNC_RESPONSE";

    @Autowired
    private SmartMeteringAdHocClient client;

    @Given("^a get specific attribute value request$")
    public void givenAGetSpecificAttributeValueRequest(final Map<String, String> settings) throws Throwable {

        final GetSpecificAttributeValueRequest request = new GetSpecificAttributeValueRequestBuilder()
                .fromParameterMap(settings).build();

        ScenarioContext.current().put(REQUEST, request);
    }

    @When("^the get specific attribute value request is received$")
    public void whenTheGetSpecificAttributeValueRequestIsReceived() throws Throwable {
        final GetSpecificAttributeValueRequest request = (GetSpecificAttributeValueRequest) ScenarioContext.current()
                .get(REQUEST);

        final GetSpecificAttributeValueAsyncResponse asyncResponse = this.client
                .sendGetSpecificAttributeValueRequest(request);

        ScenarioContext.current().put(ASYNC_RESPONSE, asyncResponse);
    }

    @Then("^a get specific attribute value response should be returned$")
    public void thenAGetSpecificAttributeValueResponseShouldBeReturned(final Map<String, String> settings)
            throws Throwable {

        final GetSpecificAttributeValueAsyncResponse asyncResponse = (GetSpecificAttributeValueAsyncResponse) ScenarioContext
                .current().get(ASYNC_RESPONSE);

        final GetSpecificAttributeValueAsyncRequest asyncRequest = new GetSpecificAttributeValueAsyncRequest();
        asyncRequest.setCorrelationUid(asyncResponse.getCorrelationUid());
        asyncRequest.setDeviceIdentification(asyncResponse.getDeviceIdentification());

        final GetSpecificAttributeValueResponse response = this.client
                .retrieveGetSpecificAttributeValueResponse(asyncRequest);

        assertEquals("Result is not as expected.", settings.get(PlatformSmartmeteringKeys.RESULT), response.getResult().name());
        assertTrue("Result contains no data.", StringUtils.isNotBlank(response.getAttributeValueData()));
        assertTrue("Result data is not as expected",
                response.getAttributeValueData().contains(settings.get(PlatformSmartmeteringKeys.RESPONSE_PART)));
    }
}
