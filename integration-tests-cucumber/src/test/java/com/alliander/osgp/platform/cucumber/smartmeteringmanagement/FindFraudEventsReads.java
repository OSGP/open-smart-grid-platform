/**
 * Copyright 2016 Smart Society Services B.V. *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.smartmeteringmanagement;

import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EventLogCategory;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class FindFraudEventsReads extends AbstractFindEventsReads {

    @Override
    protected String getEventLogCategory() {
        final String category = EventLogCategory.FRAUD_DETECTION_LOG.name();
        return category.substring(0, category.lastIndexOf('_'));
    }

    @When("^the find fraud events request is received$")
    @Override
    public void theFindEventsRequestIsReceived() throws Throwable {
        super.theFindEventsRequestIsReceived();
    }

    @Then("^fraud events should be returned$")
    @Override
    public void eventsShouldBeReturned() throws Throwable {
        super.eventsShouldBeReturned();
    }
}
