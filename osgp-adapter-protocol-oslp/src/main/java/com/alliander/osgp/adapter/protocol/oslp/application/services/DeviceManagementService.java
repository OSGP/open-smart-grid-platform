/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.protocol.oslp.application.services.oslp.OslpDeviceSettingsService;
import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;
import com.alliander.osgp.adapter.protocol.oslp.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.DeviceResponseMessageSender;
import com.alliander.osgp.adapter.protocol.oslp.infra.messaging.OsgpRequestMessageSender;
import com.alliander.osgp.dto.valueobjects.DeviceFunctionDto;
import com.alliander.osgp.dto.valueobjects.EventNotificationDto;
import com.alliander.osgp.dto.valueobjects.EventTypeDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
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
     * Send a new event notification to OSGP Core with the given arguments.
     *
     * @param deviceId
     *            The Uid of the device
     * @param eventType
     *            The event type
     * @param description
     *            The description which came along with the event from the
     *            device.
     * @param index
     *            The index of the device.
     */
    public void addEventNotification(final String deviceUid, final String eventType, final String description,
            final Integer index) {

        final OslpDevice oslpDevice = this.oslpDeviceSettingsService.getDeviceByUid(deviceUid);

        LOGGER.info("addEventNotification called for device: {} with eventType: {}, description: {} and index: {}",
                oslpDevice.getDeviceIdentification(), eventType, description, index);

        final EventNotificationDto eventNotification = new EventNotificationDto(deviceUid, null,
                EventTypeDto.valueOf(eventType),
                description, index);
        final RequestMessage requestMessage = new RequestMessage("no-correlationUid", "no-organisation",
                oslpDevice.getDeviceIdentification(), eventNotification);

        this.osgpRequestMessageSender.send(requestMessage, DeviceFunctionDto.ADD_EVENT_NOTIFICATION.name());
    }

    // === UPDATE KEY ===

    public void updateKey(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final DeviceResponseMessageSender responseMessageSender, final String domain,
            final String domainVersion, final String messageType, final String publicKey) {

        LOGGER.info("updateKey called for device: {} for organisation: {} with new publicKey: {}",
                deviceIdentification, organisationIdentification, publicKey);

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

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during updateKey", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Exception occurred while updating key", e);

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        }
    }

    // === REVOKE KEY ===

    public void revokeKey(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final DeviceResponseMessageSender responseMessageSender, final String domain,
            final String domainVersion, final String messageType) {

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

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during revokeKey", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Exception occurred while revoking key", e);
            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        }
    }

    private void sendResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final OsgpException osgpException,
            final DeviceResponseMessageSender responseMessageSender) {

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(domain, domainVersion, messageType,
                correlationUid, organisationIdentification, deviceIdentification, result, osgpException, null);

        responseMessageSender.send(responseMessage);
    }
}
