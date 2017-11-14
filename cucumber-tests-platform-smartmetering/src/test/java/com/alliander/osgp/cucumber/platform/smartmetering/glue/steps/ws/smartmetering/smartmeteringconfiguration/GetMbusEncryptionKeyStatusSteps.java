/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.GetMbusEncryptionKeyStatusRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GetMbusEncryptionKeyStatusSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetMbusEncryptionKeyStatusSteps.class);
    private static final String OPERATION = "Get M-Bus encryption key status";

    @Autowired
    private SmartMeteringConfigurationClient smartMeterConfigurationClient;

    @When("^a get M-Bus encryption key status request is received$")
    public void aGetMbusEncryptionKeyStatusRequestIsReceived(final Map<String, String> requestData)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final GetMbusEncryptionKeyStatusRequest request = GetMbusEncryptionKeyStatusRequestFactory
                .fromParameterMap(requestData);
        final GetMbusEncryptionKeyStatusAsyncResponse asyncResponse = this.smartMeterConfigurationClient
                .getMbusEncryptionKeyStatus(request);

        assertNotNull(OPERATION + ": Async response should not be null", asyncResponse);
        LOGGER.info(OPERATION + ": Async response is received {}", asyncResponse);

        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION,
                asyncResponse.getDeviceIdentification());
    }

    @Then("^the get M-Bus encryption key status request should return an encryption key status$")
    public void theMbusEncryptionKeyStatusShouldBeReturned()
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final GetMbusEncryptionKeyStatusAsyncRequest asyncRequest = GetMbusEncryptionKeyStatusRequestFactory
                .fromScenarioContext();
        final GetMbusEncryptionKeyStatusResponse response = this.smartMeterConfigurationClient
                .retrieveGetMbusEncryptionKeyStatusResponse(asyncRequest);

        assertNotNull(OPERATION + ": Result should not be null", response.getResult());
        assertEquals(OPERATION + ": Result should be OK", OsgpResultType.OK, response.getResult());
        assertNotNull(OPERATION + ": Encryption key status should not be null", response.getEncryptionKeyStatus());
    }

    @Then("^the get M-Bus encryption key status request should return an exception$")
    public void theGetMbusEncryptionKeyStatusRequestShouldReturnAnException()
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final GetMbusEncryptionKeyStatusAsyncRequest asyncRequest = GetMbusEncryptionKeyStatusRequestFactory
                .fromScenarioContext();
        try {
            this.smartMeterConfigurationClient.retrieveGetMbusEncryptionKeyStatusResponse(asyncRequest);
            fail("A SoapFaultClientException should be thrown.");
        } catch (final SoapFaultClientException e) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
        }

    }
}
