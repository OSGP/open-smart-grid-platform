/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringadhoc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SpecificAttributeValueRequestFactory;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SpecificAttributeValue {

    @Autowired
    private SmartMeteringAdHocRequestClient<GetSpecificAttributeValueAsyncResponse, GetSpecificAttributeValueRequest> requestClient;

    @Autowired
    private SmartMeteringAdHocResponseClient<GetSpecificAttributeValueResponse, GetSpecificAttributeValueAsyncRequest> responseClient;

    @When("^the get specific attribute value request is received$")
    public void whenTheGetSpecificAttributeValueRequestIsReceived(final Map<String, String> settings) throws Throwable {

        final GetSpecificAttributeValueRequest request = SpecificAttributeValueRequestFactory
                .fromParameterMap(settings);
        final GetSpecificAttributeValueAsyncResponse asyncResponse = this.requestClient.doRequest(request);

        assertNotNull("AsyncResponse should not be null", asyncResponse);
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @Then("^a get specific attribute value response should be returned$")
    public void thenAGetSpecificAttributeValueResponseShouldBeReturned(final Map<String, String> settings)
            throws Throwable {

        final GetSpecificAttributeValueAsyncRequest asyncRequest = SpecificAttributeValueRequestFactory
                .fromScenarioContext();
        final GetSpecificAttributeValueResponse response = this.responseClient.getResponse(asyncRequest);

        assertEquals("Result is not as expected.", settings.get(PlatformSmartmeteringKeys.RESULT),
                response.getResult().name());
        assertTrue("Result contains no data.", StringUtils.isNotBlank(response.getAttributeValueData()));
        assertTrue("Result data is not as expected",
                response.getAttributeValueData().contains(settings.get(PlatformSmartmeteringKeys.RESPONSE_PART)));
    }
}
