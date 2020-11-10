/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EventLogCategory;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EventType;

public class FindPowerFailureEventsReads extends AbstractFindEventsReads {

    private static final List<EventType> allowed = Collections.unmodifiableList(Arrays.asList(EventType.POWER_FAILURE));

    @Override
    protected String getEventLogCategory() {
        return EventLogCategory.POWER_FAILURE_EVENT_LOG.name();
    }

    @When("^receiving a find power failure events request$")
    @Override
    public void receivingAFindEventsRequest(final Map<String, String> requestData) throws Throwable {
        super.receivingAFindEventsRequest(requestData);
    }

    @Then("^(\\d++) power failure events should be returned$")
    public void numberOfEventsShouldBeReturned(final int numberOfEvents, final Map<String, String> settings)
            throws Throwable {
        super.eventsShouldBeReturned(numberOfEvents, settings);
    }

    @Override
    protected List<EventType> getAllowedEventTypes() {
        return allowed;
    }

}
