/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetClockConfigurationRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.dlms.Keys;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.configuration.SetClockConfigurationRequestDataFactory;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class SetClockConfigurationSteps extends BaseBundleSteps {

    @Given("^a set clock configuration action is part of a bundled request$")
    public void aSetClockConfigurationActionIsPartOfABundledRequest(final Map<String, String> settings)
            throws Throwable {

        final BundleRequest request = (BundleRequest) ScenarioContext.Current().get(Keys.BUNDLE_REQUEST);

        final SetClockConfigurationRequest action = this.defaultMapper.map(
                SetClockConfigurationRequestDataFactory.fromParameterMap(settings), SetClockConfigurationRequest.class);

        this.addActionToBundleRequest(request, action);
    }

    @Then("^the bundle response contains a set clock configuration response$")
    public void theBundleResponseContainsASetClockConfigurationResponse(final Map<String, String> settings)
            throws Throwable {
        final Response actionResponse = this.getNextBundleResponse();
        assertEquals(OsgpResultType.fromValue(settings.get(Keys.RESULT)), actionResponse.getResult());
    }
}
