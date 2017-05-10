/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.database.core;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getDateTime2;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getEnum;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getInteger;
import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.cucumber.platform.Defaults;
import com.alliander.osgp.cucumber.platform.GlueBase;
import com.alliander.osgp.cucumber.platform.Keys;
import com.alliander.osgp.cucumber.platform.core.wait.Wait;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Event;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.valueobjects.EventType;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class EventSteps extends GlueBase {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private EventRepository eventRepository;

    @Given("^an event$")
    public void anEvent(final Map<String, String> data) throws Exception {
        final Device device = this.deviceRepository
                .findByDeviceIdentification(getString(data, Keys.KEY_DEVICE_IDENTIFICATION));

        Event event = new Event(device, getDateTime2(getString(data, Keys.DATE), DateTime.now()).toDate(),
                getEnum(data, Keys.EVENT_TYPE, EventType.class, EventType.DIAG_EVENTS_GENERAL),
                getString(data, Keys.KEY_DESCRIPTION, ""), getInteger(data, Keys.KEY_INDEX, Defaults.DEFAULT_INDEX));

        this.eventRepository.save(event);

        if (data.containsKey(Keys.TIME_UNTIL_ON)) {
            final DateTime dateTime = getDateTime2(getString(data, Keys.DATE), DateTime.now());
            final DateTime dateTimePlus = dateTime.plusHours(getInteger(data, Keys.TIME_UNTIL_ON));
            if (dateTime.isAfter(dateTimePlus.getMillis())) {
                dateTimePlus.plusDays(1);
            }
            event = new Event(device, dateTimePlus.toDate(), EventType.LIGHT_EVENTS_LIGHT_OFF, "light off",
                    getInteger(data, Keys.KEY_INDEX, Defaults.DEFAULT_INDEX));

            this.eventRepository.save(event);
        }
    }

    @Then("^the (?:event is|events are) stored$")
    public void theEventIsStored(final Map<String, String> expectedEntity) throws Throwable {

        Wait.until(() -> {
            final Device device = this.deviceRepository
                    .findByDeviceIdentification(getString(expectedEntity, Keys.KEY_DEVICE_IDENTIFICATION));

            final List<Event> eventsList = this.eventRepository.findByDevice(device);

            final String[] eventsArray = (expectedEntity.containsKey(Keys.KEY_EVENTS))
                    ? getString(expectedEntity, Keys.KEY_EVENTS).split(Keys.SEPARATOR_COMMA)
                    : (expectedEntity.containsKey(Keys.KEY_EVENT))
                            ? getString(expectedEntity, Keys.KEY_EVENT).split(Keys.SEPARATOR_COMMA) : new String[0];

            final String[] indexesArray = (expectedEntity.containsKey(Keys.KEY_INDEXES))
                    ? getString(expectedEntity, Keys.KEY_INDEXES).split(Keys.SEPARATOR_COMMA)
                    : (expectedEntity.containsKey(Keys.KEY_INDEX)
                            && !expectedEntity.get(Keys.KEY_INDEX).equals("EMPTY"))
                                    ? getString(expectedEntity, Keys.KEY_INDEX).split(Keys.SEPARATOR_COMMA)
                                    : new String[] { "0" };

            if (expectedEntity.containsKey(Keys.NUMBER_OF_EVENTS)) {
                Assert.assertEquals((int) getInteger(expectedEntity, Keys.NUMBER_OF_EVENTS), eventsList.size());
            }

            for (int i = 0; i < eventsList.size(); i++) {
                final Event event = eventsList.get(i);
                Assert.assertEquals(EventType.valueOf(eventsArray[i].trim()), event.getEventType());
                Assert.assertEquals(Integer.parseInt(indexesArray[i]), Character.getNumericValue(event.getIndex()));
            }
        });
    }
}
