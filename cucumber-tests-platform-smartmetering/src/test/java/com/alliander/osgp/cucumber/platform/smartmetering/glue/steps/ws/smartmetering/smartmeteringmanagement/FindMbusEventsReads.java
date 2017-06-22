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
import com.alliander.osgp.cucumber.core.Helpers;
import com.alliander.osgp.cucumber.platform.PlatformKeys;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class FindMbusEventsReads extends AbstractFindEventsReads {
    private static final List<EventType> allowed = Collections.unmodifiableList(Arrays.asList(new EventType[] {
            EventType.EVENTLOG_CLEARED, EventType.COMMUNICATION_ERROR_M_BUS_CHANNEL_1,
            EventType.COMMUNICATION_ERROR_M_BUS_CHANNEL_2, EventType.COMMUNICATION_ERROR_M_BUS_CHANNEL_3,
            EventType.COMMUNICATION_ERROR_M_BUS_CHANNEL_4, EventType.COMMUNICATION_OK_M_BUS_CHANNEL_1,
            EventType.COMMUNICATION_OK_M_BUS_CHANNEL_2, EventType.COMMUNICATION_OK_M_BUS_CHANNEL_3,
            EventType.COMMUNICATION_OK_M_BUS_CHANNEL_4, EventType.REPLACE_BATTERY_M_BUS_CHANNEL_1,
            EventType.REPLACE_BATTERY_M_BUS_CHANNEL_2, EventType.REPLACE_BATTERY_M_BUS_CHANNEL_3,
            EventType.REPLACE_BATTERY_M_BUS_CHANNEL_4, EventType.FRAUD_ATTEMPT_M_BUS_CHANNEL_1,
            EventType.FRAUD_ATTEMPT_M_BUS_CHANNEL_2, EventType.FRAUD_ATTEMPT_M_BUS_CHANNEL_3,
            EventType.FRAUD_ATTEMPT_M_BUS_CHANNEL_4, EventType.CLOCK_ADJUSTED_M_BUS_CHANNEL_1,
            EventType.CLOCK_ADJUSTED_M_BUS_CHANNEL_2, EventType.CLOCK_ADJUSTED_M_BUS_CHANNEL_3,
            EventType.CLOCK_ADJUSTED_M_BUS_CHANNEL_4, EventType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_1,
            EventType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_2, EventType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_3,
            EventType.NEW_M_BUS_DEVICE_DISCOVERED_CHANNEL_4, EventType.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_1,
            EventType.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_2,
            EventType.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_3,
            EventType.PERMANENT_ERROR_FROM_M_BUS_DEVICE_CHANNEL_4 }));

    @Override
    protected String getEventLogCategory() {
        final String category = EventLogCategory.M_BUS_EVENT_LOG.name();
        return category.substring(0, category.lastIndexOf('_'));
    }

    @When("^receiving a find mbus events request$")
    @Override
    public void receivingAFindEventsRequest(final Map<String, String> requestData) throws Throwable {
        super.receivingAFindEventsRequest(requestData);
    }

    @Then("^mbus events should be returned$")
    @Override
    public void eventsShouldBeReturned(final Map<String, String> settings) throws Throwable {
        super.eventsShouldBeReturned(Helpers.addSetting(settings, PlatformKeys.KEY_EVENTS_NODELIST_EXPECTED, "true"));
    }

    @Then("^mbus events for all types should be returned$")
    public void mbusEventsForAllTypesShouldBeReturned(final Map<String, String> settings) throws Throwable {
        super.eventsForAllTypesShouldBeReturned(settings);
    }

    @Then("^(\\d++) mbus events should be returned$")
    public void numberOfEventsShouldBeReturned(final int numberOfEvents, final Map<String, String> settings)
            throws Throwable {
        super.eventsShouldBeReturned(numberOfEvents, settings);
    }

    @Override
    protected List<EventType> getAllowedEventTypes() {
        return allowed;
    }
}
