/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmonitoring;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Then;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.GetSystemEventAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.GetSystemEventResponse;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification.NotificationType;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SmartMeteringMonitoringResponseClient;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring.SystemEventRequestFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SystemEventSteps {

  @Autowired
  private SmartMeteringMonitoringResponseClient<GetSystemEventResponse, GetSystemEventAsyncRequest>
      responseClient;

  @Then("^a system event should be returned$")
  public void aSystemEventShouldBeReturned(final Map<String, String> settings) throws Throwable {

    final GetSystemEventAsyncRequest asyncRequest = SystemEventRequestFactory.fromScenarioContext();
    final GetSystemEventResponse response =
        this.responseClient.getResponse(asyncRequest, NotificationType.SYSTEM_EVENT);

    assertThat(response).as("GetSystemEventResponse should not be null").isNotNull();
    assertThat(response.getDeviceIdentification())
        .as("DeviceIdentification should match")
        .isEqualTo(asyncRequest.getDeviceIdentification());
    assertThat(response.getSystemEventType().name())
        .as("Expected type should match")
        .isEqualTo(settings.get(PlatformKeys.KEY_SYSTEM_EVENT_TYPE));

    assertThat(response.getReason()).as("Expected reason should not be null").isNotNull();
    assertThat(response.getTimestamp()).as("Expected timestamp should not be null").isNotNull();
  }

  @Then("^no system event should be returned$")
  public void noSystemEventShouldBeReturned() throws Throwable {

    final boolean hasMoreResponses =
        this.responseClient.hasMoreResponses(NotificationType.SYSTEM_EVENT);

    assertThat(hasMoreResponses).as("No more responses expected").isFalse();
  }
}
