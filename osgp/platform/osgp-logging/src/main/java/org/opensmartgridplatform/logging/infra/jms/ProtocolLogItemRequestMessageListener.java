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

import org.opensmartgridplatform.logging.domain.entities.DeviceLogItem;
import org.opensmartgridplatform.logging.domain.repositories.DeviceLogItemPagingRepository;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

//This class should fetch incoming messages from a logging requests queue.
public class ProtocolLogItemRequestMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolLogItemRequestMessageListener.class);

    @Autowired
    private DeviceLogItemPagingRepository deviceLogRepository;

    @Override
    public void onMessage(final Message message) {

        try {
            final ObjectMessage objectMessage = (ObjectMessage) message;
            final String messageType = objectMessage.getJMSType();

            LOGGER.info("Received protocol log item request message of type [{}]", messageType);

            this.handleDeviceLogMessage(objectMessage);
        } catch (final JMSException e) {
            LOGGER.error("Exception: {}, StackTrace: {}", e.getMessage(), e.getStackTrace(), e);
        }
    }

    private void handleDeviceLogMessage(final ObjectMessage objectMessage) throws JMSException {

        final String deviceIdentification = objectMessage.getStringProperty(Constants.DEVICE_IDENTIFICATION);
        final String organisationIdentification = objectMessage
                .getStringProperty(Constants.ORGANISATION_IDENTIFICATION);

        final DeviceLogItem deviceLogItem = new DeviceLogItem.Builder()
                .withOrganisationIdentification(organisationIdentification)
                .withDeviceUid(objectMessage.getStringProperty(Constants.DEVICE_UID))
                .withDeviceIdentification(deviceIdentification)
                .withIncoming(Boolean.parseBoolean(objectMessage.getStringProperty(Constants.IS_INCOMING)))
                .withValid(Boolean.parseBoolean(objectMessage.getStringProperty(Constants.IS_VALID)))
                .withEncodedMessage(objectMessage.getStringProperty(Constants.ENCODED_MESSAGE))
                .withDecodedMessage(objectMessage.getStringProperty(Constants.DECODED_MESSAGE))
                .withPayloadMessageSerializedSize(
                        objectMessage.getIntProperty(Constants.PAYLOAD_MESSAGE_SERIALIZED_SIZE))
                .build();
        this.deviceLogRepository.save(deviceLogItem);
    }
}
