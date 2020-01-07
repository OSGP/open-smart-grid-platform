/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetMbusEncryptionKeyStatusResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.GetMbusEncryptionKeyStatusRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

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

        assertThat(asyncResponse).as(OPERATION + ": Async response should not be null").isNotNull();
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

        assertThat(response.getResult()).as(OPERATION + ": Result should not be null").isNotNull();
        assertThat(response.getResult()).as(OPERATION + ": Result should be OK").isEqualTo(OsgpResultType.OK);
        assertThat(response.getEncryptionKeyStatus()).as(OPERATION + ": Encryption key status should not be null")
                .isNotNull();
    }

    @Then("^the get M-Bus encryption key status request should return an exception$")
    public void theGetMbusEncryptionKeyStatusRequestShouldReturnAnException()
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        final GetMbusEncryptionKeyStatusAsyncRequest asyncRequest = GetMbusEncryptionKeyStatusRequestFactory
                .fromScenarioContext();
        try {
            this.smartMeterConfigurationClient.retrieveGetMbusEncryptionKeyStatusResponse(asyncRequest);
            Assertions.fail("A SoapFaultClientException should be thrown.");
        } catch (final SoapFaultClientException e) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
        }

    }
}
