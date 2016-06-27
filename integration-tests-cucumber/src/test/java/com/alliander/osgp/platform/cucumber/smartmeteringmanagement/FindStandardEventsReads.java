/**
 * Copyright 2016 Smart Society Services B.V. *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.smartmeteringmanagement;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EventLogCategory;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EventType;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class FindStandardEventsReads extends AbstractFindEventsReads {

    private static final List<EventType> allowed = Collections.unmodifiableList(Arrays.asList(new EventType[] {
            EventType.EVENTLOG_CLEARED, EventType.POWER_FAILURE, EventType.POWER_RETURNED,
            EventType.CLOCK_ADJUSTED_OLD_TIME, EventType.CLOCK_ADJUSTED_NEW_TIME, EventType.CLOCK_INVALID,
            EventType.REPLACE_BATTERY, EventType.BATTERY_VOLTAGE_LOW, EventType.TARIFF_ACTIVATED,
            EventType.PASSIVE_TARIFF_UPDATED, EventType.ERROR_REGISTER_CLEARED, EventType.ALARM_REGISTER_CLEARED,
            EventType.WATCHDOG_ERROR, EventType.FIRMWARE_READY_FOR_ACTIVATION, EventType.FIRMWARE_ACTIVATED,
            EventType.SUCCESSFUL_SELFCHECK_AFTER_FIRMWARE_UPDATE }));

    @Override
    protected String getEventLogCategory() {
        final String category = EventLogCategory.STANDARD_EVENT_LOG.name();
        return category.substring(0, category.lastIndexOf('_'));
    }

    @When("^the find standard events request is received$")
    @Override
    public void theFindEventsRequestIsReceived() throws Throwable {
        super.theFindEventsRequestIsReceived();
    }

    @Then("^standard events should be returned$")
    @Override
    public void eventsShouldBeReturned() throws Throwable {
        super.eventsShouldBeReturned();
    }

    @Override
    protected List<EventType> getAllowedEventTypes() {
        return allowed;
    }

}
