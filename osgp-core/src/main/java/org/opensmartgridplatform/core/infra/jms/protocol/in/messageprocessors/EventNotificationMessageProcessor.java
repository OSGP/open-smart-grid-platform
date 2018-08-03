/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.protocol.in.messageprocessors;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.opensmartgridplatform.core.application.services.EventNotificationMessageService;
import org.opensmartgridplatform.core.infra.jms.protocol.in.ProtocolRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationDto;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

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
                this.eventNotificationMessageService.handleEvent(deviceIdentification, eventNotification);

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
