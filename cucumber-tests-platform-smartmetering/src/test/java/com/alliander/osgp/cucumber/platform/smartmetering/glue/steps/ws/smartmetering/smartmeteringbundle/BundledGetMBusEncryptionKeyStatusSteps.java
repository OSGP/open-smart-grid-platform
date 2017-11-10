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

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetMBusEncryptionKeyStatusRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetMBusEncryptionKeyStatusResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.GetMBusEncryptionKeyStatusRequestBuilder;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledGetMBusEncryptionKeyStatusSteps extends BaseBundleSteps {

    @Given("^the bundle request contains a get M-Bus encryption key status action$")
    public void theBundleRequestContainsAGetMBusEncryptionKeyStatusAction() throws Throwable {

        final GetMBusEncryptionKeyStatusRequest action = new GetMBusEncryptionKeyStatusRequestBuilder().withDefaults()
                .build();

        this.addActionToBundleRequest(action);
    }

    @Given("^the bundle request contains a get M-Bus encryption key status action with parameters$")
    public void theBundleRequestContainsAGetMBusEncryptionKeyStatusAction(final Map<String, String> parameters)
            throws Throwable {

        final GetMBusEncryptionKeyStatusRequest action = new GetMBusEncryptionKeyStatusRequestBuilder()
                .fromParameterMap(parameters).build();

        this.addActionToBundleRequest(action);
    }

    @Then("^the bundle response should contain a get M-Bus encryption key status response with values$")
    public void theBundleResponseShouldContainAGetMBusEncryptionKeyStatusResponse(final Map<String, String> values)
            throws Throwable {
        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof GetMBusEncryptionKeyStatusResponse);
        assertEquals("Result is not as expected.", values.get(PlatformSmartmeteringKeys.RESULT),
                response.getResult().name());
    }
}
