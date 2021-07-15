/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringconfiguration;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.OsgpResultType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarAsyncResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetActivityCalendarResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SetActivityCalendarRequestFactory;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration.SmartMeteringConfigurationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SetActivityCalendar {
  protected static final Logger LOGGER = LoggerFactory.getLogger(SetActivityCalendar.class);

  @Autowired private SmartMeteringConfigurationClient smartMeteringConfigurationClient;

  @When("^the set activity calendar request is received$")
  public void theSetActivityCalendarRequestIsReceived(final Map<String, String> requestData)
      throws Throwable {
    final SetActivityCalendarRequest setActivityCalendarRequest =
        SetActivityCalendarRequestFactory.fromParameterMap(requestData);

    final SetActivityCalendarAsyncResponse setActivityCalendarAsyncResponse =
        this.smartMeteringConfigurationClient.setActivityCalendar(setActivityCalendarRequest);

    LOGGER.info(
        "Set activity calendar asyncResponse is received {}", setActivityCalendarAsyncResponse);
    assertThat(setActivityCalendarAsyncResponse)
        .as("Set activity calendar asyncResponse should not be null")
        .isNotNull();

    ScenarioContext.current()
        .put(
            PlatformSmartmeteringKeys.KEY_CORRELATION_UID,
            setActivityCalendarAsyncResponse.getCorrelationUid());
  }

  @Then("^the activity calendar profiles are set on the device$")
  public void theActivityCalendarProfilesAreSetOnTheDevice(final Map<String, String> settings)
      throws Throwable {
    final SetActivityCalendarAsyncRequest setActivityCalendarAsyncRequest =
        SetActivityCalendarRequestFactory.fromScenarioContext();

    final SetActivityCalendarResponse setActivityCalendarResponse =
        this.smartMeteringConfigurationClient.getSetActivityCalendarResponse(
            setActivityCalendarAsyncRequest);

    LOGGER.info(
        "Set activity calendar with result: {}", setActivityCalendarResponse.getResult().name());
    assertThat(setActivityCalendarResponse.getResult())
        .as("Set activity calendar response is null")
        .isNotNull();
    assertThat(setActivityCalendarResponse.getResult())
        .as("Set activity calendar response should be OK")
        .isEqualTo(OsgpResultType.OK);
  }
}
