/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertTrue;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ReadAlarmRegisterRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ReadAlarmRegisterResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledReadAlarmRegisterSteps extends BaseBundleSteps {

    @Given("^the bundle request contains a read alarm register action$")
    public void theBundleRequestContainsAReadAlarmRegisterAction() throws Throwable {

        final ReadAlarmRegisterRequest action = new ReadAlarmRegisterRequest();

        this.addActionToBundleRequest(action);
    }

    @Then("^the bundle response should contain a read alarm register response$")
    public void theBundleResponseShouldContainAReadAlarmRegisterResponse() throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof ReadAlarmRegisterResponse);
    }
}
