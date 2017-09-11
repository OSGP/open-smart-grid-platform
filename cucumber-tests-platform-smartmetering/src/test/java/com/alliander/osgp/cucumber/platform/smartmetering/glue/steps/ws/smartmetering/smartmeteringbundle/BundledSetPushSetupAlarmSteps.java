/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetPushSetupAlarmRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.SetPushSetupAlarmRequestBuilder;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledSetPushSetupAlarmSteps extends BaseBundleSteps {

    @Given("^the bundle request contains a set push setup alarm action$")
    public void theBundleRequestContainsASetPushSetupAlarmAction() throws Throwable {

        final SetPushSetupAlarmRequest action = new SetPushSetupAlarmRequestBuilder().withDefaults().build();

        this.addActionToBundleRequest(action);
    }

    @Given("^the bundle request contains a set push setup alarm action with parameters$")
    public void theBundleRequestContainsASetPushSetupAlarmAction(final Map<String, String> parameters)
            throws Throwable {

        final SetPushSetupAlarmRequest action = new SetPushSetupAlarmRequestBuilder().fromParameterMap(parameters)
                .build();

        this.addActionToBundleRequest(action);
    }

    @Then("^the bundle response should contain a set push setup alarm response$")
    public void theBundleResponseShouldContainASetPushSetupAlarmResponse() throws Throwable {
        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof ActionResponse);
    }

    @Then("^the bundle response should contain a set push setup alarm response with values$")
    public void theBundleResponseShouldContainASetPushSetupAlarmResponse(final Map<String, String> values)
            throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof ActionResponse);
        assertEquals("Result is not as expected.", values.get(PlatformSmartmeteringKeys.RESULT),
                response.getResult().name());
    }

}
