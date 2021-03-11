/**
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

import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceByChannelAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DeCoupleMbusDeviceByChannelResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.AbstractSmartMeteringSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.DeCoupleMbusDeviceByChannelRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.SmartMeteringInstallationClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DeCoupleMbusDeviceByChannelSteps extends AbstractSmartMeteringSteps {

    @Autowired
    private SmartMeteringInstallationClient smartMeteringInstallationClient;

    @When("^the DeCouple MBus Device By Channel \"([^\"]*)\" from E-meter \"([^\"]*)\" request is received$")
    public void theDeCoupleMbusDeviceByChannelResponseIsForDevice(final String channel, final String eMeter)
            throws WebServiceSecurityException {

        final DeCoupleMbusDeviceByChannelRequest request = DeCoupleMbusDeviceByChannelRequestFactory
                .forGatewayAndChannel(eMeter, channel);
        final DeCoupleMbusDeviceByChannelAsyncResponse asyncResponse = this.smartMeteringInstallationClient
                .deCoupleMbusDeviceByChannel(request);

        this.checkAndSaveCorrelationId(asyncResponse.getCorrelationUid());
    }

    @Then("^the DeCouple MBus Device By Channel response is \"([^\"]*)\" for device \"([^\"]*)\"$")
    public void theDeCoupleResponseIs(final String status, final String mbusDevice) throws WebServiceSecurityException {

        final DeCoupleMbusDeviceByChannelAsyncRequest request = DeCoupleMbusDeviceByChannelRequestFactory
                .fromScenarioContext();
        final DeCoupleMbusDeviceByChannelResponse response = this.smartMeteringInstallationClient
                .getDeCoupleMbusDeviceByChannelResponse(request);

        assertThat(response.getResult()).as("Result").isNotNull();
        assertThat(response.getResult().name()).as("Result").isEqualTo(status);
        if (mbusDevice.equals("NULL")) {
            assertThat(response.getMbusDeviceIdentification()).as("MbusDeviceIdentification").isNull();
        } else {
            assertThat(response.getMbusDeviceIdentification()).as("MbusDeviceIdentification").isEqualTo(mbusDevice);
        }
    }

    @Then("^retrieving the DeCouple By Channel response results in an exception$")
    public void retrievingTheDeCoupleResponseResultsInAnException() throws WebServiceSecurityException {

        final DeCoupleMbusDeviceByChannelAsyncRequest asyncRequest = DeCoupleMbusDeviceByChannelRequestFactory
                .fromScenarioContext();

        try {
            this.smartMeteringInstallationClient.getDeCoupleMbusDeviceByChannelResponse(asyncRequest);
            Assertions.fail("A SoapFaultClientException should be thrown");
        } catch (final SoapFaultClientException e) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
        }
    }

    @When("^the DeCouple MBus Device By Channel \"([^\"]*)\" from E-meter \"([^\"]*)\" request is received for an unknown gateway$")
    public void theDeCoupleGMeterFromEMeterRequestIsReceivedForAnUnknownDevice(final String channel,
            final String eMeter) throws WebServiceSecurityException {

        final DeCoupleMbusDeviceByChannelRequest request = DeCoupleMbusDeviceByChannelRequestFactory
                .forGatewayAndChannel(eMeter, channel);

        try {
            this.smartMeteringInstallationClient.deCoupleMbusDeviceByChannel(request);
            Assertions.fail("A SoapFaultClientException should be thrown");
        } catch (final SoapFaultClientException e) {
            ScenarioContext.current().put(PlatformKeys.RESPONSE, e);
        }
    }
}
