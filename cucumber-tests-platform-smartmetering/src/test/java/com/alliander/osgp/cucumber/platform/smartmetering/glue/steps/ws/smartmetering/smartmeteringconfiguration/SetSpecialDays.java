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
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetSpecialDaysRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SetSpecialDays {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SetSpecialDays.class);

    @Autowired
    private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

    @When("^the set special days request is received$")
    public void theSetSpecialDaysRequestIsReceived(final Map<String, String> requestData) throws Throwable {
        final SetSpecialDaysRequest setSpecialDaysRequest = SetSpecialDaysRequestFactory.fromParameterMap(requestData);

        final SetSpecialDaysAsyncResponse setSpecialDaysAsyncResponse = this.smartMeteringConfigurationClient
                .setSpecialDays(setSpecialDaysRequest);

        LOGGER.info("Set special days response is received {}", setSpecialDaysAsyncResponse);

        assertNotNull("Set special days response should not be null", setSpecialDaysAsyncResponse);
        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
                setSpecialDaysAsyncResponse.getCorrelationUid());
    }

    @Then("^the special days should be set on the device$")
    public void theSpecialDaysShouldBeSetOnTheDevice(final Map<String, String> settings) throws Throwable {
        final SetSpecialDaysAsyncRequest setSpecialDaysAsyncRequest = SetSpecialDaysRequestFactory
                .fromScenarioContext();
        final SetSpecialDaysResponse setSpecialDaysResponse = this.smartMeteringConfigurationClient
                .retrieveSetSpecialDaysResponse(setSpecialDaysAsyncRequest);

        LOGGER.info("Set special days result is: {}", setSpecialDaysResponse.getResult());

        assertNotNull("Set special days result is null", setSpecialDaysResponse.getResult());
        assertEquals("Set special days result should be OK", OsgpResultType.OK, setSpecialDaysResponse.getResult());
    }
}
