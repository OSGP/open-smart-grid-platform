/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ReplaceKeysAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ReplaceKeysAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ReplaceKeysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ReplaceKeysResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.AbstractSmartMeteringSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.GenerateAndReplaceKeysRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.ReplaceKeysRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ReplaceKeysSteps extends AbstractSmartMeteringSteps {

    @Autowired
    private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

    @When("^the replace keys request is received$")
    public void theReplaceKeysRequestIsReceived(final Map<String, String> settings) throws Throwable {
        ScenarioContext.current().put(PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
        ScenarioContext.current().put(PlatformKeys.KEY_DEVICE_AUTHENTICATIONKEY,
                settings.get(PlatformKeys.KEY_DEVICE_AUTHENTICATIONKEY));
        ScenarioContext.current().put(PlatformKeys.KEY_DEVICE_ENCRYPTIONKEY,
                settings.get(PlatformKeys.KEY_DEVICE_ENCRYPTIONKEY));
        final ReplaceKeysRequest request = ReplaceKeysRequestFactory.fromParameterMap(settings);
        final ReplaceKeysAsyncResponse asyncResponse = this.smartMeteringConfigurationClient.replaceKeys(request);
        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @Then("^the replace keys response should be returned$")
    public void theReplaceKeysResponseShouldBeReturned(final Map<String, String> responseParameters) throws Throwable {
        final String correlationUid = (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
        final Map<String, String> extendedParameters = SettingsHelper.addDefault(responseParameters,
                PlatformKeys.KEY_CORRELATION_UID, correlationUid);
        final ReplaceKeysAsyncRequest replaceKeysAsyncRequest = ReplaceKeysRequestFactory
                .fromParameterMapAsync(extendedParameters);

        final ReplaceKeysResponse response = this.smartMeteringConfigurationClient
                .getReplaceKeysResponse(replaceKeysAsyncRequest);

        final String expectedResult = responseParameters.get(PlatformKeys.KEY_RESULT);
        assertNotNull("Result", response.getResult());
        assertEquals("Result", expectedResult, response.getResult().name());
    }

    @Then("^the replace keys response generating an error is received$")
    public void theReplaceKeysResponseGeneratingAnErrorIsReceived(final Map<String, String> responseParameters)
            throws Throwable {
        final String correlationUid = (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
        final Map<String, String> extendedParameters = SettingsHelper.addDefault(responseParameters,
                PlatformKeys.KEY_CORRELATION_UID, correlationUid);
        try {
            final ReplaceKeysAsyncRequest replaceKeysAsyncRequest = ReplaceKeysRequestFactory
                    .fromParameterMapAsync(extendedParameters);

            this.smartMeteringConfigurationClient.getReplaceKeysResponse(replaceKeysAsyncRequest);
        } catch (final Exception exception) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, exception);
        }
    }

    @When("^the generate and replace keys request is received$")
    public void theGenerateAndReplaceKeysRequestIsReceived(final Map<String, String> settings) throws Throwable {
        ScenarioContext.current().put(PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                settings.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
        final GenerateAndReplaceKeysRequest request = GenerateAndReplaceKeysRequestFactory.fromParameterMap(settings);
        final GenerateAndReplaceKeysAsyncResponse asyncResponse = this.smartMeteringConfigurationClient
                .generateAndReplaceKeys(request);

        ScenarioContext.current().put(PlatformKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @Then("^the generate and replace keys response should be returned$")
    public void theGenerateAndReplaceKeysResponseShouldBeReturned(final Map<String, String> responseParameters)
            throws Throwable {
        final String correlationUid = (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
        final Map<String, String> extendedParameters = SettingsHelper.addDefault(responseParameters,
                PlatformKeys.KEY_CORRELATION_UID, correlationUid);

        final GenerateAndReplaceKeysAsyncRequest generateAndReplaceKeysAsyncRequest = GenerateAndReplaceKeysRequestFactory
                .fromParameterMapAsync(extendedParameters);

        final GenerateAndReplaceKeysResponse response = this.smartMeteringConfigurationClient
                .getGenerateAndReplaceKeysResponse(generateAndReplaceKeysAsyncRequest);

        final String expectedResult = responseParameters.get(PlatformKeys.KEY_RESULT);
        assertNotNull("Result", response.getResult());
        assertEquals("Result", expectedResult, response.getResult().name());
    }
}
