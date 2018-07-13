/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.elster.infra.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageSender;

public class DeviceResponseMessageSender implements ResponseMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceResponseMessageSender.class);

    @Autowired
    @Qualifier("oslpResponsesJmsTemplate")
    private JmsTemplate oslpResponsesJmsTemplate;

    @Override
    public void send(final ResponseMessage responseMessage) {
        if (!(responseMessage instanceof ProtocolResponseMessage)) {
            LOGGER.error("Only ProtocolResponseMessage type is expected for DeviceResponseMessageSender");
            return;
        }

        final ProtocolResponseMessage msg = (ProtocolResponseMessage) responseMessage;

        if (!this.checkMessage(msg)) {
            return;
        }

        this.sendMessage(msg);
    }

    private boolean checkMessage(final ProtocolResponseMessage msg) {
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
        if (StringUtils.isBlank(msg.getDomain())) {
            LOGGER.error("Domain is blank");
            return false;
        }
        if (StringUtils.isBlank(msg.getMessageType())) {
            LOGGER.error("MessageType is blank");
            return false;
        }

        return true;
    }

    private void sendMessage(final ProtocolResponseMessage responseMessage) {

        LOGGER.info("Sending protocol response message for device: {} of message type: {} with message priority: {}",
                responseMessage.getDeviceIdentification(), responseMessage.getMessageType(),
                responseMessage.getMessagePriority());

        this.oslpResponsesJmsTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(final Session session) throws JMSException {
                final ObjectMessage objectMessage = session.createObjectMessage(responseMessage);
                objectMessage.setJMSCorrelationID(responseMessage.getCorrelationUid());
                objectMessage.setStringProperty(Constants.DOMAIN, responseMessage.getDomain());
                objectMessage.setStringProperty(Constants.DOMAIN_VERSION, responseMessage.getDomainVersion());
                objectMessage.setJMSType(responseMessage.getMessageType());
                objectMessage.setJMSPriority(responseMessage.getMessagePriority());
                objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                        responseMessage.getOrganisationIdentification());
                objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION,
                        responseMessage.getDeviceIdentification());
                objectMessage.setStringProperty(Constants.RESULT, responseMessage.getResult().toString());
                if (responseMessage.getOsgpException() != null) {
                    objectMessage.setStringProperty(Constants.DESCRIPTION,
                            responseMessage.getOsgpException().getMessage());
                }
                objectMessage.setBooleanProperty(Constants.IS_SCHEDULED, responseMessage.isScheduled());
                objectMessage.setIntProperty(Constants.RETRY_COUNT, responseMessage.getRetryCount());
                return objectMessage;
            }
        });
    }
}
