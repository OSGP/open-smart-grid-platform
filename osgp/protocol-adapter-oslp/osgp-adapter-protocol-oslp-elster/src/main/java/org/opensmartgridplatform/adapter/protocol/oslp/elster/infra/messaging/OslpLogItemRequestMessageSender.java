/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component(value = "protocolOslpOutboundLogItemRequestsMessageSender")
public class OslpLogItemRequestMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(OslpLogItemRequestMessageSender.class);

    @Autowired
    @Qualifier("protocolOslpOutboundLogItemRequestsJmsTemplate")
    private JmsTemplate jmsTemplate;

    public void send(final OslpLogItemRequestMessage oslpLogItemRequestMessage) {

        LOGGER.debug("Sending OslpLogItemRequestMessage");

        this.jmsTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(final Session session) throws JMSException {
                return OslpLogItemRequestMessageSender.createMessage(session, oslpLogItemRequestMessage);
            }
        });
    }

    private static Message createMessage(final Session session,
            final OslpLogItemRequestMessage oslpLogItemRequestMessage) throws JMSException {
        final ObjectMessage objectMessage = session.createObjectMessage();
        objectMessage.setJMSType(Constants.OSLP_LOG_ITEM_REQUEST);
        objectMessage.setStringProperty(Constants.IS_INCOMING, oslpLogItemRequestMessage.isIncoming().toString());
        objectMessage.setStringProperty(Constants.DEVICE_UID, oslpLogItemRequestMessage.getDeviceUid());
        objectMessage.setStringProperty(Constants.ENCODED_MESSAGE, oslpLogItemRequestMessage.getEncodedMessage());
        objectMessage.setStringProperty(Constants.DECODED_MESSAGE, oslpLogItemRequestMessage.getDecodedMessage());
        objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION,
                oslpLogItemRequestMessage.getDeviceIdentification());
        objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                oslpLogItemRequestMessage.getOrganisationIdentification());
        objectMessage.setStringProperty(Constants.IS_VALID, oslpLogItemRequestMessage.isValid().toString());
        objectMessage.setIntProperty(Constants.PAYLOAD_MESSAGE_SERIALIZED_SIZE,
                oslpLogItemRequestMessage.getPayloadMessageSerializedSize());
        return objectMessage;

    }
}
