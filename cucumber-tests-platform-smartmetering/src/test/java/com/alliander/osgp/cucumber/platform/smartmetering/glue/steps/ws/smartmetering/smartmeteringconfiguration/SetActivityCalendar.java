/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetActivityCalendarRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SetActivityCalendar extends SmartMeteringStepsBase {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SetActivityCalendar.class);

    @Autowired
    private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

    @When("^the set activity calendar request is received$")
    public void theSetActivityCalendarRequestIsReceived(final Map<String, String> requestData) throws Throwable {
        final SetActivityCalendarRequest setActivityCalendarRequest = SetActivityCalendarRequestFactory
                .fromParameterMap(requestData);

        final SetActivityCalendarAsyncResponse setActivityCalendarAsyncResponse = this.smartMeteringConfigurationClient
                .setActivityCalendar(setActivityCalendarRequest);

        LOGGER.info("Set activity calendar asyncResponse is received {}", setActivityCalendarAsyncResponse);
        assertNotNull("Set activity calendar asyncResponse should not be null", setActivityCalendarAsyncResponse);

        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
                setActivityCalendarAsyncResponse.getCorrelationUid());
    }

    @Then("^the activity calendar profiles are set on the device$")
    public void theActivityCalendarProfilesAreSetOnTheDevice(final Map<String, String> settings) throws Throwable {
        final SetActivityCalendarAsyncRequest setActivityCalendarAsyncRequest = SetActivityCalendarRequestFactory
                .fromScenarioContext();

        final SetActivityCalendarResponse setActivityCalendarResponse = this.smartMeteringConfigurationClient
                .getSetActivityCalendarResponse(setActivityCalendarAsyncRequest);

        LOGGER.info("Set activity calendar with result: {}", setActivityCalendarResponse.getResult().name());
        assertNotNull("Set activity calendar response is null", setActivityCalendarResponse.getResult());
        assertEquals("Set activity calendar response should be OK", OsgpResultType.OK,
                setActivityCalendarResponse.getResult());
    }
}
