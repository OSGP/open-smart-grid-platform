/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.microgrids.glue.steps.housekeeping;

import static org.junit.Assert.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alliander.osgp.cucumber.platform.microgrids.glue.steps.database.ws.ResponseDataSteps;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ResponseDataCleanupJobSteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseDataCleanupJobSteps.class);

    @Autowired
    private ResponseDataSteps responseDataSteps;

    @Value("${iec61850.rtu.response.wait.check.interval:1000}")
    private int waitCheckIntervalMillis;

    @Value("${iec61850.rtu.response.wait.fail.duration:15000}")
    private int waitFailMillis;

    @When("^the response data cleanup job runs$")
    public void theResponseDataCleanupJobRuns() {
        // Do nothing - Scheduled task runs automatically
    }

    @Then("^the cleanup job should have removed the response data with correlation uid \"(.*)\"$")
    public void theCleanupJobShouldHaveRemovedTheResponseData(final String correlationUid) {

        this.waitForResponseDataToBeRemoved(correlationUid, this.waitCheckIntervalMillis, this.waitFailMillis);
    }

    @Then("^the cleanup job should not have removed the response data with correlation uid \"(.*)\"$")
    public void theCleanupJobShouldNotHaveRemovedTheResponseData(final String correlationUid) {

        this.waitToMakeSureResponseDataIsNotRemoved(correlationUid, this.waitCheckIntervalMillis, this.waitFailMillis);
    }

    private void waitForResponseDataToBeRemoved(final String correlationUid, final int timeout, final int maxtime) {

        for (int delayedtime = 0; delayedtime < maxtime; delayedtime += timeout) {

            try {
                Thread.sleep(timeout);
            } catch (final InterruptedException e) {
                LOGGER.error("Thread sleep interrupted ", e.getMessage());
                break;
            }

            try {
                this.responseDataSteps.theResponseDataRecordShouldBeDeleted(correlationUid);
            } catch (final AssertionError ae) {
                continue;
            }
            return;
        }
        fail("Cleanup job should have removed response data with correlation uid " + correlationUid + " within: "
                + maxtime + "sec.");
    }

    private void waitToMakeSureResponseDataIsNotRemoved(final String correlationUid, final int timeout,
            final int maxtime) {

        for (int delayedtime = 0; delayedtime < maxtime; delayedtime += timeout) {
            try {
                Thread.sleep(timeout);
            } catch (final InterruptedException e) {
                LOGGER.error("Thread sleep interrupted ", e.getMessage());
                break;
            }

            try {
                this.responseDataSteps.theResponseDataRecordShouldNotBeDeleted(correlationUid);
            } catch (final AssertionError ae) {
                fail("Cleanup job should not have removed response data with correlation uid " + correlationUid + ".");
            }
        }
    }
}
