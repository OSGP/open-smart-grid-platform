// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupLastGaspAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupLastGaspAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupLastGaspRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupLastGaspResponse;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration.SetPushSetupSteps.PushSetupType;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetPushSetupLastGaspRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class SetPushSetupLastGaspSteps extends SetPushSetupSteps {

  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @Autowired
  private SmartMeteringAdHocRequestClient<
          GetSpecificAttributeValueAsyncResponse, GetSpecificAttributeValueRequest>
      adHocRequestclient;

  @Autowired
  private SmartMeteringAdHocResponseClient<
          GetSpecificAttributeValueResponse, GetSpecificAttributeValueAsyncRequest>
      adHocResponseClient;

  @When("^the set PushSetupLastGasp request is received$")
  public void theSetPushSetupLastGaspRequestIsReceived(final Map<String, String> settings)
      throws Throwable {

    final SetPushSetupLastGaspRequest setPushSetupLastGaspRequest =
        SetPushSetupLastGaspRequestFactory.fromParameterMap(settings);
    final SetPushSetupLastGaspAsyncResponse setPushSetupLastGaspAsyncResponse =
        this.smartMeteringConfigurationClient.setPushSetupLastGasp(setPushSetupLastGaspRequest);

    log.info("Set push setup LastGasp response is received {}", setPushSetupLastGaspAsyncResponse);
    assertThat(setPushSetupLastGaspAsyncResponse)
        .as("Set push setup LastGasp response should not be null")
        .isNotNull();

    storeInScenario(settings, setPushSetupLastGaspAsyncResponse.getCorrelationUid());
  }

  @Then("^the PushSetupLastGasp response should be returned$")
  public void thePushSetupLastGaspResponseIs(final Map<String, String> settings) throws Throwable {
    final SetPushSetupLastGaspAsyncRequest setPushSetupLastGaspAsyncRequest =
        SetPushSetupLastGaspRequestFactory.fromScenarioContext();
    final SetPushSetupLastGaspResponse setPushSetupLastGaspResponse =
        this.smartMeteringConfigurationClient.getSetPushSetupLastGaspResponse(
            setPushSetupLastGaspAsyncRequest);

    final String expectedResult = settings.get(PlatformKeys.KEY_RESULT);

    assertThat(setPushSetupLastGaspResponse).isNotNull();
    assertThat(setPushSetupLastGaspResponse.getResult()).isNotNull();
    assertThat(setPushSetupLastGaspResponse.getResult())
        .isEqualTo(OsgpResultType.valueOf(expectedResult));
  }

  @Then("^the PushSetupLastGasp should be set on the device$")
  public void thePushSetupLastGaspShouldBeSetOnTheDevice(final Map<String, String> settings)
      throws Throwable {

    final SetPushSetupLastGaspAsyncRequest setPushSetupLastGaspAsyncRequest =
        SetPushSetupLastGaspRequestFactory.fromScenarioContext();
    final SetPushSetupLastGaspResponse setPushSetupLastGaspResponse =
        this.smartMeteringConfigurationClient.getSetPushSetupLastGaspResponse(
            setPushSetupLastGaspAsyncRequest);
    assertThat(setPushSetupLastGaspResponse)
        .as("SetPushSetupLastGaspResponse was null")
        .isNotNull();
    assertThat(setPushSetupLastGaspResponse.getResult())
        .as("SetPushSetupLastGaspResponse result was null")
        .isNotNull();
    assertThat(setPushSetupLastGaspResponse.getResult())
        .as("SetPushSetupLastGaspResponse should be OK")
        .isEqualTo(OsgpResultType.OK);
    this.assertAttributeSetOnDevice(PushSetupType.LAST_GASP, settings);
  }
}
