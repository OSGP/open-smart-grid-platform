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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.OsgpResultType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetMBusEncryptionKeyStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetMBusEncryptionKeyStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetMBusEncryptionKeyStatusRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetMBusEncryptionKeyStatusResponse;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.GetMBusEncryptionKeyStatusRequestFactory;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class GetMBusEncryptionKeyStatusSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetMBusEncryptionKeyStatusSteps.class);
    private static final String OPERATION = "Get M-Bus encryption key status";

    @Autowired
    private SmartMeteringConfigurationClient smartMeterConfigurationClient;

    @When("^a get M-Bus encryption key status request is received$")
    public void aGetMBusEncryptionKeyStatusRequestIsReceived(final Map<String, String> requestData)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final GetMBusEncryptionKeyStatusRequest request = GetMBusEncryptionKeyStatusRequestFactory
                .fromParameterMap(requestData);
        final GetMBusEncryptionKeyStatusAsyncResponse asyncResponse = this.smartMeterConfigurationClient
                .getMBusEncryptionKeyStatus(request);

        assertNotNull(OPERATION + ": Async response should not be null", asyncResponse);
        LOGGER.info(OPERATION + ": Async response is received {}", asyncResponse);

        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
    }

    @Then("^the M-Bus encryption key status should be returned$")
    public void theMBusEncryptionKeyStatusShouldBeReturned(final Map<String, String> responseData)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final GetMBusEncryptionKeyStatusAsyncRequest asyncRequest = GetMBusEncryptionKeyStatusRequestFactory
                .fromScenarioContext();
        final GetMBusEncryptionKeyStatusResponse response = this.smartMeterConfigurationClient
                .retrieveGetMBusEncryptionKeyStatusResponse(asyncRequest);

        assertNotNull(OPERATION + ": Result should not be null", response.getResult());
        assertEquals(OPERATION + ": Result should be OK", OsgpResultType.OK, response.getResult());
        assertNotNull(OPERATION + ": Encryption key status should not be null", response.getEncryptionKeyStatus());
    }
}
