/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.FindEventsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.FindEventsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.FindEventsRequestBuilder;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledFindEventsSteps extends BaseBundleSteps {

    @Given("^the bundle request contains a find events action$")
    public void theBundleRequestContainsAFindEventsAction() throws Throwable {
        final FindEventsRequest action = new FindEventsRequestBuilder().withDefaults().build();

        this.addActionToBundleRequest(action);
    }

    @Given("^the bundle request contains a find events action with parameters$")
    public void theBundleRequestContainsAFindEventsAction(final Map<String, String> parameters) throws Throwable {
        final FindEventsRequest action = new FindEventsRequestBuilder().fromParameterMap(parameters).build();

        this.addActionToBundleRequest(action);
    }

    @Then("^the bundle response should contain a find events response$")
    public void theBundleResponseShouldContainAFindEventsResponse() throws Throwable {
        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof FindEventsResponse);
    }

}
