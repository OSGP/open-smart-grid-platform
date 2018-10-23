/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.housekeeping;

import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.opensmartgridplatform.cucumber.core.RetryableAssert;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws.ResponseUrlDataSteps;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ResponseUrlDataCleanupJobSteps {

    private static final TimeUnit TIME_UNIT_RESPONSE_CLEANUP_DELAY = TimeUnit.MILLISECONDS;

    @Autowired
    private ResponseUrlDataSteps responseUrlDataSteps;

    @Value("${smartmetering.response.cleanup.wait.delay:1000}")
    private long delay;

    @Value("${smartmetering.response.cleanup.wait.retries:65}")
    private int retries;

    @When("^the response url data cleanup job runs$")
    public void theResponseUrlDataCleanupJobRuns() {
        // Do nothing - scheduled task runs automatically
    }

    @Then("^the cleanup job should have removed the response url data with correlation uid \"(.*)\"$")
    public void theCleanupJobShouldHaveRemovedTheResponseUrlData(final String correlationUid) {

        this.waitForResponseUrlDataToBeRemoved(correlationUid, this.delay, this.retries);
    }

    @Then("^the cleanup job should not have removed the response url data with correlation uid \"(.*)\"$")
    public void theCleanupJobShouldNotHaveRemovedTheResponseUrlData(final String correlationUid) {

        this.waitToMakeSureResponseUrlDataIsNotRemoved(correlationUid, this.delay, this.retries);
    }

    private void waitForResponseUrlDataToBeRemoved(final String correlationUid, final long delay, final int retries) {
        try {
            RetryableAssert.assertWithRetries(
                    () -> this.responseUrlDataSteps.theResponseUrlDataRecordShouldBeDeleted(correlationUid), retries,
                    delay, TIME_UNIT_RESPONSE_CLEANUP_DELAY);
        } catch (final AssertionError e) {
            fail("Cleanup job should have removed response url data with correlation uid " + correlationUid + " within "
                    + RetryableAssert.describeMaxDuration(retries, delay, TIME_UNIT_RESPONSE_CLEANUP_DELAY));
        }
    }

    private void waitToMakeSureResponseUrlDataIsNotRemoved(final String correlationUid, final long delay,
            final int retries) {

        try {
            RetryableAssert.assertDelayedWithRetries(
                    () -> this.responseUrlDataSteps.theResponseUrlDataRecordShouldNotBeDeleted(correlationUid), 0,
                    retries * delay, TIME_UNIT_RESPONSE_CLEANUP_DELAY);
        } catch (final AssertionError e) {
            fail("Cleanup job should not have removed response url data with correlation uid " + correlationUid + ".");
        }
    }

}
