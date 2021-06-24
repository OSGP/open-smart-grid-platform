/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;
import static org.opensmartgridplatform.cucumber.core.RetryableAssert.assertWithRetries;
import static org.opensmartgridplatform.cucumber.core.RetryableAssert.describeMaxDuration;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_DEVICE_IDENTIFICATION;

import io.cucumber.java.en.Then;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.core.BaseDeviceSteps;
import org.opensmartgridplatform.domain.core.entities.ScheduledTask;
import org.opensmartgridplatform.domain.core.repositories.ScheduledTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ReschedulingSteps extends BaseDeviceSteps {

  @Autowired private ScheduledTaskRepository scheduledTaskRepository;

  @Then("^the bundled request should be rescheduled$")
  public void bundledRequestIsRescheduled(final Map<String, String> settings) {

    final String deviceIdentification = getString(settings, KEY_DEVICE_IDENTIFICATION);
    final String correlationUid =
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);

    final Runnable assertion =
        () -> {
          final ScheduledTask scheduledTask =
              this.scheduledTaskRepository.findByCorrelationUid(correlationUid);

          assertThat(scheduledTask).isNotNull();
          assertThat(scheduledTask.getDeviceIdentification()).isEqualTo(deviceIdentification);
        };

    final int numberOfRetries = 100;
    final long delay = 1;
    final TimeUnit unit = TimeUnit.SECONDS;

    try {
      assertWithRetries(assertion, numberOfRetries, delay, unit);
    } catch (final AssertionError e) {
      throw new AssertionError(
          String.format(
              "Failed to find a scheduled retry task for bundled request message with correlationUid %s for device %s within %s",
              correlationUid,
              deviceIdentification,
              describeMaxDuration(numberOfRetries, delay, unit)),
          e);
    }
  }
}
