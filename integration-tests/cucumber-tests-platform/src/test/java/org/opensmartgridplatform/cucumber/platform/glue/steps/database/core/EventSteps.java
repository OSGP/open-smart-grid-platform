/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.opensmartgridplatform.cucumber.core.DateTimeHelper.getDateTime2;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.opensmartgridplatform.cucumber.core.GlueBase;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.domain.core.entities.RelayStatus;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.EventRepository;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class EventSteps extends GlueBase {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SsldRepository ssldRepository;

    @Autowired
    private EventRepository eventRepository;

    @Given("^an event$")
    public void anEvent(final Map<String, String> data) {
        final Device device = this.deviceRepository
                .findByDeviceIdentification(getString(data, PlatformKeys.KEY_DEVICE_IDENTIFICATION));

        final Event event = new Event(device, getDateTime2(getString(data, PlatformKeys.DATE), DateTime.now()).toDate(),
                getEnum(data, PlatformKeys.EVENT_TYPE, EventType.class, EventType.DIAG_EVENTS_GENERAL),
                getString(data, PlatformKeys.KEY_DESCRIPTION, ""),
                getInteger(data, PlatformKeys.KEY_INDEX, PlatformDefaults.DEFAULT_INDEX));

        this.eventRepository.save(event);
    }

    @Then("^the (?:event is|events are) stored$")
    public void theEventIsStored(final Map<String, String> expectedEntity) {

        // Convert comma separated events into a mutable list (for comparison)
        final List<String> expectedEvents = new ArrayList<>(
                Arrays.asList((expectedEntity.containsKey(PlatformKeys.KEY_EVENTS))
                        ? getString(expectedEntity, PlatformKeys.KEY_EVENTS).split(PlatformKeys.SEPARATOR_COMMA)
                        : (expectedEntity.containsKey(PlatformKeys.KEY_EVENT))
                                ? getString(expectedEntity, PlatformKeys.KEY_EVENT).split(PlatformKeys.SEPARATOR_COMMA)
                                : new String[0]));

        // Convert comma separated indexes into a mutable list (for comparison)
        final List<String> expectedIndexes = new ArrayList<>(
                Arrays.asList((expectedEntity.containsKey(PlatformKeys.KEY_INDEXES))
                        ? getString(expectedEntity, PlatformKeys.KEY_INDEXES).split(PlatformKeys.SEPARATOR_COMMA)
                        : (expectedEntity.containsKey(PlatformKeys.KEY_INDEX)
                                && !expectedEntity.get(PlatformKeys.KEY_INDEX).equals("EMPTY"))
                                        ? getString(expectedEntity, PlatformKeys.KEY_INDEX)
                                                .split(PlatformKeys.SEPARATOR_COMMA)
                                        : new String[] { "0" }));

        Assert.assertEquals("Number of events and indexes must be equal in scenario input", expectedEvents.size(),
                expectedIndexes.size());

        // Wait for the correct events to be available
        Wait.until(() -> {
            final Device device = this.deviceRepository
                    .findByDeviceIdentification(getString(expectedEntity, PlatformKeys.KEY_DEVICE_IDENTIFICATION));

            // Read the actual events received and check the desired size
            final List<Event> actualEvents = this.eventRepository.findByDevice(device);

            // Assume default 1 expected event
            final int expectedNumberOfEvents = getInteger(expectedEntity, PlatformKeys.NUMBER_OF_EVENTS, 1);
            Assert.assertEquals(expectedNumberOfEvents, actualEvents.size());

            // Validate all expected events have been received
            for (final Event actualEvent : actualEvents) {
                int foundEventIndex = -1;

                final Integer actualIndex = actualEvent.getIndex();

                // Try to find each event and corresponding index in the
                // expected events list
                for (int i = 0; i < expectedEvents.size(); i++) {
                    if (EventType.valueOf(expectedEvents.get(i).trim()) == actualEvent.getEventType()
                            && Integer.parseInt(expectedIndexes.get(i)) == actualIndex) {
                        foundEventIndex = i;
                        break;
                    }
                }

                Assert.assertTrue(
                        "Unable to find event [" + actualEvent.getEventType() + "] with index [" + actualIndex + "]",
                        foundEventIndex != -1);

                // Correct combination of event and index are found, remove them
                // from the expected results lists
                expectedEvents.remove(foundEventIndex);
                expectedIndexes.remove(foundEventIndex);
            }
        });

        // Wait until the relay statuses have been updated in the DB
        final int numStatuses = getInteger(expectedEntity, PlatformKeys.NUMBER_OF_STATUSES, 0);
        if (numStatuses > 0) {
            Wait.until(() -> {
                final Ssld ssld = this.ssldRepository
                        .findByDeviceIdentification(getString(expectedEntity, PlatformKeys.KEY_DEVICE_IDENTIFICATION));
                final List<RelayStatus> relayStatuses = ssld.getRelayStatuses();

                Assert.assertTrue("Number of relay_status records = " + relayStatuses.size()
                        + ", wait until this equals " + numStatuses, relayStatuses.size() == numStatuses);
            });
        }
    }

}
