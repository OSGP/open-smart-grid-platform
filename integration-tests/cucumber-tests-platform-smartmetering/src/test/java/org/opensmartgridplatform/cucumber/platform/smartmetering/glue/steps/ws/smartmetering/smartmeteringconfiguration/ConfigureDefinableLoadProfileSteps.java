//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ConfigureDefinableLoadProfileResponse;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.ScenarioContextHelper;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.ConfigureDefinableLoadProfileRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfigureDefinableLoadProfileSteps {

  @Autowired private SmartMeteringConfigurationClient client;

  @When("^a Configure Definable Load Profile request is received$")
  public void aConfigureDefinableLoadProfileRequestIsReceived(final Map<String, String> settings)
      throws Throwable {
    final ConfigureDefinableLoadProfileRequest request =
        ConfigureDefinableLoadProfileRequestFactory.fromParameterMap(settings);
    final ConfigureDefinableLoadProfileAsyncResponse asyncResponse =
        this.client.configureDefinableLoadProfile(request);

    assertThat(asyncResponse).isNotNull();
    ScenarioContextHelper.saveAsyncResponse(asyncResponse);
  }

  @Then("^the Configure Definable Load Profile response should be returned$")
  public void theConfigureDefinableLoadProfileResponseShouldBeReturned(
      final Map<String, String> settings) throws Throwable {

    final ConfigureDefinableLoadProfileAsyncRequest asyncRequest =
        ConfigureDefinableLoadProfileRequestFactory.fromParameterMapAsync(settings);

    final ConfigureDefinableLoadProfileResponse response =
        this.client.getConfigureDefinableLoadProfileResponse(asyncRequest);

    final String expectedResult = settings.get(PlatformKeys.KEY_RESULT);
    assertThat(response.getResult()).as("Result").isNotNull();
    assertThat(response.getResult().name()).as("Result").isEqualTo(expectedResult);
  }
}
