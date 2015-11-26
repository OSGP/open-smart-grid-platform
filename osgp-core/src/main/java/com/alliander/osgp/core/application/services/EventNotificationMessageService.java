/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.application.services;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Event;
import com.alliander.osgp.domain.core.entities.RelayStatus;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.valueobjects.EventType;

@Service
@Transactional
public class EventNotificationMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventNotificationMessageService.class);

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private EventRepository eventRepository;

    @Transactional(value = "transactionManager")
    public void handleEvent(final String deviceIdentification, final String deviceUid, final EventType eventType,
            final String description, final Integer index) throws UnknownEntityException {

        // Lookup device
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);

        if (device != null) {
            // If the event belonged to an existing device, then save it,
            // otherwise don't.
            this.eventRepository.save(new Event(device, eventType, description, index));

            // Checking to see if it was a light switching event
            if (eventType.equals(EventType.LIGHT_EVENTS_LIGHT_ON) || eventType.equals(EventType.LIGHT_EVENTS_LIGHT_OFF)) {
                this.handleLightSwitchingEvent(device, eventType, index);
            }

        } else {
            throw new UnknownEntityException(Device.class, deviceUid);
        }
    }

    public void handleLightSwitchingEvent(final Device device, final EventType eventType, final int index) {

        final boolean lightsOn = EventType.LIGHT_EVENTS_LIGHT_ON.equals(eventType);

        if (index != 0) {
            // only handle the event if the relay doesn't have a status yet, or
            // if the state changed
            if ((device.getRelayOneStatus() == null) || (device.getRelayOneStatus().isLastKnownState() != lightsOn)) {

                LOGGER.info("Handling new {} for device {}.", eventType.name(), device.getDeviceIdentification());
            }
        }

    }

    private RelayStatus updateRelayStatus(RelayStatus relay, final boolean lightsOn) {
        if (relay == null) {
            relay = new RelayStatus(lightsOn, DateTime.now().toDate());
        } else {
            relay.updateStatus(lightsOn, DateTime.now().toDate());
        }
        return relay;
    }
}
