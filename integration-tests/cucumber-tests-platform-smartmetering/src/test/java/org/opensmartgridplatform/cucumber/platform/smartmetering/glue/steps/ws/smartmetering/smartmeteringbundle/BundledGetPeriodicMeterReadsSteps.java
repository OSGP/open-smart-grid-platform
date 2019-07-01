/**
 * Copyright 2017 Smart Society Services B.V.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import java.util.Map;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.junit.Assert;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.FaultResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetPeriodicMeterReadsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.PeriodicMeterReadsResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.GetPeriodicMeterReadsRequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundledGetPeriodicMeterReadsSteps extends BaseBundleSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundledGetPeriodicMeterReadsSteps.class);

    private GetPeriodicMeterReadsRequest action1;
    private GetPeriodicMeterReadsRequest action2;

    @Given("^the bundle request contains a get periodic meter reads action$")
    public void theBundleRequestContainsAGetPeriodicMeterReadsAction() throws Throwable {

        this.action1 = new GetPeriodicMeterReadsRequestBuilder().withDefaults().build();

        this.addActionToBundleRequest(this.action1);
    }

    @Given("^the bundle request contains a get periodic meter reads action with parameters$")
    public void theBundleRequestContainsAGetPeriodicMeterReadsActionWithParameters(final Map<String, String> parameters)
            throws Throwable {

        this.action2 = new GetPeriodicMeterReadsRequestBuilder().fromParameterMap(parameters).build();

        this.addActionToBundleRequest(this.action2);
    }

    @Then("^the bundle response should contain a get periodic meter reads response$")
    public void theBundleResponseShouldContainAGetPeriodicMeterReadsResponse() throws Throwable {

        final Response response = this.getNextBundleResponse();

        final FaultResponse error = (FaultResponse) response;

        LOGGER.info("Got PeriodicMeterReadResponse. exception == {}, result == {}", response.getException(),
                response.getResultString());

        final String action1Debug = this.action1.getBeginDate() + " -- " + this.action1.getEndDate() + " -- "
                + this.action1.getPeriodType();

        final String action2Debug = this.action2.getBeginDate() + " -- " + this.action2.getEndDate() + " -- "
                + this.action2.getPeriodType();

        final String errorMessage =
                "Not a valid response " + error.getException() + " -- result -- " + response.getResultString()
                        + " -- inner exception-- " + error.getInnerException() + "-- inner message -- "
                        + error.getInnerMessage() + "-- message --" + error.getMessage() + " -- component -- "
                        + error.getComponent() + "-- params --" + error.getParameters() + " -- action 1 -- "
                        + action1Debug + "-- action 2 -- " + action2Debug;

        Assert.assertTrue(errorMessage, response instanceof PeriodicMeterReadsResponse);
    }
}
