
/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws.core.devicemanagement;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getDateTime;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getInteger;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindEventsRequest;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.FindEventsResponse;
import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.GlueBase;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.mocks.oslpdevice.DeviceSimulatorException;
import com.alliander.osgp.cucumber.platform.support.ws.core.CoreDeviceManagementClient;
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
                .findByDeviceIdentification(getString(data, Keys.KEY_DEVICE_IDENTIFICATION));

        for (int i = 0; i < amount; i++) {
            final Event event = new Event(device,
                    getDateTime(getString(data, Keys.TIMESTAMP, Defaults.TIMESTAMP)).toDate(),
                    getEnum(data, Keys.EVENT_TYPE, EventType.class, EventType.ALARM_NOTIFICATION),
                    getString(data, Keys.KEY_DESCRIPTION), getInteger(data, Keys.KEY_INDEX, Defaults.DEFAULT_INDEX));

            this.eventRepository.save(event);
        }
    }

    @When("^retrieve event notification request is send$")
    public void retrieveEventNotificationRequestIsSend(final Map<String, String> settings)
            throws IOException, DeviceSimulatorException, WebServiceSecurityException, GeneralSecurityException {

        final FindEventsRequest request = new FindEventsRequest();
        request.setDeviceIdentification(
                getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION));
        request.setPageSize(getInteger(settings, Keys.KEY_PAGE_SIZE, Defaults.DEFAULT_PAGE_SIZE));
        request.setPage(getInteger(settings, Keys.REQUESTED_PAGE, Defaults.REQUESTED_PAGE));

        try {
            ScenarioContext.Current()
                    .put(getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION)
                            .concat("_").concat(Keys.RESPONSE), this.client.findEventsResponse(request));
        } catch (final SoapFaultClientException ex) {
            ScenarioContext.Current()
                    .put(getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION)
                            .concat("_").concat(Keys.RESPONSE), ex);
        }

    }

    @Then("^the retrieve event notification request contains$")
    public void theRetrieveEventNotificationRequestContains(final Map<String, String> expectedResponse) {

        final FindEventsResponse response = (FindEventsResponse) ScenarioContext.Current()
                .get(getString(expectedResponse, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION)
                        .concat("_").concat(Keys.RESPONSE));

        final List<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Event> events = response.getEvents();

        Assert.assertFalse(events.isEmpty());

        for (final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Event e : events) {
            Assert.assertNotNull(e.getTimestamp());
            Assert.assertEquals(
                    getString(expectedResponse, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION),
                    e.getDeviceIdentification());
            Assert.assertEquals(
                    getEnum(expectedResponse, Keys.EVENT_TYPE,
                            com.alliander.osgp.adapter.ws.schema.core.devicemanagement.EventType.class),
                    e.getEventType());
            Assert.assertEquals(getString(expectedResponse, Keys.KEY_DESCRIPTION), e.getDescription());
            Assert.assertEquals(getInteger(expectedResponse, Keys.KEY_INDEX, Defaults.DEFAULT_INDEX), e.getIndex());
        }
    }

    @Then("^the retrieve event notification request response should contain (\\d+) pages$")
    public void theRetrieveEventNotificationRequestIsReceived(final int totalPages,
            final Map<String, String> expectedResponse) {
        final FindEventsResponse response = (FindEventsResponse) ScenarioContext.Current()
                .get(getString(expectedResponse, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION)
                        .concat("_").concat(Keys.RESPONSE));
        Assert.assertEquals(totalPages, (response.getPage().getTotalPages()));
    }

    @Then("^the stored events from \"([^\"]*)\" are filtered and retrieved$")
    public void theStoredEventsFromADeviceAreFilteredAndRetrieved(final String deviceIdentification,
            final Map<String, String> expectedResponse) throws Throwable {

        final List<Event> events = new ArrayList<>();
        final List<Event> eventIterator = this.retrieveStoredEvents(deviceIdentification);

        for (final Event e : eventIterator) {
            if (getDateTime(getString(expectedResponse, Keys.FROM_TIMESTAMP)).isBefore(e.getDateTime().getTime())
                    && getDateTime(getString(expectedResponse, Keys.TO_TIMESTAMP)).isAfter(e.getDateTime().getTime())) {
                events.add(e);
            }
        }
        Assert.assertEquals((int) getInteger(expectedResponse, Keys.KEY_RESULT), events.size());
    }

    @Then("^the stored events from \"([^\"]*)\" are retrieved and contain$")
    public void theStoredEventsAreRetrieved(final String deviceIdentification,
            final Map<String, String> expectedResponse) throws Throwable {

        final List<Event> events = this.retrieveStoredEvents(deviceIdentification);

        for (final Event e : events) {
            Assert.assertEquals(getEnum(expectedResponse, Keys.EVENT_TYPE, EventType.class), e.getEventType());
            Assert.assertEquals(getString(expectedResponse, Keys.KEY_DESCRIPTION), e.getDescription().toString());
            Assert.assertEquals((int) getInteger(expectedResponse, Keys.KEY_INDEX, Defaults.DEFAULT_INDEX),
                    (int) e.getIndex());
        }
    }

    @Then("^the stored events are filtered and retrieved$")
    public void theStoredEventsAreFilteredAndRetrieved(final Map<String, String> expectedResponse) throws Throwable {
        final List<Event> events;
        if (getString(expectedResponse, Keys.KEY_DEVICE_IDENTIFICATION).isEmpty()) {
            events = this.retrieveStoredEvents();
        } else {
            events = this.retrieveStoredEvents(getString(expectedResponse, Keys.KEY_DEVICE_IDENTIFICATION));
        }
        Assert.assertEquals((int) getInteger(expectedResponse, Keys.KEY_RESULT), events.size());
    }

    public List<Event> retrieveStoredEvents(final String deviceIdentification) {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        return this.eventRepository.findByDevice(device);
    }

    public List<Event> retrieveStoredEvents() {
        return this.eventRepository.findAll();
    }
}
