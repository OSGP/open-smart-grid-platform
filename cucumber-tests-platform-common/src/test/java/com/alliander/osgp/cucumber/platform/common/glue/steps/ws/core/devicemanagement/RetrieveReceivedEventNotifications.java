
/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.common.glue.steps.ws.core.devicemanagement;

import static com.alliander.osgp.cucumber.core.DateTimeHelper.getDateTime;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getEnum;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getInteger;
import static com.alliander.osgp.cucumber.core.ReadSettingsHelper.getString;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindEventsRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindEventsResponse;
import com.alliander.osgp.cucumber.core.GlueBase;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.core.Wait;
import com.alliander.osgp.cucumber.platform.PlatformDefaults;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.common.support.ws.core.CoreDeviceManagementClient;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Event;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.valueobjects.EventType;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Class with all the create organization requests steps
 */
public class RetrieveReceivedEventNotifications extends GlueBase {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CoreDeviceManagementClient client;

    @Given("^(\\d+) events?$")
    public void anEvent(final int amount, final Map<String, String> data) throws Exception {
        final Device device = this.deviceRepository
                .findByDeviceIdentification(getString(data, PlatformKeys.KEY_DEVICE_IDENTIFICATION));

        for (int i = 0; i < amount; i++) {
            final Event event = new Event(device,
                    getDateTime(getString(data, PlatformKeys.TIMESTAMP, PlatformDefaults.TIMESTAMP)).toDate(),
                    getEnum(data, PlatformKeys.EVENT_TYPE, EventType.class, EventType.ALARM_NOTIFICATION),
                    getString(data, PlatformKeys.KEY_DESCRIPTION),
                    getInteger(data, PlatformKeys.KEY_INDEX, PlatformDefaults.DEFAULT_INDEX));

            this.eventRepository.save(event);
        }
    }

    @When("^retrieve event notification request is send$")
    public void retrieveEventNotificationRequestIsSend(final Map<String, String> settings)
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

    @Then("^the retrieve event notification request contains$")
    public void theRetrieveEventNotificationRequestContains(final Map<String, String> expectedResponse) {
        final FindEventsResponse response = (FindEventsResponse) ScenarioContext.current()
                .get(getString(expectedResponse, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION).concat("_").concat(PlatformKeys.RESPONSE));

        final List<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Event> events = response.getEvents();

        Assert.assertFalse(events.isEmpty());

        for (final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Event e : events) {
            Assert.assertNotNull(e.getTimestamp());
            Assert.assertEquals(getString(expectedResponse, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                    PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION), e.getDeviceIdentification());
            Assert.assertEquals(
                    getEnum(expectedResponse, PlatformKeys.EVENT_TYPE,
                            com.alliander.osgp.adapter.ws.schema.core.devicemanagement.EventType.class),
                    e.getEventType());
            Assert.assertEquals(getString(expectedResponse, PlatformKeys.KEY_DESCRIPTION), e.getDescription());
            Assert.assertEquals(getInteger(expectedResponse, PlatformKeys.KEY_INDEX, PlatformDefaults.DEFAULT_INDEX),
                    e.getIndex());
        }
    }

    @Then("^the retrieve event notification request response should contain (\\d+) pages$")
    public void theRetrieveEventNotificationRequestIsReceived(final int totalPages,
            final Map<String, String> expectedResponse) {
        final FindEventsResponse response = (FindEventsResponse) ScenarioContext.current()
                .get(getString(expectedResponse, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                        PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION).concat("_").concat(PlatformKeys.RESPONSE));

        Assert.assertEquals(totalPages, (response.getPage().getTotalPages()));
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

        Assert.assertEquals((int) getInteger(expectedResponse, PlatformKeys.KEY_RESULT), events.size());
    }

    @Then("^the stored events from \"([^\"]*)\" are retrieved and contain$")
    public void theStoredEventsAreRetrieved(final String deviceIdentification,
            final Map<String, String> expectedResponse) throws Throwable {

        final List<Event> events = Wait.untilAndReturn(() -> {
            final List<Event> retval = this.retrieveStoredEvents(deviceIdentification);
            Assert.assertNotNull(retval);
            Assert.assertTrue(retval.size() > 0);
            return retval;
        });

        for (final Event e : events) {
            Assert.assertEquals(getEnum(expectedResponse, PlatformKeys.EVENT_TYPE, EventType.class), e.getEventType());
            Assert.assertEquals(getString(expectedResponse, PlatformKeys.KEY_DESCRIPTION),
                    e.getDescription().toString());
            Assert.assertEquals(
                    (int) getInteger(expectedResponse, PlatformKeys.KEY_INDEX, PlatformDefaults.DEFAULT_INDEX),
                    (int) e.getIndex());
        }
    }

    @Then("^the stored events are filtered and retrieved$")
    public void theStoredEventsAreFilteredAndRetrieved(final Map<String, String> expectedResponse) throws Throwable {
        List<Event> events = null;

        if (getString(expectedResponse, PlatformKeys.KEY_DEVICE_IDENTIFICATION).isEmpty()) {
            events = this.retrieveStoredEvents();
        } else {
            events = this.retrieveStoredEvents(getString(expectedResponse, PlatformKeys.KEY_DEVICE_IDENTIFICATION));
        }

        Assert.assertEquals((int) getInteger(expectedResponse, PlatformKeys.KEY_RESULT), events.size());
    }

    public List<Event> retrieveStoredEvents(final String deviceIdentification) {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        return this.eventRepository.findByDevice(device);
    }

    public List<Event> retrieveStoredEvents() {
        return this.eventRepository.findAll();
    }
}
