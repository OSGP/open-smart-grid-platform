/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.microgrids.glue.steps.housekeeping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alliander.osgp.cucumber.platform.microgrids.support.ResponseDataStateChecker;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ResponseDataCleanupJobSteps {

    @Autowired
    private ResponseDataStateChecker responseDataStateChecker;

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

        this.responseDataStateChecker.waitForResponseDataToBeRemoved(correlationUid, this.waitCheckIntervalMillis,
                this.waitFailMillis);
    }

    @Then("^the cleanup job should not have removed the response data with correlation uid \"(.*)\"$")
    public void theCleanupJobShouldNotHaveRemovedTheResponseData(final String correlationUid) {

        this.responseDataStateChecker.waitToMakeSureResponseDataIsNotRemoved(correlationUid,
                this.waitCheckIntervalMillis, this.waitFailMillis);
    }
}
