// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetRandomisationSettingsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetRandomisationSettingsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetRandomisationSettingsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetRandomisationSettingsResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetRandomisationSettingsFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;

public class SetRandomisationSettings {

  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @When("the set randomisation settings request is received")
  public void theSetRandomisationSettingsRequestIsReceived(final Map<String, String> parameters)
      throws WebServiceSecurityException {
    final SetRandomisationSettingsRequest request =
        SetRandomisationSettingsFactory.fromParameterMap(parameters);

    final SetRandomisationSettingsAsyncResponse asyncResponse =
        this.smartMeteringConfigurationClient.setRandomisationSettings(request);

    assertThat(asyncResponse).isNotNull();
    ScenarioContext.current()
        .put(PlatformSmartmeteringKeys.KEY_CORRELATION_UID, asyncResponse.getCorrelationUid());
  }

  @Then("the randomisation settings should be set on the device")
  public void theRandomisationSettingsShouldBeSetOnTheDevice() throws Throwable {
    this.theRandomisationSettingsShouldResultIn(OsgpResultType.OK);
  }

  @Then("the randomisation settings should not be set on the device")
  public void theRandomisationSettingsShouldNotBeSetOnTheDevice() throws Throwable {
    this.theRandomisationSettingsShouldResultIn(OsgpResultType.NOT_OK);
  }

  private void theRandomisationSettingsShouldResultIn(final OsgpResultType result)
      throws Throwable {
    final SetRandomisationSettingsAsyncRequest asyncRequest =
        SetRandomisationSettingsFactory.fromScenarioContext();
    final SetRandomisationSettingsResponse response =
        this.smartMeteringConfigurationClient.retrieveSetRandomisationSettingsResponse(
            asyncRequest);

    assertThat(response.getResult()).isEqualTo(result);
  }
}
