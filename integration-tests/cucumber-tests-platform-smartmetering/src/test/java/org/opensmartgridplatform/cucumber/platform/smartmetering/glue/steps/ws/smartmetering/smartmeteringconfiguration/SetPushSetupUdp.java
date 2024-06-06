/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.GetSpecificAttributeValueResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupUdpAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupUdpAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupUdpRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupUdpResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration.SetPushSetupSteps.PushSetupType;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetPushSetupUdpRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SetPushSetupUdp extends SetPushSetupSteps {

  private static final Logger LOGGER = LoggerFactory.getLogger(SetPushSetupUdp.class);

  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @Autowired
  private SmartMeteringAdHocRequestClient<
          GetSpecificAttributeValueAsyncResponse, GetSpecificAttributeValueRequest>
      adHocRequestclient;

  @Autowired
  private SmartMeteringAdHocResponseClient<
          GetSpecificAttributeValueResponse, GetSpecificAttributeValueAsyncRequest>
      adHocResponseClient;

  @When("^the set PushSetupUdp request is received$")
  public void theSetPushSetupUdpRequestIsReceived(final Map<String, String> settings)
      throws Throwable {

    final SetPushSetupUdpRequest setPushSetupUdpRequest =
        SetPushSetupUdpRequestFactory.fromParameterMap(settings);
    final SetPushSetupUdpAsyncResponse setPushSetupUdpAsyncResponse =
        this.smartMeteringConfigurationClient.setPushSetupUdp(setPushSetupUdpRequest);

    LOGGER.info("Set push setup Udp response is received {}", setPushSetupUdpAsyncResponse);
    assertThat(setPushSetupUdpAsyncResponse)
        .as("Set push setup Udp response should not be null")
        .isNotNull();

    ScenarioContext.current()
        .put(PlatformKeys.KEY_CORRELATION_UID, setPushSetupUdpAsyncResponse.getCorrelationUid());
  }

  @Then("^the PushSetupUdp response should be returned$")
  public void thePushSetupUdpResponseIs(final Map<String, String> settings) throws Throwable {
    final SetPushSetupUdpAsyncRequest setPushSetupUdpAsyncRequest =
        SetPushSetupUdpRequestFactory.fromScenarioContext();
    final SetPushSetupUdpResponse setPushSetupUdpResponse =
        this.smartMeteringConfigurationClient.getSetPushSetupUdpResponse(
            setPushSetupUdpAsyncRequest);

    final String expectedResult = settings.get(PlatformKeys.KEY_RESULT);

    assertThat(setPushSetupUdpResponse).isNotNull();
    assertThat(setPushSetupUdpResponse.getResult()).isNotNull();
    assertThat(setPushSetupUdpResponse.getResult())
        .isEqualTo(OsgpResultType.valueOf(expectedResult));
  }

  @Then("^the PushSetupUdp should be set on the device$")
  public void thePushSetupUdpShouldBeSetOnTheDevice(final Map<String, String> settings)
      throws Throwable {
    this.assertAttributeSetOnDevice(PushSetupType.UDP, settings);
  }
}
