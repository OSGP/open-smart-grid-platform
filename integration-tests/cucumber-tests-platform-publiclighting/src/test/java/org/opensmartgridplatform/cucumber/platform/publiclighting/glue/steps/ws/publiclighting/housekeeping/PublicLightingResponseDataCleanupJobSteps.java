/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.ws.publiclighting.housekeeping;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.opensmartgridplatform.cucumber.core.RetryableAssert;
import org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.database.ws.WsPublicLightingResponseDataRepository;
import org.opensmartgridplatform.cucumber.platform.publiclighting.glue.steps.database.ws.WsPublicLightingResponseDataSteps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

public class PublicLightingResponseDataCleanupJobSteps {

  @Autowired private WsPublicLightingResponseDataSteps responseDataSteps;

  @Autowired private WsPublicLightingResponseDataRepository publicLightingResponseDataRepository;

  @Value("${publiclighting.response.cleanup.wait.delay:1000}")
  private long delay;

  @Value("${publiclighting.response.cleanup.wait.retries:65}")
  private int retries;

  @When("^the response data cleanup job runs$")
  public void theResponseDataCleanupJobRuns() {
    // Do nothing - scheduled task runs automatically
  }

  @Then(
      "^the public lighting cleanup job should have removed the response data with correlation uid \"(.*)\"$")
  @Transactional("txMgrWsPublicLighting")
  public void thePublicLightingCleanupJobShouldHaveRemovedTheResponseData(
      final String correlationUid) {

    this.waitForResponseDataToBeRemoved(
        correlationUid, this.delay, this.retries, this.publicLightingResponseDataRepository);
  }

  @Then(
      "^the public lighting cleanup job should not have removed the response data with correlation uid \"(.*)\"$")
  @Transactional("txMgrWsPublicLighting")
  public void thePublicLightingCleanupJobShouldNotHaveRemovedTheResponseData(
      final String correlationUid) {

    this.waitToMakeSureResponseDataIsNotRemoved(
        correlationUid, this.delay, this.retries, this.publicLightingResponseDataRepository);
  }

  private void waitForResponseDataToBeRemoved(
      final String correlationUid,
      final long delay,
      final int retries,
      final ResponseDataRepository responseDataRepository) {
    try {
      RetryableAssert.assertWithRetries(
          () ->
              this.responseDataSteps.theResponseDataRecordShouldBeDeleted(
                  correlationUid, responseDataRepository),
          retries,
          delay,
          TimeUnit.MILLISECONDS);
    } catch (final AssertionError e) {
      Assertions.fail(
          "Cleanup job should have removed response data with correlation uid "
              + correlationUid
              + " within "
              + RetryableAssert.describeMaxDuration(retries, delay, TimeUnit.MILLISECONDS));
    }
  }

  private void waitToMakeSureResponseDataIsNotRemoved(
      final String correlationUid,
      final long delay,
      final int retries,
      final ResponseDataRepository responseDataRepository) {

    try {
      RetryableAssert.assertDelayedWithRetries(
          () ->
              this.responseDataSteps.theResponseDataRecordShouldNotBeDeleted(
                  correlationUid, responseDataRepository),
          0,
          retries * delay,
          TimeUnit.MILLISECONDS);
    } catch (final AssertionError e) {
      Assertions.fail(
          "Cleanup job should not have removed response data with correlation uid "
              + correlationUid
              + ".");
    }
  }
}
