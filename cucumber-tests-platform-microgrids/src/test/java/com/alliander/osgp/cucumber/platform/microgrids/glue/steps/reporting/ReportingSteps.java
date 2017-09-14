package com.alliander.osgp.cucumber.platform.microgrids.glue.steps.reporting;

import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.core.GlueBase;
import com.alliander.osgp.cucumber.platform.microgrids.mocks.iec61850.Iec61850MockServer;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class ReportingSteps extends GlueBase {

    @Autowired
    private Iec61850MockServer iec61850MockServerPampus;

    @Given("^all reports are disabled on the rtu$")
    public void allReportsAreDisabled() {
        this.iec61850MockServerPampus.ensureReportsDisabled();
    }

    @Then("^all reports should not be enabled$")
    public void allReportsShouldNotBeEnabled() {
        this.iec61850MockServerPampus.assertReportsDisabled();
    }

    @Then("^all reports should be enabled$")
    public void allReportsShouldBeEnabled() throws Throwable {
        // Thread.sleep(5000);
        this.iec61850MockServerPampus.assertReportsEnabled();
    }
}
