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

import org.apache.activemq.command.ActiveMQDestination;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.wsheaderattribute.priority.MessagePriorityEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component(value = "protocolOslpOutboundSigningServerRequestsMessageSender")
public class SigningServerRequestMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(SigningServerRequestMessageSender.class);

    @Autowired
    @Qualifier("protocolOslpOutboundSigningServerRequestsJmsTemplate")
    private JmsTemplate jmsTemplate;

    @Autowired
    private ActiveMQDestination replyToQueue;

    public void send(final RequestMessage requestMessage, final String messageType) {
        this.send(requestMessage, messageType, MessagePriorityEnum.DEFAULT.getPriority());
    }

    public void send(final RequestMessage requestMessage, final String messageType, final int messagePriority) {
        LOGGER.info("Sending request message to signing server, with reply-to-queue: {}.", this.replyToQueue);

        this.jmsTemplate.send(new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final ObjectMessage objectMessage = session.createObjectMessage(requestMessage);
                objectMessage.setJMSType(messageType);
                objectMessage.setJMSPriority(messagePriority);
                objectMessage.setJMSReplyTo(SigningServerRequestMessageSender.this.replyToQueue);
                objectMessage.setJMSCorrelationID(requestMessage.getCorrelationUid());
                objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                        requestMessage.getOrganisationIdentification());
                objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION,
                        requestMessage.getDeviceIdentification());

                return objectMessage;
            }

        });
    }

}
