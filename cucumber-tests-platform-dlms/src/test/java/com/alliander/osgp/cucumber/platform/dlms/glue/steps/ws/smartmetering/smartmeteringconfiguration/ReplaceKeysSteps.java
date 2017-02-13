/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ReplaceKeysAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ReplaceKeysAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ReplaceKeysRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ReplaceKeysResponse;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.AbstractSmartMeteringSteps;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.configuration.ReplaceKeysRequestFactory;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import com.alliander.osgp.cucumber.platform.helpers.SettingsHelper;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ReplaceKeysSteps extends AbstractSmartMeteringSteps {

    @Autowired
    private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

    @When("^the replace keys request is received$")
    public void theReplaceKeysRequestIsReceived(final Map<String, String> settings) throws Throwable {

        ScenarioContext.Current().put(Keys.KEY_DEVICE_IDENTIFICATION, settings.get(Keys.KEY_DEVICE_IDENTIFICATION));
        ScenarioContext.Current().put(Keys.KEY_DEVICE_AUTHENTICATIONKEY,
                settings.get(Keys.KEY_DEVICE_AUTHENTICATIONKEY));
        ScenarioContext.Current().put(Keys.KEY_DEVICE_ENCRYPTIONKEY, settings.get(Keys.KEY_DEVICE_ENCRYPTIONKEY));

        final ReplaceKeysRequest request = ReplaceKeysRequestFactory.fromParameterMap(settings);
        final ReplaceKeysAsyncResponse asyncResponse = this.smartMeteringConfigurationClient.replaceKeys(request);

        ScenarioContext.Current().put(Keys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @Then("^the replace keys response should be returned$")
    public void theReplaceKeysResponseShouldBeReturned(final Map<String, String> responseParameters) throws Throwable {

        final String correlationUid = (String) ScenarioContext.Current().get(Keys.KEY_CORRELATION_UID);
        final Map<String, String> extendedParameters = SettingsHelper.addDefault(responseParameters,
                Keys.KEY_CORRELATION_UID, correlationUid);

        final ReplaceKeysAsyncRequest replaceKeysAsyncRequest = ReplaceKeysRequestFactory
                .fromParameterMapAsync(extendedParameters);

        final ReplaceKeysResponse response = this.smartMeteringConfigurationClient
                .getReplaceKeysResponse(replaceKeysAsyncRequest);

        final String expectedResult = responseParameters.get(Keys.KEY_RESULT);
        assertNotNull("Result", response.getResult());
        assertEquals("Result", expectedResult, response.getResult().name());
    }
}
