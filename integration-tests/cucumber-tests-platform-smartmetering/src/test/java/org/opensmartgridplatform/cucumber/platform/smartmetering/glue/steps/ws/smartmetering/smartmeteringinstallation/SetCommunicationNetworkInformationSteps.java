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

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.SetCommunicationNetworkInformationAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.SetCommunicationNetworkInformationAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.SetCommunicationNetworkInformationRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.SetCommunicationNetworkInformationResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.helpers.SettingsHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.AbstractSmartMeteringSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.SetCommunicationNetworkInformationRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.SmartMeteringInstallationClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.assertj.core.api.Assertions.assertThat;

public class SetCommunicationNetworkInformationSteps extends AbstractSmartMeteringSteps {

    @Autowired
    private SmartMeteringInstallationClient smartMeteringInstallationClient;

    @When("^receiving a smartmetering set communication network information request$")
    public void receivingASmartmeteringSetCommunicationNetworkInformationRequest(
            final Map<String, String> requestSettings) throws WebServiceSecurityException {

        final SetCommunicationNetworkInformationRequest request =
                SetCommunicationNetworkInformationRequestFactory.fromParameters(
                requestSettings);

        final SetCommunicationNetworkInformationAsyncResponse asyncResponse =
                this.smartMeteringInstallationClient.setCommunicationNetworkInformation(
                request);

        this.checkAndSaveCorrelationId(asyncResponse.getCorrelationUid());
    }

    @Then("^the set communication network information response should be returned$")
    public void theSetCommunicationNetworkInformationResponseShouldBeReturned(
            final Map<String, String> responseSettings) throws WebServiceSecurityException {

        final String correlationUid = (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
        final Map<String, String> extendedParameters = SettingsHelper.addDefault(responseSettings,
                PlatformKeys.KEY_CORRELATION_UID, correlationUid);

        final SetCommunicationNetworkInformationAsyncRequest setCommunicationNetworkInformationAsyncRequest =
                SetCommunicationNetworkInformationRequestFactory
                .fromScenarioContext(extendedParameters);
        final SetCommunicationNetworkInformationResponse response =
                this.smartMeteringInstallationClient.getSetCommunicationNetworkInformationResponse(
                setCommunicationNetworkInformationAsyncRequest);

        assertThat(response.getResult()).as(PlatformKeys.KEY_RESULT).isNotNull();
        assertThat(response.getResult().name()).as(PlatformKeys.KEY_RESULT)
                .isEqualTo(responseSettings.get(PlatformKeys.KEY_RESULT));
        assertThat(response.getIpAddress()).isEqualTo(responseSettings.get(PlatformKeys.KEY_IP_ADDRESS));
        assertThat(response.getBtsId()).isEqualTo(Integer.parseInt(responseSettings.get(PlatformKeys.KEY_BTS_ID)));
        assertThat(response.getCellId()).isEqualTo(Integer.parseInt(responseSettings.get(PlatformKeys.KEY_CELL_ID)));

    }
}
