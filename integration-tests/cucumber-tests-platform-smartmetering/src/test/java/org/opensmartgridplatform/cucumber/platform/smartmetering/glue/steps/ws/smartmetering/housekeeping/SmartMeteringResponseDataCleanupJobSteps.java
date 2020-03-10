/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.housekeeping;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.cucumber.core.RetryableAssert;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws.SmartMeteringResponseDataSteps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SmartMeteringResponseDataCleanupJobSteps {

    @Autowired
    private SmartMeteringResponseDataSteps responseDataSteps;

    @Value("${smartmetering.response.cleanup.wait.delay:1000}")
    private long delay;

    @Value("${smartmetering.response.cleanup.wait.retries:65}")
    private int retries;

    @When("^the response data cleanup job runs$")
    public void theResponseDataCleanupJobRuns() {
        // Do nothing - scheduled task runs automatically
    }

    @Then("^the cleanup job should have removed the response data with correlation uid \"(.*)\"$")
    public void theCleanupJobShouldHaveRemovedTheResponseData(final String correlationUid) {

        this.waitForResponseDataToBeRemoved(correlationUid, this.delay, this.retries);
    }

    @Then("^the cleanup job should not have removed the response data with correlation uid \"(.*)\"$")
    public void theCleanupJobShouldNotHaveRemovedTheResponseData(final String correlationUid) {

        this.waitToMakeSureResponseDataIsNotRemoved(correlationUid, this.delay, this.retries);
    }

    private void waitForResponseDataToBeRemoved(final String correlationUid, final long delay, final int retries) {
        try {
            RetryableAssert.assertWithRetries(
                    () -> this.responseDataSteps.theResponseDataRecordShouldBeDeleted(correlationUid), retries, delay,
                    TimeUnit.MILLISECONDS);
        } catch (final AssertionError e) {
            Assertions.fail("Cleanup job should have removed response data with correlation uid " + correlationUid
                    + " within " + RetryableAssert.describeMaxDuration(retries, delay, TimeUnit.MILLISECONDS));
        }
    }

    private void waitToMakeSureResponseDataIsNotRemoved(final String correlationUid, final long delay,
            final int retries) {

        try {
            RetryableAssert.assertDelayedWithRetries(
                    () -> this.responseDataSteps.theResponseDataRecordShouldNotBeDeleted(correlationUid), 0,
                    retries * delay, TimeUnit.MILLISECONDS);
        } catch (final AssertionError e) {
            Assertions.fail(
                    "Cleanup job should not have removed response data with correlation uid " + correlationUid + ".");
        }
    }

}
