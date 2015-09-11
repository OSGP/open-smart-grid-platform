/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.infra.jms.protocol.in.messageprocessors;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.core.infra.jms.protocol.in.ProtocolRequestMessageProcessor;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Event;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.EventRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.EventType;
import com.alliander.osgp.dto.valueobjects.EventNotification;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

@Component("oslpEventNotificationMessageProcessor")
public class EventNotificationMessageProcessor extends ProtocolRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventNotificationMessageProcessor.class);

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private EventRepository eventRepository;

    protected EventNotificationMessageProcessor() {
        super(DeviceFunction.ADD_EVENT_NOTIFICATION);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        final String messageType = message.getJMSType();
        final String organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
        final String deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

        LOGGER.info("Received message of messageType: {} organisationIdentification: {} deviceIdentification: {}",
                messageType, organisationIdentification, deviceIdentification);

        final RequestMessage requestMessage = (RequestMessage) message.getObject();
        final Object dataObject = requestMessage.getRequest();

        try {
            final EventNotification eventNotification = (EventNotification) dataObject;

            this.addEventNotification(deviceIdentification, eventNotification.getDeviceUid(),
                    com.alliander.osgp.domain.core.valueobjects.EventType.valueOf(eventNotification.getEventType()
                            .name()), eventNotification.getDescription(), eventNotification.getIndex());
        } catch (final UnknownEntityException e) {
            LOGGER.error("Exception", e);
            throw new JMSException(e.getMessage());
        }
    }

    // === ADD EVENT NOTIFICATION ===

    /**
     * Add a new event notification to the repository with the given arguments.
     *
     * @param deviceId
     *            The device identification
     * @param eventType
     *            The event type
     * @param description
     *            The description which came along with the event from the
     *            device.
     * @param index
     *            The index of the device.
     *
     * @throws UnknownEntityException
     *             When the device isn't found.
     */
    @Transactional(value = "transactionManager")
    public void addEventNotification(final String deviceIdentification, final String deviceUid,
            final EventType eventType, final String description, final Integer index) throws UnknownEntityException {

        // Lookup device
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);

        if (device != null) {
            // If the event belonged to an existing device, then save it,
            // otherwise don't.
            this.eventRepository.save(new Event(device, eventType, description, index));
        } else {
            throw new UnknownEntityException(Device.class, deviceUid);
        }
    }
}
