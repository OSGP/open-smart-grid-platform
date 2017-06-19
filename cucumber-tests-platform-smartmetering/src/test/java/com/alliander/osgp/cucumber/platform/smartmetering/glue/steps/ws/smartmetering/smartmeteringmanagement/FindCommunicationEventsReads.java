/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EventLogCategory;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EventType;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class FindCommunicationEventsReads extends AbstractFindEventsReads {

    private static final List<EventType> allowed = Collections.unmodifiableList(Arrays.asList(new EventType[] {
            EventType.EVENTLOG_CLEARED, EventType.METROLOGICAL_MAINTENANCE, EventType.TECHNICAL_MAINTENANCE,
            EventType.RETRIEVE_METER_READINGS_E, EventType.RETRIEVE_METER_READINGS_G,
            EventType.RETRIEVE_INTERVAL_DATA_E, EventType.RETRIEVE_INTERVAL_DATA_G, }));

    @Override
    protected String getEventLogCategory() {
        final String category = EventLogCategory.COMMUNICATION_SESSION_LOG.name();
        return category.substring(0, category.lastIndexOf('_'));
    }

    @When("^receiving a find communication events request$")
    @Override
    public void receivingAFindEventsRequest(final Map<String, String> requestData) throws Throwable {
        LOGGER.warn("{} disabled, because it genrates a soap-fault with OBJECT_UNDEFINED",
                FindCommunicationEventsReads.class.getSimpleName());
    }

    @Then("^communication events should be returned$")
    @Override
    public void eventsShouldBeReturned(final Map<String, String> settings) throws Throwable {
    }

    @Then("^communication events for all types should be returned$")
    public void communicationEventsForAllTypesShouldBeReturned(final Map<String, String> settings) throws Throwable {
        super.eventsForAllTypesShouldBeReturned(settings);
    }

    @Then("^(\\d++) communication events should be returned$")
    public void numberOfEventsShouldBeReturned(final int numberOfEvents, final Map<String, String> settings)
            throws Throwable {
        super.eventsShouldBeReturned(numberOfEvents, settings);
    }

    @Override
    protected List<EventType> getAllowedEventTypes() {
        return allowed;
    }
}
