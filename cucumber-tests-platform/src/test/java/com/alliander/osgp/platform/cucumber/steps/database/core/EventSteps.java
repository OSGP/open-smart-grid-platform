/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database.core;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getEnum;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Event;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.valueobjects.EventType;
import com.alliander.osgp.platform.cucumber.steps.Keys;

import cucumber.api.java.en.Then;

public class EventSteps {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Then("^the event is stored$")
    public void theEventIsStored(final Map<String, String> expectedEntity) throws Throwable {

        final Device device = this.deviceRepository
                .findByDeviceIdentification(getString(expectedEntity, Keys.KEY_DEVICE_IDENTIFICATION));

        final List<Event> events = this.eventRepository.findByDevice(device);

        boolean found = false;
        for (Event event : events) {
            if (event.getDescription() == getString(expectedEntity, Keys.KEY_DESCRIPTION)
                    && event.getEventType() == getEnum(expectedEntity, Keys.KEY_EVENT, EventType.class)) {
                found = true;
            }
        }

        Assert.assertTrue(found);
    }
}
