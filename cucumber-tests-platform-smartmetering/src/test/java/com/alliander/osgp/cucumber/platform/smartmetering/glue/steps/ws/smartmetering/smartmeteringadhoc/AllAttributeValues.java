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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetAllAttributeValuesResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.AllAttributeValuesRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AllAttributeValues {

    @Autowired
    private SmartMeteringAdHocRequestClient<GetAllAttributeValuesAsyncResponse, GetAllAttributeValuesRequest> requestClient;

    @Autowired
    private SmartMeteringAdHocResponseClient<GetAllAttributeValuesResponse, GetAllAttributeValuesAsyncRequest> responseClient;

    @When("^the get all attribute values request is received$")
    public void whenTheGetAllAttributeValuesRequestIsReceived(final Map<String, String> settings) throws Throwable {

        final GetAllAttributeValuesRequest request = AllAttributeValuesRequestFactory.fromParameterMap(settings);
        final GetAllAttributeValuesAsyncResponse asyncResponse = this.requestClient.doRequest(request);

        assertNotNull("AsyncResponse should not be null", asyncResponse);
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @Then("^a get all attribute values response should be returned$")
    public void thenAGetAllAttributeValuesResponseShouldBeReturned(final Map<String, String> settings)
            throws Throwable {

        final GetAllAttributeValuesAsyncRequest asyncRequest = AllAttributeValuesRequestFactory.fromScenarioContext();
        final GetAllAttributeValuesResponse response = this.responseClient.getResponse(asyncRequest);

        assertEquals("Result is not as expected", OsgpResultType.fromValue(settings.get(PlatformKeys.KEY_RESULT)),
                response.getResult());
        assertTrue("Response should contain Output", StringUtils.isNotBlank(response.getOutput()));
    }
}
