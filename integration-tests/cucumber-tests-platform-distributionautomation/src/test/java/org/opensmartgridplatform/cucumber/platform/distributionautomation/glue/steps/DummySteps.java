/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class DummySteps {

    private static final Logger LOGGER = LoggerFactory.getLogger(DummySteps.class);

    @Given("^I wait (.+) seconds$")
    public void iWaitDurationSeconds(final String duration) {
        this.wait(Integer.parseInt(duration));
    }

    @When("^I start the simulator$")
    public void iStartTheSimulator() {

    }

    @Then("^I stop the simulator$")
    public void iStopTheSimulator() {

    }

    private void wait(final int duration) {
        LOGGER.info("Sleep {} seconds", duration);

        try {
            Thread.sleep(duration * 1000);
        } catch (final InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
