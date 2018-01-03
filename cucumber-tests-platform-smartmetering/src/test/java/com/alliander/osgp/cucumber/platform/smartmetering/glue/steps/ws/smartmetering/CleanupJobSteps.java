/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alliander.osgp.cucumber.platform.smartmetering.support.ResponseDataStateChecker;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class CleanupJobSteps {

    @Autowired
    private ResponseDataStateChecker responseDataStateChecker;

    @Value("${smartmetering.response.wait.check.interval:1000}")
    private int waitCheckIntervalMillis;
    @Value("${smartmetering.response.wait.fail.duration:30000}")
    private int waitFailMillis;

    @When("^the smart metering response data cleanup job runs$")
    public void theSmartMeteringResponseDataCleanupJobRuns() {
        // Do nothing - scheduled task runs automatically
    }

    @Then("^the response data with correlation uid \"(.*)\" should be deleted$")
    public void theResponseDataShouldBeDeleted(final String correlationUid) {

        this.responseDataStateChecker.waitForResponseDataToBeRemoved(correlationUid, this.waitCheckIntervalMillis,
                this.waitFailMillis);
    }

    @Then("^the response data with correlation uid \"(.*)\" should not be deleted$")
    public void theResponseDataShouldNotBeDeleted(final String correlationUid) {

        this.responseDataStateChecker.waitToMakeSureResponseDataIsNotRemoved(correlationUid,
                this.waitCheckIntervalMillis, this.waitFailMillis);
    }
}
