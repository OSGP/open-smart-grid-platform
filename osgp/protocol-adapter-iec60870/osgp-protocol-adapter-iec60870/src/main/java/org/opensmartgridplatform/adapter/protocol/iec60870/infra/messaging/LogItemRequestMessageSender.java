/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class LogItemRequestMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogItemRequestMessageSender.class);

    @Autowired
    private JmsTemplate logItemRequestsJmsTemplate;

    public void send(final LogItemRequestMessage logItemRequestMessage) {

        LOGGER.debug("Sending LogItemRequestMessage");

        this.logItemRequestsJmsTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(final Session session) throws JMSException {
                final ObjectMessage objectMessage = session.createObjectMessage();
                objectMessage.setJMSType(Constants.IEC60870_LOG_ITEM_REQUEST);
                objectMessage.setStringProperty(Constants.IS_INCOMING,
                        logItemRequestMessage.isIncoming().toString());
                objectMessage.setStringProperty(Constants.ENCODED_MESSAGE,
                        logItemRequestMessage.getEncodedMessage());
                objectMessage.setStringProperty(Constants.DECODED_MESSAGE,
                        logItemRequestMessage.getDecodedMessage());
                objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION,
                        logItemRequestMessage.getDeviceIdentification());
                objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                        logItemRequestMessage.getOrganisationIdentification());
                objectMessage.setStringProperty(Constants.IS_VALID, logItemRequestMessage.isValid().toString());
                objectMessage.setIntProperty(Constants.PAYLOAD_MESSAGE_SERIALIZED_SIZE,
                        logItemRequestMessage.getPayloadMessageSerializedSize());
                return objectMessage;
            }
        });
    }
}
