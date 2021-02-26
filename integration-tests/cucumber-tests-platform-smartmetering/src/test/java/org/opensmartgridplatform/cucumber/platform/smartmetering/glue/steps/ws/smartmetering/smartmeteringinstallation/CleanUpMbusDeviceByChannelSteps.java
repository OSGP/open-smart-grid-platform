/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringinstallation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CleanUpMbusDeviceByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CleanUpMbusDeviceByChannelAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CleanUpMbusDeviceByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CleanUpMbusDeviceByChannelResponse;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.AbstractSmartMeteringSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.CleanUpMbusDeviceByChannelRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.SmartMeteringInstallationClient;
import org.springframework.beans.factory.annotation.Autowired;

public class CleanUpMbusDeviceByChannelSteps extends AbstractSmartMeteringSteps {

    @Autowired
    private SmartMeteringInstallationClient smartMeteringInstallationClient;

    @When("^the Clean Up M-Bus Device By Channel request is received$")
    public void theCleanUpMBusDeviceByChannelRequestIsReceived(final Map<String, String> settings) throws Throwable {
        final CleanUpMbusDeviceByChannelRequest request = CleanUpMbusDeviceByChannelRequestFactory.fromSettings(settings);

        final CleanUpMbusDeviceByChannelAsyncResponse asyncResponse = this.smartMeteringInstallationClient
                .cleanUpMbusDeviceByChannel(request);

        this.checkAndSaveCorrelationId(asyncResponse.getCorrelationUid());
    }

    @Then("^the Clean Up M-Bus Device By Channel response is \"([^\"]*)\"$")
    public void theCleanUpMBusDeviceByChannelResponseIs(final String status) throws Throwable {
        final CleanUpMbusDeviceByChannelAsyncRequest asyncRequest = CleanUpMbusDeviceByChannelRequestFactory
                .fromScenarioContext();

        final CleanUpMbusDeviceByChannelResponse response = this.smartMeteringInstallationClient
                .getCleanUpMbusDeviceByChannelResponse(asyncRequest);

        assertThat(response.getResult()).as("Result").isNotNull();
        assertThat(response.getResult().name()).as("Result").isEqualTo(status);
    }
}
