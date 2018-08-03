/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.logging.infra.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemRepository;
import org.opensmartgridplatform.shared.infra.jms.Constants;

//This class should fetch incoming messages from a logging requests queue.
public class ProtocolLogItemRequestMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolLogItemRequestMessageListener.class);

    @Autowired
    private DeviceLogItemRepository deviceLogRepository;

    @Override
    public void onMessage(final Message message) {

        try {
            final ObjectMessage objectMessage = (ObjectMessage) message;
            final String messageType = objectMessage.getJMSType();

            LOGGER.info("Received protocol log item request message off type [{}]", messageType);

            this.handleDeviceLogMessage(objectMessage);
        } catch (final JMSException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
        }
    }

    private void handleDeviceLogMessage(final ObjectMessage objectMessage) throws JMSException {

        final String deviceIdentification = objectMessage.getStringProperty(Constants.DEVICE_IDENTIFICATION);
        final String organisationIdentification = objectMessage
                .getStringProperty(Constants.ORGANISATION_IDENTIFICATION);

        final DeviceLogItem deviceLogItem = new DeviceLogItem(organisationIdentification,
                objectMessage.getStringProperty(Constants.DEVICE_UID), deviceIdentification,
                Boolean.parseBoolean(objectMessage.getStringProperty(Constants.IS_INCOMING)),
                Boolean.parseBoolean(objectMessage.getStringProperty(Constants.IS_VALID)),
                objectMessage.getStringProperty(Constants.ENCODED_MESSAGE),
                objectMessage.getStringProperty(Constants.DECODED_MESSAGE),
                objectMessage.getIntProperty(Constants.PAYLOAD_MESSAGE_SERIALIZED_SIZE));
        this.deviceLogRepository.save(deviceLogItem);
    }
}
