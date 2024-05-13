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
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupAlarmResponse;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetPushSetupAlarmRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class SetPushSetupAlarmSteps extends SetPushSetupSteps {

  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @Autowired
  private SmartMeteringAdHocRequestClient<
          GetSpecificAttributeValueAsyncResponse, GetSpecificAttributeValueRequest>
      adHocRequestclient;

  @Autowired
  private SmartMeteringAdHocResponseClient<
          GetSpecificAttributeValueResponse, GetSpecificAttributeValueAsyncRequest>
      adHocResponseClient;

  @When("^the set PushSetupAlarm request is received$")
  public void theSetPushSetupAlarmRequestIsReceived(final Map<String, String> settings)
      throws Throwable {

    final SetPushSetupAlarmRequest setPushSetupAlarmRequest =
        SetPushSetupAlarmRequestFactory.fromParameterMap(settings);
    final SetPushSetupAlarmAsyncResponse setPushSetupAlarmAsyncResponse =
        this.smartMeteringConfigurationClient.setPushSetupAlarm(setPushSetupAlarmRequest);

    log.info("Set push setup alarm response is received {}", setPushSetupAlarmAsyncResponse);
    assertThat(setPushSetupAlarmAsyncResponse)
        .as("Set push setup alarm response should not be null")
        .isNotNull();

    storeInScenario(settings, setPushSetupAlarmAsyncResponse.getCorrelationUid());
  }

  @Then("^the PushSetupAlarm response should be returned$")
  public void thePushSetupAlarmResponseIs(final Map<String, String> settings) throws Throwable {
    final SetPushSetupAlarmAsyncRequest setPushSetupAlarmAsyncRequest =
        SetPushSetupAlarmRequestFactory.fromScenarioContext();
    final SetPushSetupAlarmResponse setPushSetupAlarmResponse =
        this.smartMeteringConfigurationClient.getSetPushSetupAlarmResponse(
            setPushSetupAlarmAsyncRequest);

    final String expectedResult = settings.get(PlatformKeys.KEY_RESULT);

    assertThat(setPushSetupAlarmResponse).isNotNull();
    assertThat(setPushSetupAlarmResponse.getResult()).isNotNull();
    assertThat(setPushSetupAlarmResponse.getResult())
        .isEqualTo(OsgpResultType.valueOf(expectedResult));
  }

  @Then("^the PushSetupAlarm should be set on the device$")
  public void thePushSetupAlarmShouldBeSetOnTheDevice(final Map<String, String> settings)
      throws Throwable {

    final SetPushSetupAlarmAsyncRequest setPushSetupAlarmAsyncRequest =
        SetPushSetupAlarmRequestFactory.fromScenarioContext();
    final SetPushSetupAlarmResponse setPushSetupAlarmResponse =
        this.smartMeteringConfigurationClient.getSetPushSetupAlarmResponse(
            setPushSetupAlarmAsyncRequest);

    assertThat(setPushSetupAlarmResponse).as("SetPushSetupAlarmResponse was null").isNotNull();
    assertThat(setPushSetupAlarmResponse.getResult())
        .as("SetPushSetupAlarmResponse result was null")
        .isNotNull();
    assertThat(setPushSetupAlarmResponse.getResult())
        .as("SetPushSetupAlarmResponse should be OK")
        .isEqualTo(OsgpResultType.OK);

    this.assertAttributeSetOnDevice(PushSetupType.ALARM, settings);
  }
}
