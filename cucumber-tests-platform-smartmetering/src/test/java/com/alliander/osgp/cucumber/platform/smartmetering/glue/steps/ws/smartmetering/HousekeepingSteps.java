package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.platform.smartmetering.support.ResponseDataStateChecker;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class HousekeepingSteps {

    @Autowired
    private ResponseDataStateChecker responseDataStateChecker;

    @When("^the meter response data cleanup job runs$")
    public void theMeterResponseDataCleanupJobRuns() {
        // Do nothing - scheduled task runs automatically
    }

    @Then("^the record with correlation uid \"(.*)\" should be deleted$")
    public void theRecordShouldBeDeleted(final String correlationUid) {

        this.responseDataStateChecker.waitForResponseDataToBeRemoved(correlationUid, 60000, 120000);
    }

    @Then("^the record with correlation uid \"(.*)\" should not be deleted$")
    public void theRecordShouldNotBeDeleted(final String correlationUid) {

        this.responseDataStateChecker.waitToMakeSureResponseDataIsNotRemoved(correlationUid, 60000, 120000);
    }
}
