/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.application.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.core.domain.model.domain.DomainRequestService;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceOutputSetting;
import com.alliander.osgp.domain.core.entities.DomainInfo;
import com.alliander.osgp.domain.core.entities.Event;
import com.alliander.osgp.domain.core.entities.RelayStatus;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.DomainInfoRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderTimestampService;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.EventMessageDataContainer;
import com.alliander.osgp.domain.core.valueobjects.EventType;
import com.alliander.osgp.domain.core.valueobjects.RelayType;
import com.alliander.osgp.dto.valueobjects.EventNotificationDto;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

@Service
@Transactional
public class EventNotificationMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventNotificationMessageService.class);

    private static final String RELAY_STATUS_UPDATED_EVENTS = "RELAY_STATUS_UPDATED";

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private SsldRepository ssldRepository;

    @Autowired
    private CorrelationIdProviderTimestampService correlationIdProviderTimestampService;

    @Autowired
    private String netmanagementOrganisation;

    @Autowired
    private DomainRequestService domainRequestService;

    @Autowired
    private DomainInfoRepository domainInfoRepository;

    @Transactional(value = "transactionManager")
    public void handleEvent(final String deviceIdentification, final Date dateTime, final EventType eventType,
            final String description, final Integer index) throws UnknownEntityException {

        // Lookup device
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);

        if (device != null) {
            // If the event belonged to an existing device, then save it,
            // otherwise don't.
            this.eventRepository.save(new Event(device, dateTime != null ? dateTime : DateTime.now().toDate(),
                    eventType, description, index));

            // Checking to see if it was a switching event
            if (eventType.equals(EventType.LIGHT_EVENTS_LIGHT_ON) || eventType.equals(EventType.LIGHT_EVENTS_LIGHT_OFF)
                    || eventType.equals(EventType.TARIFF_EVENTS_TARIFF_ON)
                    || eventType.equals(EventType.TARIFF_EVENTS_TARIFF_OFF)) {
                this.handleSwitchingEvent(device, dateTime, eventType, index);
            }

        } else {
            throw new UnknownEntityException(Device.class, deviceIdentification);
        }
    }

    public void handleEvent(final String deviceIdentification, final EventNotificationDto event)
            throws UnknownEntityException {

        final Date dateTime = event.getDateTime() != null ? event.getDateTime().toDate() : DateTime.now().toDate();
        final EventType eventType = EventType.valueOf(event.getEventType().name());
        final String description = event.getDescription();
        final Integer index = event.getIndex();

        this.handleEvent(deviceIdentification, dateTime, eventType, description, index);
    }

    @Transactional(value = "transactionManager")
    public void handleEvents(final String deviceIdentification, final List<EventNotificationDto> eventNotifications)
            throws UnknownEntityException {

        LOGGER.info("handleEvents() called for device: {} with eventNotifications.size(): {}", deviceIdentification,
                eventNotifications.size());

        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
        if (device == null) {
            throw new UnknownEntityException(Device.class, deviceIdentification);
        }

        /*
         * A list of bundled events may contain events that occurred over a
         * period of time (and as such may contain multiple switching events per
         * relay). Handling light switching events, only update the relay status
         * once for the last switching in the list.
         */
        final List<Event> lightSwitchingEvents = new ArrayList<>();
        final List<Event> tariffSwitchingEvents = new ArrayList<>();
        final List<Event> lightMeasurementDeviceEvents = new ArrayList<>();

        for (final EventNotificationDto eventNotification : eventNotifications) {
            final DateTime eventTime = eventNotification.getDateTime();
            final EventType eventType = EventType.valueOf(eventNotification.getEventType().name());
            final Event event = new Event(device, eventTime != null ? eventTime.toDate() : DateTime.now().toDate(),
                    eventType, eventNotification.getDescription(), eventNotification.getIndex());

            LOGGER.info("Saving event with eventType: {} eventTime: {} description: {} index: {}", eventType.name(),
                    eventTime, eventNotification.getDescription(), eventNotification.getIndex());
            this.eventRepository.save(event);

            if (eventType.equals(EventType.LIGHT_EVENTS_LIGHT_ON)
                    || eventType.equals(EventType.LIGHT_EVENTS_LIGHT_OFF)) {
                lightSwitchingEvents.add(event);
            } else if (eventType.equals(EventType.TARIFF_EVENTS_TARIFF_ON)
                    || eventType.equals(EventType.TARIFF_EVENTS_TARIFF_OFF)) {
                tariffSwitchingEvents.add(event);
            } else if (eventType.equals(EventType.LIGHT_SENSOR_REPORTS_DARK)
                    || eventType.equals(EventType.LIGHT_SENSOR_REPORTS_LIGHT)) {
                lightMeasurementDeviceEvents.add(event);
            }
        }

        this.handleLightSwitchingEvents(device, lightSwitchingEvents);
        this.handleTariffSwitchingEvents(device, tariffSwitchingEvents);
        this.handleLightMeasurementDeviceEvents(device, lightMeasurementDeviceEvents);
    }

    private void handleLightSwitchingEvents(final Device device, final List<Event> lightSwitchingEvents) {

        LOGGER.info("handleLightSwitchingEvents() called for device: {} with lightSwitchingEvents.size(): {}",
                device.getDeviceIdentification(), lightSwitchingEvents.size());

        if (lightSwitchingEvents.isEmpty()) {
            return;
        }

        // Determine light relays for SSLD.
        final Ssld ssld = this.ssldRepository.findOne(device.getId());
        final Set<Integer> indexesLightRelays = new TreeSet<>();
        for (final DeviceOutputSetting deviceOutputSetting : ssld.getOutputSettings()) {
            if (deviceOutputSetting.getOutputType().equals(RelayType.LIGHT)) {
                indexesLightRelays.add(deviceOutputSetting.getExternalId());
            }
        }

        for (final int index : indexesLightRelays) {
            LOGGER.info("  indexesLightRelays: {}", index);
        }

        final Map<Integer, RelayStatus> lastRelayStatusPerIndex = new TreeMap<>();
        for (final Event lightSwitchingEvent : lightSwitchingEvents) {
            final Integer index = lightSwitchingEvent.getIndex();
            if (index == 0) {
                this.handleLightSwitchingEventForIndex0(indexesLightRelays, device, lightSwitchingEvent,
                        lastRelayStatusPerIndex);
            } else {
                this.createRelayStatus(device, lightSwitchingEvent, index, lastRelayStatusPerIndex);
            }
        }

        if (!lastRelayStatusPerIndex.isEmpty()) {
            LOGGER.info("calling ssld.updateRelayStatuses() for device: {} with lastRelayStatusPerIndex.size(): {}",
                    ssld.getDeviceIdentification(), lastRelayStatusPerIndex.size());

            ssld.updateRelayStatusses(lastRelayStatusPerIndex);
            this.deviceRepository.save(device);

            this.sendRequestMessageToDomainCore(RELAY_STATUS_UPDATED_EVENTS, ssld.getDeviceIdentification(), null);
        }
    }

    private void handleLightSwitchingEventForIndex0(final Set<Integer> indexesLightRelays, final Device device,
            final Event event, final Map<Integer, RelayStatus> lastRelayStatusPerIndexMap) {
        for (final Integer relayIndex : indexesLightRelays) {
            this.createRelayStatus(device, event, relayIndex, lastRelayStatusPerIndexMap);
        }
    }

    private void createRelayStatus(final Device device, final Event switchingEvent, final Integer relayIndex,
            final Map<Integer, RelayStatus> lastRelayStatusPerIndex) {

        final boolean isRelayOn = EventType.LIGHT_EVENTS_LIGHT_ON.equals(switchingEvent.getEventType())
                || EventType.TARIFF_EVENTS_TARIFF_ON.equals(switchingEvent.getEventType());

        if (lastRelayStatusPerIndex.get(relayIndex) == null || switchingEvent.getDateTime()
                .after(lastRelayStatusPerIndex.get(relayIndex).getLastKnowSwitchingTime())) {
            lastRelayStatusPerIndex.put(relayIndex,
                    new RelayStatus(device, relayIndex, isRelayOn, switchingEvent.getDateTime()));
        }
    }

    private void handleTariffSwitchingEvents(final Device device, final List<Event> tariffSwitchingEvents) {

        if (tariffSwitchingEvents.isEmpty()) {
            return;
        }

        final Ssld ssld = this.ssldRepository.findOne(device.getId());
        final Set<Integer> indexesTariffRelays = new TreeSet<>();
        for (final DeviceOutputSetting deviceOutputSetting : ssld.getOutputSettings()) {
            if (deviceOutputSetting.getOutputType().equals(RelayType.TARIFF)) {
                indexesTariffRelays.add(deviceOutputSetting.getExternalId());
            }
        }

        final Map<Integer, RelayStatus> lastRelayStatusPerIndex = new TreeMap<>();
        for (final Event tariffSwitchingEvent : tariffSwitchingEvents) {
            this.createRelayStatus(device, tariffSwitchingEvent, tariffSwitchingEvent.getIndex(),
                    lastRelayStatusPerIndex);
        }

        if (!lastRelayStatusPerIndex.isEmpty()) {
            ssld.updateRelayStatusses(lastRelayStatusPerIndex);
            this.deviceRepository.save(device);

            this.sendRequestMessageToDomainCore(RELAY_STATUS_UPDATED_EVENTS, ssld.getDeviceIdentification(), null);
        }
    }

    private void handleSwitchingEvent(final Device device, final Date dateTime, final EventType eventType,
            final int index) {

        // if the index == 0 handle all LIGHT relays, otherwise just handle the
        // index
        if (index == 0) {
            final Ssld ssld = this.ssldRepository.findOne(device.getId());
            for (final DeviceOutputSetting deviceOutputSetting : ssld.getOutputSettings()) {
                if (deviceOutputSetting.getOutputType().equals(RelayType.LIGHT)) {
                    this.updateRelayStatus(deviceOutputSetting.getExternalId(), device, dateTime, eventType);
                }
            }
        } else {
            this.updateRelayStatus(index, device, dateTime, eventType);
        }

        this.deviceRepository.save(device);

        this.sendRequestMessageToDomainCore(RELAY_STATUS_UPDATED_EVENTS, device.getDeviceIdentification(), null);
    }

    private void updateRelayStatus(final int index, final Device device, final Date dateTime,
            final EventType eventType) {

        final boolean isRelayOn = EventType.LIGHT_EVENTS_LIGHT_ON.equals(eventType)
                || EventType.TARIFF_EVENTS_TARIFF_ON.equals(eventType);

        // Only handle the event if the relay doesn't have a status yet, or
        // if the state changed
        final Ssld ssld = this.ssldRepository.findOne(device.getId());
        if ((ssld.getRelayStatusByIndex(index) == null)
                || (ssld.getRelayStatusByIndex(index).isLastKnownState() != isRelayOn)) {
            LOGGER.info("Handling new event {} for device {} to update the relay status for index {} with date {}.",
                    eventType.name(), device.getDeviceIdentification(), index, dateTime);

            if (ssld.getRelayStatusByIndex(index) == null
                    || dateTime.after(ssld.getRelayStatusByIndex(index).getLastKnowSwitchingTime())) {
                ssld.updateRelayStatusByIndex(index, new RelayStatus(device, index, isRelayOn,
                        dateTime == null ? DateTime.now().toDate() : dateTime));
            }
        }
    }

    private void handleLightMeasurementDeviceEvents(final Device device,
            final List<Event> lightMeasurementDeviceEvents) {
        if (lightMeasurementDeviceEvents.isEmpty()) {
            LOGGER.info("List of events is empty for device: {}, not needed to process events.",
                    device.getDeviceIdentification());
            return;
        }

        try {
            final EventMessageDataContainer dataContainer = new EventMessageDataContainer(lightMeasurementDeviceEvents);
            this.sendRequestMessageToDomainPublicLighting(DeviceFunction.SET_TRANSITION.name(),
                    device.getDeviceIdentification(), dataContainer);
        } catch (final Exception e) {
            LOGGER.error(String.format("Unexpected exception while handling events for light measurement device: %s",
                    device.getDeviceIdentification()), e);
        }
    }

    /**
     * Send a request message to OSGP-ADAPTER-DOMAIN-CORE.
     */
    private void sendRequestMessageToDomainCore(final String messageType, final String deviceIdentification,
            final Serializable dataObject) {
        final String correlationUid = this.correlationIdProviderTimestampService
                .getCorrelationId(this.netmanagementOrganisation, deviceIdentification);

        final RequestMessage message = new RequestMessage(correlationUid, this.netmanagementOrganisation,
                deviceIdentification, dataObject);
        final DomainInfo domainInfo = this.domainInfoRepository.findByDomainAndDomainVersion("CORE", "1.0");

        this.domainRequestService.send(message, messageType, domainInfo);
    }

    /**
     * Send a request message to OSGP-ADAPTER-DOMAIN-PUBLICLIGHTING.
     */
    private void sendRequestMessageToDomainPublicLighting(final String messageType, final String deviceIdentification,
            final Serializable dataObject) {
        final String correlationUid = this.correlationIdProviderTimestampService
                .getCorrelationId(this.netmanagementOrganisation, deviceIdentification);

        final RequestMessage message = new RequestMessage(correlationUid, this.netmanagementOrganisation,
                deviceIdentification, dataObject);
        final DomainInfo domainInfo = this.domainInfoRepository.findByDomainAndDomainVersion("PUBLIC_LIGHTING", "1.0");

        this.domainRequestService.send(message, messageType, domainInfo);
    }
}
