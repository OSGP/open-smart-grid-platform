/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetAlarmNotificationsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.Response;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.SetAlarmNotificationsRequestBuilder;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledSetAlarmNotificationsSteps extends BaseBundleSteps {

    @Given("^the bundle request contains a set alarm notifications action$")
    public void theBundleRequestContainsASetAlarmNotificationsAction() throws Throwable {

        final SetAlarmNotificationsRequest action = new SetAlarmNotificationsRequestBuilder().withDefaults().build();

        this.addActionToBundleRequest(action);
    }

    @Given("^the bundle request contains a set alarm notifications action with parameters$")
    public void theBundleRequestContainsASetAlarmNotificationsAction(final Map<String, String> parameters)
            throws Throwable {

        final SetAlarmNotificationsRequest action = new SetAlarmNotificationsRequestBuilder()
                .fromParameterMap(parameters).build();

        this.addActionToBundleRequest(action);
    }

    @Then("^the bundle response should contain a set alarm notifications response$")
    public void theBundleResponseShouldContainASetAlarmNotificationsResponse() throws Throwable {
        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof ActionResponse);
    }

    @Then("^the bundle response should contain a set alarm notifications response with values$")
    public void theBundleResponseShouldContainASetAlarmNotificationsResponse(final Map<String, String> values)
            throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof ActionResponse);
        assertEquals("Result is not as expected.", values.get(PlatformSmartmeteringKeys.RESULT),
                response.getResult().name());
    }

}
