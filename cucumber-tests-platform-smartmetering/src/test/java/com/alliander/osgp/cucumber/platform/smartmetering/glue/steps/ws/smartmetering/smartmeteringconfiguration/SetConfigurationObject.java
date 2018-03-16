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
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetConfigurationObjectRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SetConfigurationObject {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SetConfigurationObject.class);

    @Autowired
    private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

    @When("^the set configuration object request is received$")
    public void theSetConfigurationObjectRequestIsReceived(final Map<String, String> requestData) throws Throwable {
        final SetConfigurationObjectRequest setConfigurationObjectRequest = SetConfigurationObjectRequestFactory
                .fromParameterMap(requestData);

        final SetConfigurationObjectAsyncResponse setConfigurationObjectAsyncResponse = this.smartMeteringConfigurationClient
                .setConfigurationObject(setConfigurationObjectRequest);

        LOGGER.info("Set configuration object response is received {}", setConfigurationObjectAsyncResponse);

        assertNotNull("Set configuration object response should not be null", setConfigurationObjectAsyncResponse);
        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
                setConfigurationObjectAsyncResponse.getCorrelationUid());
    }

    @Then("^the configuration object should be set on the device$")
    public void theConfigurationObjectShouldBeSetOnTheDevice(final Map<String, String> settings) throws Throwable {
        final SetConfigurationObjectAsyncRequest setConfigurationObjectAsyncRequest = SetConfigurationObjectRequestFactory
                .fromScenarioContext();
        final SetConfigurationObjectResponse setConfigurationObjectResponse = this.smartMeteringConfigurationClient
                .retrieveSetConfigurationObjectResponse(setConfigurationObjectAsyncRequest);

        assertEquals("Set configuration object result", OsgpResultType.OK, setConfigurationObjectResponse.getResult());
    }
}
