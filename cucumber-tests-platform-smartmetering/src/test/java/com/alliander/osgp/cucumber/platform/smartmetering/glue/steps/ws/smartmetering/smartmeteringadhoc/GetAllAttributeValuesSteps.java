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

import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocClient;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GetAllAttributeValuesSteps {

    private static final String REQUEST = "REQUEST";
    private static final String ASYNC_RESPONSE = "ASYNC_RESPONSE";

    @Autowired
    private SmartMeteringAdHocClient client;

    @Given("^a get all attribute values request$")
    public void givenAGetAllAttributeValuesRequest(final Map<String, String> settings) throws Throwable {

        final GetAllAttributeValuesRequest request = new GetAllAttributeValuesRequest();
        request.setDeviceIdentification(settings.get(PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION));

        ScenarioContext.current().put(REQUEST, request);
    }

    @When("^the get all attribute values request is received$")
    public void whenTheGetAllAttributeValuesRequestIsReceived() throws Throwable {
        final GetAllAttributeValuesRequest request = (GetAllAttributeValuesRequest) ScenarioContext.current()
                .get(REQUEST);

        final GetAllAttributeValuesAsyncResponse asyncResponse = this.client.sendGetAllAttributeValuesRequest(request);

        ScenarioContext.current().put(ASYNC_RESPONSE, asyncResponse);
    }

    @Then("^a get all attribute values response should be returned$")
    public void thenAGetAllAttributeValuesResponseShouldBeReturned(final Map<String, String> settings)
            throws Throwable {

        final GetAllAttributeValuesAsyncResponse asyncResponse = (GetAllAttributeValuesAsyncResponse) ScenarioContext
                .current().get(ASYNC_RESPONSE);

        final GetAllAttributeValuesAsyncRequest asyncRequest = new GetAllAttributeValuesAsyncRequest();
        asyncRequest.setCorrelationUid(asyncResponse.getCorrelationUid());
        asyncRequest.setDeviceIdentification(asyncResponse.getDeviceIdentification());

        final GetAllAttributeValuesResponse response = this.client.retrieveGetAllAttributeValuesResponse(asyncRequest);

        // TODO: Use expected result from settings
        assertEquals(OsgpResultType.OK, response.getResult());
        assertTrue(StringUtils.isNotBlank(response.getOutput()));
    }
}
