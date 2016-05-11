/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.infra.jms.protocol.in.messageprocessors;

import java.util.Date;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.core.application.services.EventNotificationMessageService;
import com.alliander.osgp.core.infra.jms.protocol.in.ProtocolRequestMessageProcessor;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.dto.valueobjects.EventNotificationDto;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

@Component("oslpEventNotificationMessageProcessor")
public class EventNotificationMessageProcessor extends ProtocolRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventNotificationMessageProcessor.class);

    @Autowired
    private EventNotificationMessageService eventNotificationMessageService;

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

            if (dataObject instanceof EventNotificationDto) {

                final EventNotificationDto eventNotification = (EventNotificationDto) dataObject;

                Date dateTime;
                if (eventNotification.getDateTime() == null) {
                    LOGGER.warn("Event Notification for device {} did not contain date/time, using new Date().",
                            deviceIdentification);
                    dateTime = new Date();
                } else {
                    dateTime = eventNotification.getDateTime().toDate();
                }

                this.eventNotificationMessageService.handleEvent(deviceIdentification, dateTime,
                        com.alliander.osgp.domain.core.valueobjects.EventType.valueOf(eventNotification.getEventType()
                                .name()), eventNotification.getDescription(), eventNotification.getIndex());

            } else if (dataObject instanceof List) {

                @SuppressWarnings("unchecked")
                final List<EventNotificationDto> eventNotifications = (List<EventNotificationDto>) dataObject;
                this.eventNotificationMessageService.handleEvents(deviceIdentification, eventNotifications);
            }

        } catch (final UnknownEntityException e) {
            LOGGER.error("Exception", e);
            throw new JMSException(e.getMessage());
        }
    }
}
