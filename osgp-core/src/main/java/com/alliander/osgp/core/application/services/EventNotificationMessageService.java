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
import com.alliander.osgp.domain.core.entities.DeviceOutputSetting;
import com.alliander.osgp.domain.core.entities.Event;
import com.alliander.osgp.domain.core.entities.RelayStatus;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.valueobjects.EventType;
import com.alliander.osgp.domain.core.valueobjects.RelayType;

@Service
@Transactional
public class EventNotificationMessageService {

    private static Logger LOGGER = LoggerFactory.getLogger(EventNotificationMessageService.class);

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SsldRepository ssldRepository;

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

    private void handleLightSwitchingEvent(final Device device, final EventType eventType, final int index) {

        // if the index == 0 handle all LIGHT relays, otherwise just handle the
        // index
        if (index == 0) {
            final Ssld ssld = this.ssldRepository.findOne(device.getId());
            for (final DeviceOutputSetting deviceOutputSetting : ssld.getOutputSettings()) {
                if (deviceOutputSetting.getOutputType().equals(RelayType.LIGHT)) {
                    this.updateRelayStatus(deviceOutputSetting.getExternalId(), device, eventType);
                }
            }
        } else {
            this.updateRelayStatus(index, device, eventType);
        }

        this.deviceRepository.save(device);
    }

    private void updateRelayStatus(final int index, final Device device, final EventType eventType) {

        final boolean lightsOn = EventType.LIGHT_EVENTS_LIGHT_ON.equals(eventType);

        // Only handle the event if the relay doesn't have a status yet, or
        // if the state changed
        final Ssld ssld = this.ssldRepository.findOne(device.getId());
        if ((ssld.getRelayStatusByIndex(index) == null)
                || (ssld.getRelayStatusByIndex(index).isLastKnownState() != lightsOn)) {
            LOGGER.info("Handling new {} for device {}.", eventType.name(), device.getDeviceIdentification());

            ssld.updateRelayStatusByIndex(index, new RelayStatus(device, index, lightsOn, DateTime.now().toDate()));
        }
    }
}