/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.application.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.NotImplementedException;
import org.joda.time.DateTime;
import org.opensmartgridplatform.core.domain.model.domain.DomainRequestService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.domain.core.entities.RelayStatus;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.opensmartgridplatform.domain.core.valueobjects.RelayType;
import org.opensmartgridplatform.dto.valueobjects.DomainTypeDto;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationDto;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderTimestampService;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventNotificationMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventNotificationMessageService.class);

    @Autowired
    private CorrelationIdProviderTimestampService correlationIdProviderTimestampService;

    @Autowired
    private String netmanagementOrganisation;

    @Autowired
    private DomainRequestService domainRequestService;

    @Autowired
    private EventNotificationHelperService eventNotificationHelperService;

    public void handleEvent(final String deviceIdentification, final Date dateTime, final EventType eventType,
            final String description, final Integer index) throws UnknownEntityException {

        this.eventNotificationHelperService.saveEvent(new Event(deviceIdentification,
                dateTime != null ? dateTime : DateTime.now().toDate(), eventType, description, index));

        if (this.isSwitchingEvent(eventType)) {
            this.handleSwitchingEvent(deviceIdentification, dateTime, eventType, index);
        } else if (this.isLightMeasurementEvent(eventType)) {
            this.handleLightMeasurementDeviceEvent(deviceIdentification, dateTime, eventType, description, index);
        }
    }

    private boolean isSwitchingEvent(final EventType eventType) {
        return eventType.equals(EventType.LIGHT_EVENTS_LIGHT_ON) || eventType.equals(EventType.LIGHT_EVENTS_LIGHT_OFF)
                || eventType.equals(EventType.TARIFF_EVENTS_TARIFF_ON)
                || eventType.equals(EventType.TARIFF_EVENTS_TARIFF_OFF);
    }

    private boolean isLightMeasurementEvent(final EventType eventType) {
        return eventType.equals(EventType.LIGHT_SENSOR_REPORTS_DARK)
                || eventType.equals(EventType.LIGHT_SENSOR_REPORTS_LIGHT);
    }

    public void handleEvent(final String deviceIdentification, final EventNotificationDto event)
            throws UnknownEntityException {

        LOGGER.info("handleEvent() called for device: {} with event: {}", deviceIdentification, event);

        final Date dateTime = event.getDateTime() != null ? event.getDateTime().toDate() : DateTime.now().toDate();
        final EventType eventType = EventType.valueOf(event.getEventType().name());
        final String description = event.getDescription();
        final Integer index = event.getIndex();

        this.handleEvent(deviceIdentification, dateTime, eventType, description, index);
    }

    public void handleEvents(final String deviceIdentification, final List<EventNotificationDto> eventNotifications)
            throws UnknownEntityException {

        LOGGER.info("handleEvents() called for device: {} with eventNotifications.size(): {}", deviceIdentification,
                eventNotifications.size());
        for (final EventNotificationDto event : eventNotifications) {
            LOGGER.info("  event: {}", event);
        }

        final Device device = this.eventNotificationHelperService.findDevice(deviceIdentification);

        /*
         * A list of bundled events may contain events that occurred over a
         * period of time (and as such may contain multiple switching events per
         * relay). Handling light switching events, only update the relay status
         * once for the last switching in the list.
         */
        final List<Event> switchDeviceEvents = new ArrayList<>();

        for (final EventNotificationDto eventNotification : eventNotifications) {
            final DateTime eventTime = eventNotification.getDateTime();
            final EventType eventType = EventType.valueOf(eventNotification.getEventType().name());
            final Event event = new Event(deviceIdentification,
                    eventTime != null ? eventTime.toDate() : DateTime.now().toDate(), eventType,
                    eventNotification.getDescription(), eventNotification.getIndex());

            LOGGER.info("Saving event for device: {} with eventType: {} eventTime: {} description: {} index: {}",
                    deviceIdentification, eventType.name(), eventTime, eventNotification.getDescription(),
                    eventNotification.getIndex());
            this.eventNotificationHelperService.saveEvent(event);

            if (this.isSwitchingEvent(eventType)) {
                switchDeviceEvents.add(event);
            } else if (this.isLightMeasurementEvent(eventType)) {
                this.handleLightMeasurementDeviceEvents(deviceIdentification, eventNotifications);
            }
        }

        this.handleSwitchDeviceEvents(device, switchDeviceEvents);
    }

    private void handleSwitchDeviceEvents(final Device device, final List<Event> switchDeviceEvents)
            throws UnknownEntityException {

        LOGGER.info("handleSwitchDeviceEvents() called for device: {} with lightSwitchingEvents.size(): {}",
                device.getDeviceIdentification(), switchDeviceEvents.size());

        if (switchDeviceEvents.isEmpty()) {
            return;
        }

        // Determine light and tariff relays for SSLD.
        final Ssld ssld = this.eventNotificationHelperService.findSsld(device.getId());
        final Set<Integer> indexesLightRelays = new TreeSet<>();
        final Set<Integer> indexesTariffRelays = new TreeSet<>();
        for (final DeviceOutputSetting deviceOutputSetting : ssld.getOutputSettings()) {
            if (deviceOutputSetting.getOutputType().equals(RelayType.LIGHT)) {
                indexesLightRelays.add(deviceOutputSetting.getExternalId());
            }
            if (deviceOutputSetting.getOutputType().equals(RelayType.TARIFF)
                    || deviceOutputSetting.getOutputType().equals(RelayType.TARIFF_REVERSED)) {
                indexesTariffRelays.add(deviceOutputSetting.getExternalId());
            }
        }
        this.printRelayIndexes(indexesLightRelays, indexesTariffRelays, device.getDeviceIdentification());

        final Map<Integer, RelayStatus> lastRelayStatusPerIndex = new TreeMap<>();
        for (final Event lightSwitchingEvent : switchDeviceEvents) {
            final Integer index = lightSwitchingEvent.getIndex();
            if (index == 0) {
                this.handleLightSwitchingEventForIndex0(indexesLightRelays, device, lightSwitchingEvent,
                        lastRelayStatusPerIndex);
            } else {
                this.createRelayStatus(device, lightSwitchingEvent, index, lastRelayStatusPerIndex);
            }
        }
        this.printRelayStatuses(lastRelayStatusPerIndex, device.getDeviceIdentification());

        if (!lastRelayStatusPerIndex.isEmpty()) {
            LOGGER.info(
                    "calling ssld.updateSwitchingEventRelayStatuses() for device: {} with lastRelayStatusPerIndex.size(): {}",
                    ssld.getDeviceIdentification(), lastRelayStatusPerIndex.size());

            ssld.updateSwitchingEventRelayStatuses(lastRelayStatusPerIndex);
            this.eventNotificationHelperService.saveSsld(ssld);

            this.sendRequestMessageToDomainCore(MessageType.RELAY_STATUS_UPDATED_EVENTS.name(),
                    ssld.getDeviceIdentification(), null);
        }
    }

    private void handleLightMeasurementDeviceEvents(final String deviceIdentification,
            final List<EventNotificationDto> eventNotifications) {

        if (eventNotifications.size() != 1) {
            throw new NotImplementedException(
                    "Only lists containing exactly one light event are supported as bundled events.");
        }

        final EventNotificationDto event = eventNotifications.get(0);
        final EventType eventType = EventType.valueOf(event.getEventType().toString());

        this.handleLightMeasurementDeviceEvent(deviceIdentification, event.getDateTime().toDate(), eventType,
                event.getDescription(), event.getIndex());

    }

    private void handleLightSwitchingEventForIndex0(final Set<Integer> indexesLightRelays, final Device device,
            final Event event, final Map<Integer, RelayStatus> lastRelayStatusPerIndexMap) {
        for (final Integer relayIndex : indexesLightRelays) {
            this.createRelayStatus(device, event, relayIndex, lastRelayStatusPerIndexMap);
        }
    }

    private void createRelayStatus(final Device device, final Event switchingEvent, final Integer relayIndex,
            final Map<Integer, RelayStatus> lastRelayStatusPerIndex) {

        final EventType eventType = switchingEvent.getEventType();
        final boolean isRelayOn = EventType.LIGHT_EVENTS_LIGHT_ON.equals(eventType)
                || EventType.TARIFF_EVENTS_TARIFF_ON.equals(eventType);

        if (lastRelayStatusPerIndex.get(relayIndex) == null || switchingEvent.getDateTime()
                .after(lastRelayStatusPerIndex.get(relayIndex).getLastSwitchingEventTime())) {
            final RelayStatus relayStatus = new RelayStatus.Builder(device, relayIndex)
                    .withLastSwitchingEventState(isRelayOn, switchingEvent.getDateTime())
                    .build();
            lastRelayStatusPerIndex.put(relayIndex, relayStatus);
        }
    }

    private void handleSwitchingEvent(final String deviceIdentification, final Date dateTime, final EventType eventType,
            final int index) throws UnknownEntityException {

        final Ssld ssld = this.eventNotificationHelperService.findSsld(deviceIdentification);
        // If the index == 0 handle all LIGHT relays, otherwise just handle the
        // index.
        if (index == 0) {
            for (final DeviceOutputSetting deviceOutputSetting : ssld.getOutputSettings()) {
                if (deviceOutputSetting.getOutputType().equals(RelayType.LIGHT)) {
                    this.updateRelayStatusForEvent(deviceOutputSetting.getExternalId(), ssld, dateTime, eventType);
                }
            }
        } else {
            this.updateRelayStatusForEvent(index, ssld, dateTime, eventType);
        }

        this.eventNotificationHelperService.saveSsld(ssld);

        this.sendRequestMessageToDomainCore(MessageType.RELAY_STATUS_UPDATED_EVENTS.name(), deviceIdentification, null);
    }

    private void updateRelayStatusForEvent(final int index, final Ssld ssld, final Date dateTime,
            final EventType eventType) {

        final boolean isRelayOn = EventType.LIGHT_EVENTS_LIGHT_ON.equals(eventType)
                || EventType.TARIFF_EVENTS_TARIFF_ON.equals(eventType);

        // Only handle the event if the relay doesn't have a status yet, or
        // if the timestamp of the event is after the timestamp of the last
        // switching event in the DB.
        final RelayStatus oldRelayStatus = ssld.getRelayStatusByIndex(index);
        final Date eventTime = dateTime == null ? DateTime.now().toDate() : dateTime;
        if (oldRelayStatus == null || eventTime.after(oldRelayStatus.getLastSwitchingEventTime())) {
            LOGGER.info("Handling new event {} for device {} to update the relay status for index {} with date {}.",
                    eventType.name(), ssld.getDeviceIdentification(), index, dateTime);

            if (ssld.getRelayStatusByIndex(index) == null
                    || eventTime.after(ssld.getRelayStatusByIndex(index).getLastSwitchingEventTime())) {
                final RelayStatus newRelayState = new RelayStatus.Builder(ssld, index)
                        .withLastSwitchingEventState(isRelayOn, eventTime)
                        .build();

                ssld.addOrUpdateRelayStatus(newRelayState);
            }
        }
    }

    private void handleLightMeasurementDeviceEvent(final String deviceIdentification, final Date dateTime,
            final EventType eventType, final String description, final int index) {

        try {
            final Event lightMeasurementDeviceEvent = new Event(deviceIdentification,
                    dateTime != null ? dateTime : DateTime.now().toDate(), eventType, description, index);
            this.sendRequestMessageToDomainPublicLighting(MessageType.EVENT_NOTIFICATION.name(), deviceIdentification,
                    lightMeasurementDeviceEvent);
        } catch (final Exception e) {
            LOGGER.error(String.format("Unexpected exception while handling events for light measurement device: %s",
                    deviceIdentification), e);
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
        final DomainInfo domainInfo = this.eventNotificationHelperService.findDomainInfo("CORE", "1.0");

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
        final DomainInfo domainInfo = this.eventNotificationHelperService
                .findDomainInfo(DomainTypeDto.PUBLIC_LIGHTING.name(), "1.0");

        this.domainRequestService.send(message, messageType, domainInfo);
    }

    private void printRelayIndexes(final Set<Integer> indexesLightRelays, final Set<Integer> indexesTariffRelays,
            final String deviceIdentification) {
        LOGGER.info("relay indexes for device: {}", deviceIdentification);
        for (final int index : indexesLightRelays) {
            LOGGER.info("  indexesLightRelays: {}", index);
        }
        for (final int index : indexesTariffRelays) {
            LOGGER.info("  indexesTariffRelays: {}", index);
        }
    }

    private void printRelayStatuses(final Map<Integer, RelayStatus> lastRelayStatusPerIndex,
            final String deviceIdentification) {
        LOGGER.info("print relay statuses for device: {}", deviceIdentification);
        for (final Map.Entry<Integer, RelayStatus> entry : lastRelayStatusPerIndex.entrySet()) {
            LOGGER.info("key: {}, value: {}", entry.getKey(), entry.getValue());
        }
    }
}
