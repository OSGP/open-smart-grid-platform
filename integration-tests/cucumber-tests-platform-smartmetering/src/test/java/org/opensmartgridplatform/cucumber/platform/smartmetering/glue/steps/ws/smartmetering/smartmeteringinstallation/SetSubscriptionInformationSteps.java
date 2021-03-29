/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringinstallation;

import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.SetSubscriptionInformationAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.SetSubscriptionInformationAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.SetSubscriptionInformationRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.SetSubscriptionInformationResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.device.DlmsDeviceSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.AbstractSmartMeteringSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.SetSubscriptionInformationRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.SmartMeteringInstallationClient;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.assertj.core.api.Assertions.assertThat;

public class SetSubscriptionInformationSteps extends AbstractSmartMeteringSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetSubscriptionInformationSteps.class);

    @Autowired
    private SmartMeteringInstallationClient smartMeteringInstallationClient;

    @Autowired
    private DlmsDeviceSteps dlmsDeviceSteps;

    @Given("^a dlms device$")
    public void aDlmsDevice(final Map<String, String> inputSettings) {
        final Device device = this.dlmsDeviceSteps.createDeviceInCoreDatabase(inputSettings);

        LOGGER.info("Created new Device for SetSubscriptionInformationSteps {}", device.getDeviceIdentification());

        this.dlmsDeviceSteps.setScenarioContextForDevice(inputSettings, device);

        this.dlmsDeviceSteps.createDeviceAuthorisationInCoreDatabase(device);
    }

    @When("^receiving a smartmetering set subscription information request$")
    public void receivingASmartmeteringSetSubscriptionInformationRequest(final Map<String, String> requestSettings)
            throws WebServiceSecurityException {

        final SetSubscriptionInformationRequest request = SetSubscriptionInformationRequestFactory.fromParameters(
                requestSettings);

        final SetSubscriptionInformationAsyncResponse asyncResponse =
                this.smartMeteringInstallationClient.setSubscriptionInformation(
                request);

        this.checkAndSaveCorrelationId(asyncResponse.getCorrelationUid());
    }

    @Then("^the set subscription information response should be returned$")
    public void theSetSubscriptionInformationResponseShouldBeReturned(final Map<String, String> responseSettings)
            throws WebServiceSecurityException {

        final String correlationUid = (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
        final Map<String, String> extendedParameters = SettingsHelper.addDefault(responseSettings,
                PlatformKeys.KEY_CORRELATION_UID, correlationUid);

        final SetSubscriptionInformationAsyncRequest setSubscriptionInformationAsyncRequest =
                SetSubscriptionInformationRequestFactory
                .fromScenarioContext(extendedParameters);
        final SetSubscriptionInformationResponse response =
                this.smartMeteringInstallationClient.getSetSubscriptionInformationResponse(
                setSubscriptionInformationAsyncRequest);

        assertThat(response.getResult()).as("Result").isNotNull();
        assertThat(response.getResult().name()).as("Result").isEqualTo(responseSettings.get("Result"));
        assertThat(response.getIpAddress()).isEqualTo(responseSettings.get("IpAddress"));
        assertThat(response.getBtsId()).isEqualTo(Integer.parseInt(responseSettings.get("BtsId")));
        assertThat(response.getCellId()).isEqualTo(Integer.parseInt(responseSettings.get("CellId")));

    }
}
