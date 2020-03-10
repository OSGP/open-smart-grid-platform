
/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.devicemanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.DateTimeHelper.getDateTime;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindEventsRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindEventsResponse;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreDeviceManagementClient;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.EventRepository;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Class with all the create organization requests steps
 */
public class RetrieveReceivedEventNotifications {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CoreDeviceManagementClient client;

    /**
     * There are 47 events enumerated by
     * {@link org.opensmartgridplatform.domain.core.valueobjects.EventType}.
     * This step will create an event record for every event type.
     */
    @Given("^all events are present for device$")
    public void allEvents(final Map<String, String> data) {
        final Device device = this.deviceRepository
                .findByDeviceIdentification(getString(data, PlatformKeys.KEY_DEVICE_IDENTIFICATION));

        for (final EventType eventType : EventType.values()) {
            final Event event = new Event(device, getDateTime(PlatformDefaults.TIMESTAMP).toDate(), eventType,
                    PlatformDefaults.DEFAULT_EVENT_DESCRIPTION, PlatformDefaults.DEFAULT_INDEX);
            this.eventRepository.save(event);
        }
    }

    @Given("^(\\d+) events?$")
    public void anEvent(final int amount, final Map<String, String> data) throws Exception {
        final Device device = this.deviceRepository
                .findByDeviceIdentification(getString(data, PlatformKeys.KEY_DEVICE_IDENTIFICATION));

        for (int i = 0; i < amount; i++) {
            final Event event = new Event(device,
                    getDateTime(getString(data, PlatformKeys.TIMESTAMP, PlatformDefaults.TIMESTAMP)).toDate(),
                    getEnum(data, PlatformKeys.EVENT_TYPE, EventType.class, EventType.ALARM_NOTIFICATION),
                    getString(data, PlatformKeys.KEY_DESCRIPTION, PlatformDefaults.DEFAULT_EVENT_DESCRIPTION),
                    getInteger(data, PlatformKeys.KEY_INDEX, PlatformDefaults.DEFAULT_INDEX));

            this.eventRepository.save(event);
        }
    }

    @When("^a retrieve event notification request is sent$")
    public void aRetrieveEventNotificationRequestIsSent(final Map<String, String> settings)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final FindEventsRequest request = new FindEventsRequest();
        request.setDeviceIdentification(getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));
        request.setPageSize(getInteger(settings, PlatformKeys.KEY_PAGE_SIZE, PlatformDefaults.DEFAULT_PAGE_SIZE));
        request.setPage(getInteger(settings, PlatformKeys.REQUESTED_PAGE, PlatformDefaults.REQUESTED_PAGE));

        try {
            ScenarioContext.current()
                    .put(getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION).concat("_").concat(PlatformKeys.RESPONSE),
                            this.client.findEventsResponse(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.current()
                    .put(getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION).concat("_").concat(PlatformKeys.RESPONSE),
                            ex);
        }
    }

    @Then("^the retrieve event notification response contains$")
    public void theRetrieveEventNotificationResponseContains(final Map<String, String> expectedResponse) {
        final FindEventsResponse response = (FindEventsResponse) ScenarioContext.current()
                .get(getString(expectedResponse, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION).concat("_").concat(PlatformKeys.RESPONSE));

        final List<org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Event> events = response
                .getEvents();

        assertThat(events.isEmpty()).isFalse();

        for (final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Event e : events) {
            assertThat(e.getTimestamp()).isNotNull();
            assertThat(e.getDeviceIdentification()).isEqualTo(getString(expectedResponse,
                    PlatformKeys.KEY_DEVICE_IDENTIFICATION, PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));
            assertThat(e.getEventType().value()).isEqualTo(getString(expectedResponse, PlatformKeys.EVENT_TYPE));
            assertThat(e.getDescription()).isEqualTo(getString(expectedResponse, PlatformKeys.KEY_DESCRIPTION));
            assertThat(e.getIndex())
                    .isEqualTo(getInteger(expectedResponse, PlatformKeys.KEY_INDEX, PlatformDefaults.DEFAULT_INDEX));
        }
    }

    @Then("^the retrieve event notification request response should contain (\\d+) pages$")
    public void theRetrieveEventNotificationRequestIsReceived(final int totalPages,
            final Map<String, String> expectedResponse) {
        final FindEventsResponse response = (FindEventsResponse) ScenarioContext.current()
                .get(getString(expectedResponse, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION).concat("_").concat(PlatformKeys.RESPONSE));

        assertThat(response.getPage().getTotalPages()).isEqualTo(totalPages);
    }

    @Then("^the retrieve event notifications response should contain (\\d+) events on the current page and a total of (\\d+) pages$")
    public void theRetrieveEventNotificationResponseShouldContainNumberOfEventsAndTotalPages(final int numberOfEvents,
            final int totalPages, final Map<String, String> expectedResponse) {

        final FindEventsResponse response = (FindEventsResponse) ScenarioContext.current()
                .get(getString(expectedResponse, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION).concat("_").concat(PlatformKeys.RESPONSE));

        assertThat(response.getPage().getTotalPages()).isEqualTo(totalPages);
        assertThat(response.getEvents().size()).isEqualTo(numberOfEvents);
    }

    @Then("^the stored events from \"([^\"]*)\" are filtered and retrieved$")
    public void theStoredEventsFromADeviceAreFilteredAndRetrieved(final String deviceIdentification,
            final Map<String, String> expectedResponse) throws Throwable {
        final List<Event> events = new ArrayList<>();
        final List<Event> eventIterator = this.retrieveStoredEvents(deviceIdentification);
        final DateTime fromTimestamp = getDateTime(getString(expectedResponse, PlatformKeys.FROM_TIMESTAMP)),
                toTimestamp = getDateTime(getString(expectedResponse, PlatformKeys.TO_TIMESTAMP));

        for (final Event e : eventIterator) {
            if (fromTimestamp.isBefore(e.getDateTime().getTime()) && toTimestamp.isAfter(e.getDateTime().getTime())) {
                events.add(e);
            }
        }

        assertThat(events.size()).isEqualTo((int) getInteger(expectedResponse, PlatformKeys.KEY_RESULT));
    }

    @Then("^the stored events from \"([^\"]*)\" are retrieved and contain$")
    public void theStoredEventsAreRetrieved(final String deviceIdentification,
            final Map<String, String> expectedResponse) throws Throwable {

        final List<Event> events = Wait.untilAndReturn(() -> {
            final List<Event> retval = this.retrieveStoredEvents(deviceIdentification);
            assertThat(retval).isNotNull();
            assertThat(retval.size() > 0).isTrue();
            return retval;
        });

        for (final Event e : events) {
            assertThat(e.getEventType()).isEqualTo(getEnum(expectedResponse, PlatformKeys.EVENT_TYPE, EventType.class));

            assertThat(e.getDescription()).isEqualTo(getString(expectedResponse, PlatformKeys.KEY_DESCRIPTION));

            assertThat((int) e.getIndex()).isEqualTo(
                    (int) getInteger(expectedResponse, PlatformKeys.KEY_INDEX, PlatformDefaults.DEFAULT_INDEX));
        }
    }

    @Then("^the stored events are filtered and retrieved$")
    public void theStoredEventsAreFilteredAndRetrieved(final Map<String, String> expectedResponse) throws Throwable {
        List<Event> events;

        if (getString(expectedResponse, PlatformKeys.KEY_DEVICE_IDENTIFICATION).isEmpty()) {
            events = this.retrieveStoredEvents();
        } else {
            events = this.retrieveStoredEvents(getString(expectedResponse, PlatformKeys.KEY_DEVICE_IDENTIFICATION));
        }

        assertThat(events.size()).isEqualTo((int) getInteger(expectedResponse, PlatformKeys.KEY_RESULT));
    }

    public List<Event> retrieveStoredEvents(final String deviceIdentification) {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        return this.eventRepository.findByDevice(device);
    }

    public List<Event> retrieveStoredEvents() {
        return this.eventRepository.findAll();
    }
}
