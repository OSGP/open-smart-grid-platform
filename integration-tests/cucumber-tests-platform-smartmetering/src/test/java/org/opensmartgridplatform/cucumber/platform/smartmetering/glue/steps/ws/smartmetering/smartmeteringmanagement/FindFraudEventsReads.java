/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EventLogCategory;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EventType;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class FindFraudEventsReads extends AbstractFindEventsReads {

    private static final List<EventType> allowed = Collections.unmodifiableList(Arrays.asList(
            EventType.TERMINAL_COVER_CLOSED,
            EventType.TERMINAL_COVER_REMOVED,
            EventType.STRONG_DC_FIELD_DETECTED,
            EventType.NO_STRONG_DC_FIELD_ANYMORE,
            EventType.METER_COVER_CLOSED,
            EventType.METER_COVER_REMOVED,
            EventType.FAILED_LOGIN_ATTEMPT,
            EventType.CONFIGURATION_CHANGE,
            EventType.EVENTLOG_CLEARED));

    @Override
    protected String getEventLogCategory() {
        return EventLogCategory.FRAUD_DETECTION_LOG.name();
    }

    @When("^receiving a find fraud events request$")
    @Override
    public void receivingAFindEventsRequest(final Map<String, String> requestData) throws Throwable {
        super.receivingAFindEventsRequest(requestData);
    }

    @Then("^fraud events should be returned$")
    @Override
    public void eventsShouldBeReturned(final Map<String, String> settings) throws Throwable {
        super.eventsShouldBeReturned(settings);
    }

    @Then("^fraud events for all types should be returned$")
    public void fraudEventsForAllTypesShouldBeReturned(final Map<String, String> settings) throws Throwable {
        super.eventsForAllTypesShouldBeReturned(settings);
    }

    @Then("^(\\d++) fraud events should be returned$")
    public void numberOfEventsShouldBeReturned(final int numberOfEvents, final Map<String, String> settings)
            throws Throwable {
        super.eventsShouldBeReturned(numberOfEvents, settings);
    }

    @Override
    protected List<EventType> getAllowedEventTypes() {
        return allowed;
    }
}
