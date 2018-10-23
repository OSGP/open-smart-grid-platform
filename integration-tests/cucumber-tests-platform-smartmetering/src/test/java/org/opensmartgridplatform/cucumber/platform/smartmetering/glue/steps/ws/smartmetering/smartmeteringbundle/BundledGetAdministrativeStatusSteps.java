/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertTrue;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.AdministrativeStatusResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.GetAdministrativeStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledGetAdministrativeStatusSteps extends BaseBundleSteps {

    @Given("^the bundle request contains a get administrative status action$")
    public void theBundleRequestContainsAGetAdministrativeStatusAction() throws Throwable {

        final GetAdministrativeStatusRequest action = new GetAdministrativeStatusRequest();

        this.addActionToBundleRequest(action);
    }

    @Then("^the bundle response should contain a get administrative status response$")
    public void theBundleResponseShouldContainAGetAdministrativeStatusResponse() throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof AdministrativeStatusResponse);
    }
}
