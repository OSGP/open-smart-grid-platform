/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.elster.application.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.alliander.osgp.adapter.protocol.oslp.elster.application.services.oslp.OslpDeviceSettingsService;
import com.alliander.osgp.adapter.protocol.oslp.elster.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.elster.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.oslp.elster.infra.messaging.DeviceResponseMessageSender;
import com.alliander.osgp.adapter.protocol.oslp.elster.infra.messaging.OsgpRequestMessageSender;
import com.alliander.osgp.dto.valueobjects.DeviceFunctionDto;
import com.alliander.osgp.dto.valueobjects.EventNotificationDto;
import com.alliander.osgp.dto.valueobjects.EventTypeDto;
import com.alliander.osgp.oslp.Oslp;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.MessageMetadata;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "oslpDeviceManagementService")
@Transactional(value = "transactionManager")
public class DeviceManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementService.class);

    @Autowired
    private OslpDeviceSettingsService oslpDeviceSettingsService;

    @Autowired
    private OsgpRequestMessageSender osgpRequestMessageSender;

    /**
     * Constructor
     */
    public DeviceManagementService() {
        // Parameterless constructor required for transactions...
    }

    // === ADD EVENT NOTIFICATION ===

    /**
     * Create a new event notification DTO with the given arguments.
     *
     * @param deviceIdentitication
     *            The identification of the device.
     * @param deviceUid
     *            The UID of the device.
     * @param eventType
     *            The event type. May not be empty or null.
     * @param description
     *            The description which came along with the event from the
     *            device. May be an empty string, but not null.
     * @param index
     *            The index of the relay. May not be null.
     * @param timestamp
     *            The date and time of the event. May be an empty string or
     *            null.
     */
    private EventNotificationDto createEventNotificationDto(final String deviceIdentification, final String deviceUid,
            final String eventType, final String description, final Integer index, final String timestamp) {
        Assert.notNull(eventType);
        Assert.notNull(description);
        Assert.notNull(index);

        LOGGER.info("addEventNotification called for device: {} with eventType: {}, description: {} and timestamp: {}",
                deviceIdentification, eventType, description, timestamp);

        // Convert timestamp to DateTime.
        DateTime dateTime;
        if (StringUtils.isEmpty(timestamp)) {
            dateTime = DateTime.now();
            LOGGER.info("timestamp is empty, using DateTime.now(): {}", dateTime);
        } else {
            final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss Z");
            dateTime = dateTimeFormatter.withOffsetParsed().parseDateTime(timestamp.concat(" +0000"));
            LOGGER.info("parsed timestamp from string: {} to DateTime: {}", timestamp, dateTime);
        }

        return new EventNotificationDto(deviceUid, dateTime, EventTypeDto.valueOf(eventType), description, index);
    }

    /**
     * Send a list of event notifications to OSGP Core.
     *
     * @param deviceIdentification
     *            The identification of the device.
     * @param eventNotifications
     *            The event notifications.
     *
     * @throws ProtocolAdapterException
     *             In case the device can not be found in the database.
     */
    public void addEventNotifications(final String deviceUid, final List<Oslp.EventNotification> eventNotifications) {
        LOGGER.info("addEventNotifications called for device {}", deviceUid);
        final OslpDevice oslpDevice = this.oslpDeviceSettingsService.getDeviceByUid(deviceUid);
        final String deviceIdentification = oslpDevice.getDeviceIdentification();

        final List<EventNotificationDto> eventNotificationDtos = new ArrayList<>();
        for (final Oslp.EventNotification eventNotification : eventNotifications) {
            final String eventType = eventNotification.getEvent().name();
            final String description = eventNotification.getDescription();
            final int index = eventNotification.getIndex().isEmpty() ? 0 : (int) eventNotification.getIndex().byteAt(0);
            String timestamp = eventNotification.getTimestamp();
            LOGGER.debug("-->> timestamp: {}", timestamp);
            // Hack for faulty firmware version. RTC_NOT_SET event can contain
            // illegal timestamp value of 20000000xxxxxx.
            if (!StringUtils.isEmpty(timestamp) && timestamp.startsWith("20000000")) {
                final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
                timestamp = DateTime.now().withZone(DateTimeZone.UTC).toString(dateTimeFormatter);
                LOGGER.info("Using DateTime.now() instead of '20000000xxxxxx', value is: {}", timestamp);
            }
            final EventNotificationDto dto = this.createEventNotificationDto(deviceIdentification, deviceUid, eventType,
                    description, index, timestamp);
            eventNotificationDtos.add(dto);
        }

        final RequestMessage requestMessage = new RequestMessage("no-correlationUid", "no-organisation",
                deviceIdentification, new ArrayList<>(eventNotificationDtos));

        this.osgpRequestMessageSender.send(requestMessage, DeviceFunctionDto.ADD_EVENT_NOTIFICATION.name());
    }

    // === UPDATE KEY ===

    public void updateKey(final MessageMetadata messageMetadata,
            final DeviceResponseMessageSender responseMessageSender, final String publicKey) {

        final String deviceIdentification = messageMetadata.getDeviceIdentification();
        final String organisationIdentification = messageMetadata.getOrganisationIdentification();
        LOGGER.info("updateKey called for device: {} for organisation: {} with new publicKey: {}", deviceIdentification,
                organisationIdentification, publicKey);

        try {
            OslpDevice oslpDevice = this.oslpDeviceSettingsService
                    .getDeviceByDeviceIdentification(deviceIdentification);
            if (oslpDevice == null) {
                // Device not found, create new device
                LOGGER.debug("Device [{}] does not exist, creating new device", deviceIdentification);
                oslpDevice = new OslpDevice(deviceIdentification);
                oslpDevice = this.oslpDeviceSettingsService.addDevice(oslpDevice);
            }

            oslpDevice.updatePublicKey(publicKey);
            this.oslpDeviceSettingsService.updateDevice(oslpDevice);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during updateKey", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Exception occurred while updating key", e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        }
    }

    // === REVOKE KEY ===

    public void revokeKey(final MessageMetadata messageMetadata,
            final DeviceResponseMessageSender responseMessageSender) {

        final String deviceIdentification = messageMetadata.getDeviceIdentification();
        final String organisationIdentification = messageMetadata.getOrganisationIdentification();
        LOGGER.info("revokeKey called for device: {} for organisation: {}", deviceIdentification,
                organisationIdentification);

        try {
            final OslpDevice oslpDevice = this.oslpDeviceSettingsService
                    .getDeviceByDeviceIdentification(deviceIdentification);
            if (oslpDevice == null) {
                throw new ProtocolAdapterException(String.format("Device not found: %s", deviceIdentification));
            }

            oslpDevice.revokePublicKey();
            this.oslpDeviceSettingsService.updateDevice(oslpDevice);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during revokeKey", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Exception occurred while revoking key", e);
            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        }
    }

    private void sendResponseMessage(final MessageMetadata messageMetadata, final ResponseMessageResultType result,
            final OsgpException osgpException, final DeviceResponseMessageSender responseMessageSender) {

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(messageMetadata);
        final ProtocolResponseMessage responseMessage = ProtocolResponseMessage.newBuilder()
                .domain(messageMetadata.getDomain()).domainVersion(messageMetadata.getDomainVersion())
                .deviceMessageMetadata(deviceMessageMetadata).result(result).osgpException(osgpException).build();

        responseMessageSender.send(responseMessage);
    }
}
