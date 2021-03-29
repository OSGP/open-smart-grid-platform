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
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.AbstractSmartMeteringSteps;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.SetSubscriptionInformationRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation.SmartMeteringInstallationClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.assertj.core.api.Assertions.assertThat;

public class SetSubscriptionInformationSteps extends AbstractSmartMeteringSteps {

    @Autowired
    private SmartMeteringInstallationClient smartMeteringInstallationClient;

    @When("^receiving a smartmetering set subscription information request$")
    public void receivingASmartmeteringSetSubscriptionInformationRequest(final Map<String, String> inputSettings)
            throws WebServiceSecurityException {

        final SetSubscriptionInformationRequest request = SetSubscriptionInformationRequestFactory.fromParameters(
                inputSettings);

        final SetSubscriptionInformationAsyncResponse asyncResponse =
                this.smartMeteringInstallationClient.setSubscriptionInformation(
                request);

        this.checkAndSaveCorrelationId(asyncResponse.getCorrelationUid());
    }

    @Then("^the set subscription information response should be returned$")
    public void theSetSubscriptionInformationResponseShouldBeReturned(final Map<String, String> inputSettings)
            throws WebServiceSecurityException {

        final SetSubscriptionInformationAsyncRequest setSubscriptionInformationAsyncRequest =
                SetSubscriptionInformationRequestFactory
                .fromScenarioContext();
        final SetSubscriptionInformationResponse response =
                this.smartMeteringInstallationClient.getSetSubscriptionInformationResponse(
                setSubscriptionInformationAsyncRequest);

        assertThat(response.getResult()).as("Result").isNotNull();
        assertThat(response.getResult().name()).as("Result").isEqualTo(inputSettings.get("Result"));
        assertThat(response.getIpAddress()).isEqualTo(inputSettings.get("IpAddress"));
        assertThat(response.getBtsId()).isEqualTo(Integer.parseInt(inputSettings.get("BtsId")));
        assertThat(response.getCellId()).isEqualTo(Integer.parseInt(inputSettings.get("CellId")));

    }
}
