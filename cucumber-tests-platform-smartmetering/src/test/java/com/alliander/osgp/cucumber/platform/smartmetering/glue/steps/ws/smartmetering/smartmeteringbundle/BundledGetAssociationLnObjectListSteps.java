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

import org.apache.commons.lang3.StringUtils;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.AssociationLnObjectsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetAssociationLnObjectsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledGetAssociationLnObjectListSteps extends BaseBundleSteps {

    @Given("^the bundle request contains a get association ln objects action$")
    public void theBundleRequestContainsAGetAssociationLnObjectsAction() throws Throwable {

        final GetAssociationLnObjectsRequest action = new GetAssociationLnObjectsRequest();

        this.addActionToBundleRequest(action);
    }

    @Then("^the bundle response should contain a get association ln objects response$")
    public void theBundleResponseShouldContainAGetAssociationLnObjectsResponse() throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof AssociationLnObjectsResponse);
    }

    @Then("^the bundle response should contain a get association ln objects response with values$")
    public void theBundleResponseShouldContainAGetAssociationLnObjectsResponse(final Map<String, String> values)
            throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof AssociationLnObjectsResponse);
        assertEquals("Result is not as expected.", values.get(PlatformSmartmeteringKeys.RESULT),
                response.getResult().name());
        assertTrue("Result contains no data.", StringUtils.isNotBlank(response.getResultString()));
    }
}
