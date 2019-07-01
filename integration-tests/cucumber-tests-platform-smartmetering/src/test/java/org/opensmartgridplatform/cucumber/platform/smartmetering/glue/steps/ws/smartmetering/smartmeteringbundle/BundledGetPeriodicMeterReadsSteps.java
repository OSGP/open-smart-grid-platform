/**
 * Copyright 2017 Smart Society Services B.V.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.junit.Assert;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetPeriodicMeterReadsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.PeriodicMeterReadsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.FaultResponseData;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.GetPeriodicMeterReadsRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class BundledGetPeriodicMeterReadsSteps extends BaseBundleSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundledGetPeriodicMeterReadsSteps.class);

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

        FaultResponseData error = (FaultResponseData) response;

        LOGGER.info("Got PeriodicMeterReadResponse. exception == {}, result == {}", response.getException(), response.getResultString());

        String errorMessage = "Not a valid response " +
                error.getException() + " -- result -- " +
                response.getResultString() + " -- inner exception-- " +
                error.getInnerException() + "-- inner message -- " +
                error.getInnerMessage() + "-- message --" +
                error.getMessage() + " -- component -- " +
                error.getComponent() + "-- params --" +
                error.getParameters();

        Assert.assertTrue(errorMessage, response instanceof PeriodicMeterReadsResponse);
    }
}
