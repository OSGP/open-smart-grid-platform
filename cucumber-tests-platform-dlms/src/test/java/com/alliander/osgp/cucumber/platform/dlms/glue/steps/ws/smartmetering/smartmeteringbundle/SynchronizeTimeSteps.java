/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.ZoneId;
import java.time.zone.ZoneRules;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SynchronizeTimeRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.dlms.Keys;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.configuration.SynchronizeTimeRequestDataFactory;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class SynchronizeTimeSteps extends BaseBundleSteps {

    @Given("^a synchronize time action is part of a bundled request$")
    public void aSynchronizeTimeActionIsPartOfABundledRequest(final Map<String, String> settings) throws Throwable {

        final BundleRequest request = (BundleRequest) ScenarioContext.Current().get(Keys.BUNDLE_REQUEST);

        final SynchronizeTimeRequest action = this.defaultMapper
                .map(SynchronizeTimeRequestDataFactory.fromParameterMap(settings), SynchronizeTimeRequest.class);

        this.addActionToBundleRequest(request, action);
    }

    @Given("^a valid synchronize time action for timezone \"([^\"]*)\" is part of a bundled request$")
    public void aValidSynchronizeTimeActionForTimezoneIsPartOfABundledRequest(final String timeZoneId)
            throws Throwable {
        final BundleRequest request = (BundleRequest) ScenarioContext.Current().get(Keys.BUNDLE_REQUEST);

        final SynchronizeTimeRequest action = new SynchronizeTimeRequest();

        final ZoneId zone = ZoneId.of(timeZoneId);
        final Instant now = Instant.now();
        final ZoneRules rules = zone.getRules();

        final int offset = (rules.getOffset(now).getTotalSeconds() / 60) * -1;
        final boolean dst = rules.isDaylightSavings(now);

        action.setDeviation(offset);
        action.setDst(dst);

        this.addActionToBundleRequest(request, action);
    }

    @Then("^the bundle response contains a synchronize time response$")
    public void theBundleResponseContainsASynchronizeTimeResponse(final Map<String, String> settings) throws Throwable {
        final Response actionResponse = this.getNextBundleResponse();
        assertEquals(OsgpResultType.fromValue(settings.get(Keys.RESULT)), actionResponse.getResult());
    }

}
