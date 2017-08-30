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
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetClockConfigurationRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetClockConfigurationRequestDataFactory;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledSetClockConfigurationSteps extends BaseBundleSteps {

    @Given("^the bundle request contains a set clock configuration action with parameters$")
    public void theBundleRequestContainsASetClockConfigurationAction(final Map<String, String> settings)
            throws Throwable {

        final SetClockConfigurationRequest action = this.mapperFacade.map(
                SetClockConfigurationRequestDataFactory.fromParameterMap(settings), SetClockConfigurationRequest.class);

        this.addActionToBundleRequest(action);
    }

    @Then("^the bundle response should contain a set clock configuration response with values$")
    public void theBundleResponseShouldContainASetClockConfigurationResponse(final Map<String, String> settings)
            throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof ActionResponse);
        assertEquals(OsgpResultType.fromValue(settings.get(PlatformSmartmeteringKeys.RESULT)), response.getResult());
    }
}
