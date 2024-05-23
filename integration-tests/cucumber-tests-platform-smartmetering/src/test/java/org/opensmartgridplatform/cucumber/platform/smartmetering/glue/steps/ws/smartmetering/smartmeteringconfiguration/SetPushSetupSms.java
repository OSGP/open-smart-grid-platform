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
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsResponse;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetPushSetupSmsRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class SetPushSetupSms extends SetPushSetupSteps {

  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @Autowired
  private SmartMeteringAdHocRequestClient<
          GetSpecificAttributeValueAsyncResponse, GetSpecificAttributeValueRequest>
      adHocRequestclient;

  @Autowired
  private SmartMeteringAdHocResponseClient<
          GetSpecificAttributeValueResponse, GetSpecificAttributeValueAsyncRequest>
      adHocResponseClient;

  @When("^the set PushSetupSms request is received$")
  public void theSetPushSetupSmsRequestIsReceived(final Map<String, String> settings)
      throws Throwable {

    final SetPushSetupSmsRequest setPushSetupSmsRequest =
        SetPushSetupSmsRequestFactory.fromParameterMap(settings);
    final SetPushSetupSmsAsyncResponse setPushSetupSmsAsyncResponse =
        this.smartMeteringConfigurationClient.setPushSetupSms(setPushSetupSmsRequest);

    log.info("Set push setup sms response is received {}", setPushSetupSmsAsyncResponse);
    assertThat(setPushSetupSmsAsyncResponse)
        .as("Set push setup sms response should not be null")
        .isNotNull();

    storeInScenario(settings, setPushSetupSmsAsyncResponse.getCorrelationUid());
  }

  @Then("^the PushSetupSms should be set on the device$")
  public void thePushSetupSmsShouldBeSetOnTheDevice(final Map<String, String> settings)
      throws Throwable {

    final SetPushSetupSmsAsyncRequest setPushSetupSmsAsyncRequest =
        SetPushSetupSmsRequestFactory.fromScenarioContext();
    final SetPushSetupSmsResponse setPushSetupSmsResponse =
        this.smartMeteringConfigurationClient.getSetPushSetupSmsResponse(
            setPushSetupSmsAsyncRequest);

    assertThat(setPushSetupSmsResponse).as("SetPushSetupSmsResponse was null").isNotNull();
    assertThat(setPushSetupSmsResponse.getResult())
        .as("SetPushSetupSmsResponse result was null")
        .isNotNull();
    assertThat(setPushSetupSmsResponse.getResult())
        .as("SetPushSetupSmsResponse should be OK")
        .isEqualTo(OsgpResultType.OK);

    this.assertAttributeSetOnDevice(PushSetupType.SMS, settings);
  }
}
