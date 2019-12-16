/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.signing.server.infra.messaging;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component("signingServerOutboundResponsesMessageSender")
public class SigningServerResponseMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(SigningServerResponseMessageSender.class);

    @Autowired
    @Qualifier("signingServerOutboundResponsesJmsTemplate")
    private JmsTemplate responsesJmsTemplate;

    public void send(final ResponseMessage responseMessage, final String messageType, final Destination replyToQueue) {

        if (!this.checkMessage(responseMessage)) {
            LOGGER.error("Response message failed check, not sending response.");
            return;
        }

        this.sendMessage(responseMessage, messageType, replyToQueue);
    }

    private boolean checkMessage(final ResponseMessage msg) {
        if (StringUtils.isBlank(msg.getOrganisationIdentification())) {
            LOGGER.error("OrganisationIdentification is blank");
            return false;
        }
        if (StringUtils.isBlank(msg.getDeviceIdentification())) {
            LOGGER.error("DeviceIdentification is blank");
            return false;
        }
        if (StringUtils.isBlank(msg.getCorrelationUid())) {
            LOGGER.error("CorrelationUid is blank");
            return false;
        }
        if (msg.getResult() == null) {
            LOGGER.error("Result is null");
            return false;
        }

        return true;
    }

    private void sendMessage(final ResponseMessage responseMessage, final String messageType,
            final Destination replyToQueue) {

        this.responsesJmsTemplate.send(replyToQueue, new MessageCreator() {
            @Override
            public Message createMessage(final Session session) throws JMSException {
                final ObjectMessage objectMessage = session.createObjectMessage(responseMessage);
                objectMessage.setJMSCorrelationID(responseMessage.getCorrelationUid());
                objectMessage.setJMSType(messageType);
                objectMessage.setJMSPriority(responseMessage.getMessagePriority());
                objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                        responseMessage.getOrganisationIdentification());
                objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION,
                        responseMessage.getDeviceIdentification());
                if (responseMessage.getOsgpException() != null) {
                    objectMessage.setStringProperty(Constants.DESCRIPTION,
                            responseMessage.getOsgpException().getMessage());
                }
                return objectMessage;
            }
        });
    }
}
