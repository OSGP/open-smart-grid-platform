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

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetEncryptionKeyExchangeOnGMeterRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetMbusUserKeyByChannelRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SetEncryptionKeyExchangeOnGMeterSteps {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SetEncryptionKeyExchangeOnGMeterSteps.class);

    @Autowired
    private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

    @When("^the exchange user key request is received$")
    public void theExchangeUserKeyRequestIsReceived(final Map<String, String> requestData) throws Throwable {
        final SetEncryptionKeyExchangeOnGMeterRequest setEncryptionKeyExchangeOnGMeterRequest = SetEncryptionKeyExchangeOnGMeterRequestFactory
                .fromParameterMap(requestData);

        final SetEncryptionKeyExchangeOnGMeterAsyncResponse setEncryptionKeyExchangeOnGMeterAsyncResponse = this.smartMeteringConfigurationClient
                .setEncryptionKeyExchangeOnGMeter(setEncryptionKeyExchangeOnGMeterRequest);

        assertNotNull("Set encryptionKey exchange on GMeter async response should not be null",
                setEncryptionKeyExchangeOnGMeterAsyncResponse);
        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
                setEncryptionKeyExchangeOnGMeterAsyncResponse.getCorrelationUid());
    }

    @Then("^the exchange user key response should be returned$")
    public void theExchangeUserKeyResponseShouldBeReturned(final Map<String, String> settings) throws Throwable {
        final SetEncryptionKeyExchangeOnGMeterAsyncRequest setEncryptionKeyExchangeOnGMeterAsyncRequest = SetEncryptionKeyExchangeOnGMeterRequestFactory
                .fromScenarioContext();
        final SetEncryptionKeyExchangeOnGMeterResponse setEncryptionKeyExchangeOnGMeterResponse = this.smartMeteringConfigurationClient
                .retrieveSetEncryptionKeyExchangeOnGMeterResponse(setEncryptionKeyExchangeOnGMeterAsyncRequest);

        final String expectedResult = settings.get(PlatformKeys.KEY_RESULT);
        assertNotNull("Set Encryption Key Exchange On G-Meter result must not be null",
                setEncryptionKeyExchangeOnGMeterResponse.getResult());
        assertEquals("Set Encryption Key Exchange On G-Meter result", expectedResult,
                setEncryptionKeyExchangeOnGMeterResponse.getResult().name());
    }

    @When("^the set m-bus user key by channel request is received$")
    public void theSetMbusUserKeyByChannelRequestIsReceived(final Map<String, String> requestData) throws Throwable {
        final SetMbusUserKeyByChannelRequest setMbusUserKeyByChannelRequest = SetMbusUserKeyByChannelRequestFactory
                .fromParameterMap(requestData);
        final SetMbusUserKeyByChannelAsyncResponse setMbusUserKeyByChannelAsyncResponse = this.smartMeteringConfigurationClient
                .setMbusUserKeyByChannel(setMbusUserKeyByChannelRequest);

        assertNotNull("Set M-Bus User Key By Channel async response should not be null",
                setMbusUserKeyByChannelAsyncResponse);
        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
                setMbusUserKeyByChannelAsyncResponse.getCorrelationUid());
    }

    @Then("^the set m-bus user key by channel response should be returned$")
    public void theSetMbusUserKeyByChannelResponseShouldBeReturned(final Map<String, String> settings)
            throws Throwable {
        final SetMbusUserKeyByChannelAsyncRequest setMbusUserKeyByChannelAsyncRequest = SetMbusUserKeyByChannelRequestFactory
                .fromScenarioContext();
        final SetMbusUserKeyByChannelResponse setMbusUserKeyByChannelResponse = this.smartMeteringConfigurationClient
                .getSetMbusUserKeyByChannelResponse(setMbusUserKeyByChannelAsyncRequest);

        final String expectedResult = settings.get(PlatformKeys.KEY_RESULT);
        assertNotNull("Set M-Bus User Key By Channel result must not be null",
                setMbusUserKeyByChannelResponse.getResult());
        assertEquals("Set M-Bus User Key By Channel result", expectedResult,
                setMbusUserKeyByChannelResponse.getResult().name());
    }
}
