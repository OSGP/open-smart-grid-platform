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

import java.time.Instant;
import java.time.ZoneId;
import java.time.zone.ZoneRules;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SynchronizeTimeRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SynchronizeTimeRequestDataFactory;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledSynchronizeTimeSteps extends BaseBundleSteps {

    private static final String DEFAULT_TIMEZONE = "Europe/Amsterdam";

    @Given("^the bundle request contains a synchronize time action$")
    public void theBundleRequestContainsASynchronizeTimeAction() throws Throwable {

        this.theBundleRequestContainsAValidSynchronizeTimeAction(DEFAULT_TIMEZONE);
    }

    @Given("^the bundle request contains a synchronize time action with parameters$")
    public void theBundleRequestContainsASynchronizeTimeAction(final Map<String, String> settings) throws Throwable {

        final SynchronizeTimeRequest action = this.mapperFacade
                .map(SynchronizeTimeRequestDataFactory.fromParameterMap(settings), SynchronizeTimeRequest.class);

        this.addActionToBundleRequest(action);
    }

    @Given("^the bundle request contains a valid synchronize time action for timezone \"([^\"]*)\"")
    public void theBundleRequestContainsAValidSynchronizeTimeAction(final String timeZoneId) throws Throwable {

        final SynchronizeTimeRequest action = new SynchronizeTimeRequest();

        final ZoneId zone = ZoneId.of(timeZoneId);
        final Instant now = Instant.now();
        final ZoneRules rules = zone.getRules();

        final int offset = (rules.getOffset(now).getTotalSeconds() / 60) * -1;
        final boolean dst = rules.isDaylightSavings(now);

        action.setDeviation(offset);
        action.setDst(dst);

        this.addActionToBundleRequest(action);
    }

    @Then("^the bundle response should contain a synchronize time response$")
    public void theBundleResponseShouldContainASynchronizeTimeResponse() throws Throwable {
        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof ActionResponse);
    }

    @Then("^the bundle response should contain a synchronize time response with values$")
    public void theBundleResponseShouldContainASynchronizeTimeResponse(final Map<String, String> values)
            throws Throwable {
        final Response response = this.getNextBundleResponse();

        assertEquals(OsgpResultType.fromValue(values.get(PlatformSmartmeteringKeys.RESULT)), response.getResult());
    }

}
