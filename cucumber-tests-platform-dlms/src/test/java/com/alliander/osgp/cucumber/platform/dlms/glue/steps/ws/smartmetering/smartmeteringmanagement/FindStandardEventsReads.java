/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.glue.steps.ws.smartmetering.smartmeteringmanagement;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;
import static com.alliander.osgp.cucumber.platform.core.Helpers.saveCorrelationUidInScenarioContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EventLogCategory;
import com.alliander.osgp.adapter.ws.schema.smartmetering.management.EventType;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.Keys;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class FindStandardEventsReads extends AbstractFindEventsReads {

    private static final List<EventType> allowed = Collections.unmodifiableList(Arrays.asList(new EventType[] {
            EventType.EVENTLOG_CLEARED, EventType.POWER_FAILURE, EventType.POWER_RETURNED,
            EventType.CLOCK_ADJUSTED_OLD_TIME, EventType.CLOCK_ADJUSTED_NEW_TIME, EventType.CLOCK_INVALID,
            EventType.REPLACE_BATTERY, EventType.BATTERY_VOLTAGE_LOW, EventType.TARIFF_ACTIVATED,
            EventType.PASSIVE_TARIFF_UPDATED, EventType.ERROR_REGISTER_CLEARED, EventType.ALARM_REGISTER_CLEARED,
            EventType.WATCHDOG_ERROR, EventType.FIRMWARE_READY_FOR_ACTIVATION, EventType.FIRMWARE_ACTIVATED,
            EventType.SUCCESSFUL_SELFCHECK_AFTER_FIRMWARE_UPDATE, EventType.CLOCK_UPDATE }));

    @Override
    protected String getEventLogCategory() {
        final String category = EventLogCategory.STANDARD_EVENT_LOG.name();
        return category.substring(0, category.lastIndexOf('_'));
    }

    @When("^receiving a find standard events request$")
    @Override
    public void receivingAFindStandardEventsRequest(final Map<String, String> requestData) throws Throwable {
        super.receivingAFindStandardEventsRequest(requestData);

        // Save the returned CorrelationUid in the Scenario related context for
        // further use.
        saveCorrelationUidInScenarioContext(
                this.runXpathResult.getValue(this.response, PATH_CORRELATION_UID),
                getString(PROPERTIES_MAP, Keys.KEY_ORGANIZATION_IDENTIFICATION,
                        Defaults.DEFAULT_ORGANIZATION_IDENTIFICATION));
    }

    @Then("^standard events should be returned$")
    @Override
    public void eventsShouldBeReturned(final Map<String, String> settings) throws Throwable {
        super.eventsShouldBeReturned(settings);
    }

    @Override
    protected List<EventType> getAllowedEventTypes() {
        return allowed;
    }

}
