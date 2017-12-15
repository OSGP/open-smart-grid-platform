package com.alliander.osgp.cucumber.platform.microgrids.glue.steps.housekeeping;

import static org.junit.Assert.assertNotNull;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.ws.microgrids.domain.entities.RtuResponseData;
import com.alliander.osgp.adapter.ws.microgrids.domain.repositories.RtuResponseDataRepository;
import com.alliander.osgp.cucumber.platform.microgrids.support.RtuResponseDataStateChecker;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class HousekeepingSteps {

    @Autowired
    private RtuResponseDataRepository rtuResponseDataRepository;

    @Autowired
    private RtuResponseDataStateChecker rtuResponseDataStateChecker;

    @When("^the rtu response data cleanup job runs$")
    public void theRtuResponseDataCleanupJobRuns() {
        // TODO OC-31 - Do nothing?
    }

    @Then("^the record with correlation uid \"(.*)\" should be deleted$")
    public void theRecordShouldBeDeleted(final String correlationUid) {

        this.rtuResponseDataStateChecker.waitForRtuResponseDataToBeRemoved(correlationUid, 60000, 120000);
    }

    @Then("^the record with correlation uid \"(.*)\" should not be deleted$")
    public void theRecordShouldNotBeDeleted(final String correlationUid) {

        this.rtuResponseDataStateChecker.waitToMakeSureRtuResponseDataIsNotRemoved(correlationUid, 60000, 120000);
        final RtuResponseData rtuResponseData = this.rtuResponseDataRepository
                .findSingleResultByCorrelationUid(correlationUid);

        assertNotNull("Rtu Response Data should not be deleted", rtuResponseData);
    }
}
