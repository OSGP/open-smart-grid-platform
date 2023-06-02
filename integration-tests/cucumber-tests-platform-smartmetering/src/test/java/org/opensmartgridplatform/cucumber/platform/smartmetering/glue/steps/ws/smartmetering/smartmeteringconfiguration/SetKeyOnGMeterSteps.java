//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetKeyOnGMeterAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetKeyOnGMeterAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetKeyOnGMeterRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetKeyOnGMeterResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetKeyOnGMeterRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetMbusUserKeyByChannelRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SetKeyOnGMeterSteps {
  protected static final Logger LOGGER = LoggerFactory.getLogger(SetKeyOnGMeterSteps.class);

  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @When("^the set key on GMeter request is received$")
  public void theSetKeyOnGMeterRequestIsReceived(final Map<String, String> requestData)
      throws Throwable {
    final SetKeyOnGMeterRequest SetKeyOnGMeterRequest =
        SetKeyOnGMeterRequestFactory.fromParameterMap(requestData);

    final SetKeyOnGMeterAsyncResponse SetKeyOnGMeterAsyncResponse =
        this.smartMeteringConfigurationClient.SetKeyOnGMeter(SetKeyOnGMeterRequest);

    assertThat(SetKeyOnGMeterAsyncResponse)
        .as("Set Key on GMeter async response should not be null")
        .isNotNull();
    ScenarioContext.current()
        .put(
            PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
            SetKeyOnGMeterAsyncResponse.getCorrelationUid());
  }

  @Then("^the set key on GMeter response should be returned$")
  public void theSetKeyOnGMeterResponseShouldBeReturned(final Map<String, String> settings)
      throws Throwable {
    final SetKeyOnGMeterAsyncRequest SetKeyOnGMeterAsyncRequest =
        SetKeyOnGMeterRequestFactory.fromScenarioContext();
    final SetKeyOnGMeterResponse SetKeyOnGMeterResponse =
        this.smartMeteringConfigurationClient.retrieveSetKeyOnGMeterResponse(
            SetKeyOnGMeterAsyncRequest);

    final String expectedResult = settings.get(PlatformKeys.KEY_RESULT);
    assertThat(SetKeyOnGMeterResponse.getResult())
        .as("Set Key On G-Meter result must not be null")
        .isNotNull();
    assertThat(SetKeyOnGMeterResponse.getResult().name())
        .as("Set Key On G-Meter result")
        .isEqualTo(expectedResult);
  }

  @When("^the set m-bus user key by channel request is received$")
  public void theSetMbusUserKeyByChannelRequestIsReceived(final Map<String, String> requestData)
      throws Throwable {
    final SetMbusUserKeyByChannelRequest setMbusUserKeyByChannelRequest =
        SetMbusUserKeyByChannelRequestFactory.fromParameterMap(requestData);
    final SetMbusUserKeyByChannelAsyncResponse setMbusUserKeyByChannelAsyncResponse =
        this.smartMeteringConfigurationClient.setMbusUserKeyByChannel(
            setMbusUserKeyByChannelRequest);

    assertThat(setMbusUserKeyByChannelAsyncResponse)
        .as("Set M-Bus User Key By Channel async response should not be null")
        .isNotNull();
    ScenarioContext.current()
        .put(
            PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
            setMbusUserKeyByChannelAsyncResponse.getCorrelationUid());
  }

  @Then("^the set m-bus user key by channel response should be returned$")
  public void theSetMbusUserKeyByChannelResponseShouldBeReturned(final Map<String, String> settings)
      throws Throwable {
    final SetMbusUserKeyByChannelAsyncRequest setMbusUserKeyByChannelAsyncRequest =
        SetMbusUserKeyByChannelRequestFactory.fromScenarioContext();
    final SetMbusUserKeyByChannelResponse setMbusUserKeyByChannelResponse =
        this.smartMeteringConfigurationClient.getSetMbusUserKeyByChannelResponse(
            setMbusUserKeyByChannelAsyncRequest);

    final String expectedResult = settings.get(PlatformKeys.KEY_RESULT);
    assertThat(setMbusUserKeyByChannelResponse.getResult())
        .as("Set M-Bus User Key By Channel result must not be null")
        .isNotNull();
    assertThat(setMbusUserKeyByChannelResponse.getResult().name())
        .as("Set M-Bus User Key By Channel result")
        .isEqualTo(expectedResult);
  }
}
