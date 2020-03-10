/**
/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetAdministrativeStatusResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetAdministrativeStatusRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SetAdministrativeStatus {
    protected static final Logger LOGGER = LoggerFactory.getLogger(SetAdministrativeStatus.class);

    @Autowired
    private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

    @When("^the set administrative status request is received$")
    public void theSetAdministrativeStatusRequestIsReceived(final Map<String, String> requestData) throws Throwable {
        final SetAdministrativeStatusRequest setAdministrativeStatusRequest = SetAdministrativeStatusRequestFactory
                .fromParameterMap(requestData);

        final SetAdministrativeStatusAsyncResponse setAdministrativeStatusAsyncResponse = this.smartMeteringConfigurationClient
                .setAdministrativeStatus(setAdministrativeStatusRequest);

        LOGGER.info("Set administrative status response is received {}", setAdministrativeStatusAsyncResponse);

        assertThat(setAdministrativeStatusAsyncResponse).as("Set administrative status response should not be null")
                .isNotNull();
        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
                setAdministrativeStatusAsyncResponse.getCorrelationUid());
    }

    @Then("^the administrative status should be set on the device$")
    public void theAdministrativeStatusShouldBeSetOnTheDevice(final Map<String, String> settings) throws Throwable {
        final SetAdministrativeStatusAsyncRequest setAdministrativeStatusAsyncRequest = SetAdministrativeStatusRequestFactory
                .fromScenarioContext();
        final SetAdministrativeStatusResponse setAdministrativeStatusResponse = this.smartMeteringConfigurationClient
                .retrieveSetAdministrativeStatusResponse(setAdministrativeStatusAsyncRequest);

        LOGGER.info("The administrative status result is: {}", setAdministrativeStatusResponse.getResult());
        assertThat(setAdministrativeStatusResponse.getResult()).as("Administrative status type result is null")
                .isNotNull();
        assertThat(setAdministrativeStatusResponse.getResult()).as("Administrative status type should be OK")
                .isEqualTo(OsgpResultType.OK);
    }
}
