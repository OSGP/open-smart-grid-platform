// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetThdConfigurationAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetThdConfigurationAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetThdConfigurationRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetThdConfigurationResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocRequestClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc.SmartMeteringAdHocResponseClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetThdConfigurationRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SetThdConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(SetThdConfiguration.class);

  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @Autowired
  private SmartMeteringAdHocRequestClient<
          SetThdConfigurationAsyncResponse, SetThdConfigurationRequest>
      requestclient;

  @Autowired
  private SmartMeteringAdHocResponseClient<
          SetThdConfigurationResponse, SetThdConfigurationAsyncRequest>
      responseClient;

  @When("^the set ThdConfiguration request is received$")
  public void theSetThdConfigurationRequestIsReceived(final Map<String, String> settings)
      throws Throwable {

    final SetThdConfigurationRequest setThdConfigurationRequest =
        SetThdConfigurationRequestFactory.fromParameterMap(settings);
    final SetThdConfigurationAsyncResponse setThdConfigurationAsyncResponse =
        this.smartMeteringConfigurationClient.setThdConfiguration(setThdConfigurationRequest);

    LOGGER.info("Set THD configuration response is received {}", setThdConfigurationAsyncResponse);
    assertThat(setThdConfigurationAsyncResponse)
        .as("Set THD configuration response should not be null")
        .isNotNull();

    ScenarioContext.current()
        .put(
            PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
            setThdConfigurationAsyncResponse.getCorrelationUid());
    ScenarioContext.current()
        .put(PlatformSmartmeteringKeys.HOSTNAME, settings.get(PlatformSmartmeteringKeys.HOSTNAME));
    ScenarioContext.current()
        .put(PlatformSmartmeteringKeys.PORT, settings.get(PlatformSmartmeteringKeys.PORT));
  }

  @Then("^the ThdConfiguration should be set on the device$")
  public void theThdConfigurationShouldBeSetOnTheDevice(final Map<String, String> settings)
      throws Throwable {
    this.theThdConfigurationShouldResultIn(settings, true);
  }

  @Then("^the ThdConfiguration should not be set on the device$")
  public void theThdConfigurationShouldNotBeSetOnTheDevice(final Map<String, String> settings)
      throws Throwable {
    this.theThdConfigurationShouldResultIn(settings, false);
  }

  private void theThdConfigurationShouldResultIn(
      final Map<String, String> settings, final boolean succes) throws Throwable {

    final SetThdConfigurationAsyncRequest setThdConfigurationAsyncRequest =
        SetThdConfigurationRequestFactory.fromScenarioContext();
    final SetThdConfigurationResponse setThdConfigurationResponse =
        this.smartMeteringConfigurationClient.getSetThdConfigurationResponse(
            setThdConfigurationAsyncRequest);

    assertThat(setThdConfigurationResponse).as("SetThdConfigurationResponse was null").isNotNull();
    assertThat(setThdConfigurationResponse.getResult())
        .as("SetThdConfigurationResponse result was null")
        .isNotNull();

    assertThat(setThdConfigurationResponse.getResult())
        .as("SetThdConfigurationResponse should be OK")
        .isEqualTo(succes ? OsgpResultType.OK : OsgpResultType.NOT_OK);
  }
}
