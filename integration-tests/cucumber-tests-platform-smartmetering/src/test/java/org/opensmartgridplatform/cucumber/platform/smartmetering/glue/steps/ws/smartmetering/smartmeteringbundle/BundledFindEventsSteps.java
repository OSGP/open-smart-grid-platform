/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.FindEventsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.FindEventsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.FindEventsRequestBuilder;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

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

        assertThat(response instanceof FindEventsResponse).as("Not a valid response").isTrue();
    }

}
