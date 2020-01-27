/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.FirmwareVersion;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.core.DeviceFirmwareModuleSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.FirmwareVersionRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.GetFirmwareVersionRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.opensmartgridplatform.domain.core.entities.FirmwareModule;
import org.opensmartgridplatform.domain.core.repositories.FirmwareModuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class GetFirmwareVersion {
    protected static final Logger LOGGER = LoggerFactory.getLogger(GetFirmwareVersion.class);

    @Autowired
    private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

    @Autowired
    private FirmwareModuleRepository firmwareModuleRepository;

    @Autowired
    private DeviceFirmwareModuleSteps deviceFirmwareModuleSteps;

    @When("^the get firmware version request is received$")
    public void theGetFirmwareVersionRequestIsReceived(final Map<String, String> requestData) throws Throwable {
        final GetFirmwareVersionRequest getFirmwareVersionRequest = GetFirmwareVersionRequestFactory
                .fromParameterMap(requestData);

        final GetFirmwareVersionAsyncResponse getFirmwareVersionAsyncResponse = this.smartMeteringConfigurationClient
                .getFirmwareVersion(getFirmwareVersionRequest);

        assertThat(getFirmwareVersionAsyncResponse).as("Get firmware version asyncResponse should not be null")
                .isNotNull();
        LOGGER.info("Get firmware version asyncResponse is received {}", getFirmwareVersionAsyncResponse);

        ScenarioContext.current().put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
                getFirmwareVersionAsyncResponse.getCorrelationUid());
    }

    @Then("^the firmware version result should be returned$")
    public void theFirmwareVersionResultShouldBeReturned(final Map<String, String> settings) throws Throwable {
        final GetFirmwareVersionAsyncRequest getFirmwareVersionAsyncRequest = FirmwareVersionRequestFactory
                .fromScenarioContext();
        final GetFirmwareVersionResponse getFirmwareVersionResponse = this.smartMeteringConfigurationClient
                .retrieveGetFirmwareVersionResponse(getFirmwareVersionAsyncRequest);

        assertThat(getFirmwareVersionResponse.getResult()).as("Get firmware version response has result null")
                .isNotNull();
        assertThat(getFirmwareVersionResponse.getResult()).as("Response should be OK").isEqualTo(OsgpResultType.OK);

        final List<FirmwareVersion> firmwareVersions = getFirmwareVersionResponse.getFirmwareVersion();

        this.checkFirmwareVersionResult(settings, firmwareVersions);
    }

    public void checkFirmwareVersionResult(final Map<String, String> settings,
            final List<FirmwareVersion> firmwareVersions) {

        final Map<FirmwareModule, String> expectedVersionsByModule = this.deviceFirmwareModuleSteps
                .getFirmwareModuleVersions(settings, true);

        assertThat(firmwareVersions.size()).as("Number of firmware modules").isEqualTo(expectedVersionsByModule.size());

        for (final FirmwareVersion receivedFirmwareVersion : firmwareVersions) {
            assertThat(receivedFirmwareVersion.getFirmwareModuleType()).as("The received firmware module type is null")
                    .isNotNull();

            assertThat(receivedFirmwareVersion.getVersion()).as("The received firmware version is null").isNotNull();
            final String moduleDescription = receivedFirmwareVersion.getFirmwareModuleType().name();
            final String moduleVersion = receivedFirmwareVersion.getVersion();

            final FirmwareModule firmwareModule = this.firmwareModuleRepository
                    .findByDescriptionIgnoreCase(moduleDescription);
            assertThat(firmwareModule).as("Received version \"" + moduleVersion + "\" for unknown firmware module \""
                    + moduleDescription + "\"").isNotNull();

            final String expectedVersion = expectedVersionsByModule.get(firmwareModule);
            assertThat(expectedVersion).as("Received version \"" + moduleVersion + "\" for firmware module \""
                    + moduleDescription + "\" which was not expected").isNotNull();
            assertThat(moduleVersion).as("Version for firmware module \"" + moduleDescription + "\"")
                    .isEqualTo(expectedVersion);
        }
    }

}
