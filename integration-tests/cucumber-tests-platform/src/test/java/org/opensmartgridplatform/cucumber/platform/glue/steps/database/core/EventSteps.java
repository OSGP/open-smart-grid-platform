/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.DateTimeHelper.getDateTime2;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.domain.core.entities.RelayStatus;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.repositories.EventRepository;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.springframework.beans.factory.annotation.Autowired;

public class EventSteps {

  @Autowired private SsldRepository ssldRepository;

  @Autowired private EventRepository eventRepository;

  @Given("^an event$")
  public void anEvent(final Map<String, String> data) {
    final String deviceIdentification = getString(data, PlatformKeys.KEY_DEVICE_IDENTIFICATION);
    final Date date = getDateTime2(getString(data, PlatformKeys.DATE), DateTime.now()).toDate();
    final EventType eventType =
        getEnum(data, PlatformKeys.EVENT_TYPE, EventType.class, EventType.DIAG_EVENTS_GENERAL);
    final String description = getString(data, PlatformKeys.KEY_DESCRIPTION, "");
    final Integer index = getInteger(data, PlatformKeys.KEY_INDEX, PlatformDefaults.DEFAULT_INDEX);

    final Event event = new Event(deviceIdentification, date, eventType, description, index);

    this.eventRepository.save(event);
  }

  @Then("^the (?:event is|events are) stored$")
  public void theEventIsStored(final Map<String, String> expectedEntity) {

    // Convert comma separated events into a mutable list (for comparison)
    final List<String> expectedEvents =
        new ArrayList<>(
            Arrays.asList(
                (expectedEntity.containsKey(PlatformKeys.KEY_EVENTS))
                    ? getString(expectedEntity, PlatformKeys.KEY_EVENTS)
                        .split(PlatformKeys.SEPARATOR_COMMA)
                    : (expectedEntity.containsKey(PlatformKeys.KEY_EVENT))
                        ? getString(expectedEntity, PlatformKeys.KEY_EVENT)
                            .split(PlatformKeys.SEPARATOR_COMMA)
                        : new String[0]));

    // Convert comma separated indexes into a mutable list (for comparison)
    final List<String> expectedIndexes =
        new ArrayList<>(
            Arrays.asList(
                (expectedEntity.containsKey(PlatformKeys.KEY_INDEXES))
                    ? getString(expectedEntity, PlatformKeys.KEY_INDEXES)
                        .split(PlatformKeys.SEPARATOR_COMMA)
                    : (expectedEntity.containsKey(PlatformKeys.KEY_INDEX)
                            && !expectedEntity.get(PlatformKeys.KEY_INDEX).equals("EMPTY"))
                        ? getString(expectedEntity, PlatformKeys.KEY_INDEX)
                            .split(PlatformKeys.SEPARATOR_COMMA)
                        : new String[] {"0"}));

    assertThat(expectedIndexes.size())
        .as("Number of events and indexes must be equal in scenario input")
        .isEqualTo(expectedEvents.size());

    // Wait for the correct events to be available
    Wait.until(
        () -> {
          final String deviceIdentification =
              getString(expectedEntity, PlatformKeys.KEY_DEVICE_IDENTIFICATION);

          // Read the actual events received and check the desired size
          final List<Event> actualEvents =
              this.eventRepository.findByDeviceIdentification(deviceIdentification);

          // Assume default 1 expected event
          final int expectedNumberOfEvents =
              getInteger(expectedEntity, PlatformKeys.NUMBER_OF_EVENTS, 1);
          assertThat(actualEvents.size()).isEqualTo(expectedNumberOfEvents);

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
            assertThat(foundEventIndex != -1)
                .as(
                    "Unable to find event ["
                        + actualEvent.getEventType()
                        + "] with index ["
                        + actualIndex
                        + "]")
                .isTrue();

            // Correct combination of event and index are found, remove them
            // from the expected results lists
            expectedEvents.remove(foundEventIndex);
            expectedIndexes.remove(foundEventIndex);
          }
        });

    // Wait until the relay statuses have been updated in the DB
    final int numStatuses = getInteger(expectedEntity, PlatformKeys.NUMBER_OF_STATUSES, 0);
    if (numStatuses > 0) {
      Wait.until(
          () -> {
            final Ssld ssld =
                this.ssldRepository.findByDeviceIdentification(
                    getString(expectedEntity, PlatformKeys.KEY_DEVICE_IDENTIFICATION));
            final List<RelayStatus> relayStatuses = ssld.getRelayStatuses();

            assertThat(relayStatuses.size() == numStatuses)
                .as(
                    "Number of relay_status records = "
                        + relayStatuses.size()
                        + ", wait until this equals "
                        + numStatuses)
                .isTrue();
          });
    }
  }
}
