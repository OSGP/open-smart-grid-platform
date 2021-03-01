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
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceByChannelAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceByChannelResponse;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.AbstractSmartMeteringSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.DeCoupleMbusDeviceByChannelRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.SmartMeteringInstallationClient;
import org.springframework.beans.factory.annotation.Autowired;

public class DeCoupleMbusDeviceByChannelSteps extends AbstractSmartMeteringSteps {

    @Autowired
    private SmartMeteringInstallationClient smartMeteringInstallationClient;

    @When("^the De Couple M-Bus Device By Channel request is received$")
    public void theDeCoupleMBusDeviceByChannelRequestIsReceived(final Map<String, String> settings) throws Throwable {
        final DeCoupleMbusDeviceByChannelRequest request = DeCoupleMbusDeviceByChannelRequestFactory.fromSettings(settings);

        final DeCoupleMbusDeviceByChannelAsyncResponse asyncResponse = this.smartMeteringInstallationClient
                .deCoupleMbusDeviceByChannel(request);

        this.checkAndSaveCorrelationId(asyncResponse.getCorrelationUid());
    }

    @Then("^the De Couple M-Bus Device By Channel response is \"([^\"]*)\"$")
    public void theDeCoupleMBusDeviceByChannelResponseIs(final String status) throws Throwable {
        final DeCoupleMbusDeviceByChannelAsyncRequest asyncRequest = DeCoupleMbusDeviceByChannelRequestFactory
                .fromScenarioContext();

        final DeCoupleMbusDeviceByChannelResponse response = this.smartMeteringInstallationClient
                .getDeCoupleMbusDeviceByChannelResponse(asyncRequest);

        assertThat(response.getResult()).as("Result").isNotNull();
        assertThat(response.getResult().name()).as("Result").isEqualTo(status);
    }
}
