/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.messaging;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.IllegalStateException;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.Iec61850DeviceConnectionService;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageSender;

public class DeviceResponseMessageSender implements ResponseMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceResponseMessageSender.class);

    private static final String LOG_MESSAGE_RESPONSE_MESSAGE_OF_WRONG_TYPE = "Only ProtocolResponseMessage type is expected for DeviceResponseMessageSender, received responseMessage of type {}.";
    private static final String LOG_MESSAGE_JMS_EXCEPTION = "JMS Exception, closing all connections.";
    private static final String LOG_MESSAGE_BLANK_FIELD = "{} is blank.";
    private static final String LOG_MESSAGE_NULL_FIELD = "{} is null.";

    @Autowired
    @Qualifier("iec61850ResponsesJmsTemplate")
    private JmsTemplate iec61850ResponsesJmsTemplate;

    @Autowired
    private Iec61850DeviceConnectionService iec61850deviceConnectionService;

    @Autowired
    private boolean isCloseConnectionsOnBrokerFailure;

    @Override
    public void send(final ResponseMessage responseMessage) {
        if (!(responseMessage instanceof ProtocolResponseMessage)) {
            LOGGER.error(LOG_MESSAGE_RESPONSE_MESSAGE_OF_WRONG_TYPE, responseMessage.getClass().getName());
            return;
        }

        final ProtocolResponseMessage msg = (ProtocolResponseMessage) responseMessage;

        if (!this.checkMessage(msg)) {
            return;
        }

        try {
            this.sendMessage(msg);
        } catch (final IllegalStateException | UncategorizedJmsException e) {
            /*
             * IllegalStateException occurs when activemq connection pool is
             * exhausted or activemq failover timeout is reached
             * UncategorizedJmsException might also occur when activemq failover
             * timeout is reached
             */
            if (this.isCloseConnectionsOnBrokerFailure) {
                LOGGER.error(LOG_MESSAGE_JMS_EXCEPTION, e);
                this.iec61850deviceConnectionService.closeAllConnections();
            }
            throw e;
        }
    }

    private boolean checkMessage(final ProtocolResponseMessage msg) {
        if (StringUtils.isBlank(msg.getOrganisationIdentification())) {
            LOGGER.error(LOG_MESSAGE_BLANK_FIELD, "OrganisationIdentification");
            return false;
        }
        if (StringUtils.isBlank(msg.getDeviceIdentification())) {
            LOGGER.error(LOG_MESSAGE_BLANK_FIELD, "DeviceIdentification");
            return false;
        }
        if (StringUtils.isBlank(msg.getCorrelationUid())) {
            LOGGER.error(LOG_MESSAGE_BLANK_FIELD, "CorrelationUid");
            return false;
        }
        if (msg.getResult() == null) {
            LOGGER.error(LOG_MESSAGE_NULL_FIELD, "Result");
            return false;
        }
        if (StringUtils.isBlank(msg.getDomain())) {
            LOGGER.error(LOG_MESSAGE_BLANK_FIELD, "Domain");
            return false;
        }
        if (StringUtils.isBlank(msg.getMessageType())) {
            LOGGER.error(LOG_MESSAGE_BLANK_FIELD, "MessageType");
            return false;
        }

        return true;
    }

    private void sendMessage(final ProtocolResponseMessage responseMessage) {
        this.iec61850ResponsesJmsTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(final Session session) throws JMSException {
                final ObjectMessage objectMessage = session.createObjectMessage(responseMessage);
                objectMessage.setJMSCorrelationID(responseMessage.getCorrelationUid());
                objectMessage.setStringProperty(Constants.DOMAIN, responseMessage.getDomain());
                objectMessage.setStringProperty(Constants.DOMAIN_VERSION, responseMessage.getDomainVersion());
                objectMessage.setJMSType(responseMessage.getMessageType());
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
