/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.SmartMeteringStepsBase;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.GetAdministrativeStatusRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GetAdministrativeStatus extends SmartMeteringStepsBase {
    protected static final Logger LOGGER = LoggerFactory.getLogger(GetAdministrativeStatus.class);

    @Autowired
    private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

    @When("^the get administrative status request is received$")
    public void theRetrieveAdministrativeStatusRequestIsReceived(final Map<String, String> requestData)
            throws Throwable {
        final GetAdministrativeStatusRequest getAdministrativeStatusRequest = GetAdministrativeStatusRequestFactory
                .fromParameterMap(requestData);

        final GetAdministrativeStatusAsyncResponse getAdministrativeStatusAsyncResponse = this.smartMeteringConfigurationClient
                .getAdministrativeStatus(getAdministrativeStatusRequest);

        LOGGER.info("Get administrative status asyncResponse is received {}", getAdministrativeStatusAsyncResponse);
        assertNotNull("Get administrative status asyncResponse should not be null",
                getAdministrativeStatusAsyncResponse);
        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
                getAdministrativeStatusAsyncResponse.getCorrelationUid());
    }

    @Then("^the administrative status should be returned$")
    public void theAdministrativeStatusShouldBeReturned(final Map<String, String> settings) throws Throwable {
        final GetAdministrativeStatusAsyncRequest getAdministrativeStatusAsyncRequest = GetAdministrativeStatusRequestFactory
                .fromScenarioContext();
        final GetAdministrativeStatusResponse getAdministrativeStatusResponse = this.smartMeteringConfigurationClient
                .retrieveGetAdministrativeStatusResponse(getAdministrativeStatusAsyncRequest);

        LOGGER.info("The administrative status is: {}", getAdministrativeStatusResponse.getEnabled());
        assertNotNull("Administrative status type is null", getAdministrativeStatusResponse.getEnabled());
    }

}
