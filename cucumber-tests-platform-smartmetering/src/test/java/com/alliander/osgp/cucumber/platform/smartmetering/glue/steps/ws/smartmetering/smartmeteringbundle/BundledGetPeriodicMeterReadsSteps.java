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

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetPeriodicMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.PeriodicMeterReadsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.GetPeriodicMeterReadsRequestBuilder;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledGetPeriodicMeterReadsSteps extends BaseBundleSteps {

    @Given("^the bundle request contains a get periodic meter reads action$")
    public void theBundleRequestContainsAGetPeriodicMeterReadsAction() throws Throwable {

        final GetPeriodicMeterReadsRequest action = new GetPeriodicMeterReadsRequestBuilder().withDefaults().build();

        this.addActionToBundleRequest(action);
    }

    @Given("^the bundle request contains a get periodic meter reads action with parameters$")
    public void theBundleRequestContainsAGetPeriodicMeterReadsActionWithParameters(final Map<String, String> parameters)
            throws Throwable {

        final GetPeriodicMeterReadsRequest action = new GetPeriodicMeterReadsRequestBuilder()
                .fromParameterMap(parameters).build();

        this.addActionToBundleRequest(action);
    }

    @Then("^the bundle response should contain a get periodic meter reads response$")
    public void theBundleResponseShouldContainAGetPeriodicMeterReadsResponse() throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof PeriodicMeterReadsResponse);
    }
}
