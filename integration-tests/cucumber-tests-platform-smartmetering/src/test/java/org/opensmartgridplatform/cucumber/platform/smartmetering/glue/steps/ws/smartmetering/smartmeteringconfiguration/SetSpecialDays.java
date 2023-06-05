// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetSpecialDaysRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SetSpecialDays {
  protected static final Logger LOGGER = LoggerFactory.getLogger(SetSpecialDays.class);

  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @When("^the set special days request is received$")
  public void theSetSpecialDaysRequestIsReceived(final Map<String, String> requestData)
      throws Throwable {
    final SetSpecialDaysRequest setSpecialDaysRequest =
        SetSpecialDaysRequestFactory.fromParameterMap(requestData);

    final SetSpecialDaysAsyncResponse setSpecialDaysAsyncResponse =
        this.smartMeteringConfigurationClient.setSpecialDays(setSpecialDaysRequest);

    LOGGER.info("Set special days response is received {}", setSpecialDaysAsyncResponse);

    assertThat(setSpecialDaysAsyncResponse)
        .as("Set special days response should not be null")
        .isNotNull();
    ScenarioContext.current()
        .put(
            PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
            setSpecialDaysAsyncResponse.getCorrelationUid());
  }

  @Then("^the special days should be set on the device$")
  public void theSpecialDaysShouldBeSetOnTheDevice(final Map<String, String> settings)
      throws Throwable {
    final SetSpecialDaysAsyncRequest setSpecialDaysAsyncRequest =
        SetSpecialDaysRequestFactory.fromScenarioContext();
    final SetSpecialDaysResponse setSpecialDaysResponse =
        this.smartMeteringConfigurationClient.retrieveSetSpecialDaysResponse(
            setSpecialDaysAsyncRequest);

    LOGGER.info("Set special days result is: {}", setSpecialDaysResponse.getResult());

    assertThat(setSpecialDaysResponse.getResult())
        .as("Set special days result is null")
        .isNotNull();
    assertThat(setSpecialDaysResponse.getResult())
        .as("Set special days result should be OK")
        .isEqualTo(OsgpResultType.OK);
  }
}
