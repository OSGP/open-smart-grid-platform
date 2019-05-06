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

import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientConnectionService;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.IllegalStateException;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class DeviceResponseMessageSender implements ResponseMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceResponseMessageSender.class);

    private static final String LOG_MESSAGE_RESPONSE_MESSAGE_OF_WRONG_TYPE = "Only ProtocolResponseMessage type is expected for DeviceResponseMessageSender, received responseMessage of type {}.";
    private static final String LOG_MESSAGE_JMS_EXCEPTION = "JMS Exception, closing all connections.";
    private static final String LOG_MESSAGE_BLANK_FIELD = "{} is blank.";
    private static final String LOG_MESSAGE_NULL_FIELD = "{} is null.";

    @Autowired
    @Qualifier("iec60870ResponsesJmsTemplate")
    private JmsTemplate iec60870ResponsesJmsTemplate;

    @Autowired
    private ClientConnectionService iec60870DeviceConnectionService;

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
                this.iec60870DeviceConnectionService.closeAllConnections();
            }
            throw e;
        }
    }

    private boolean checkMessage(final ProtocolResponseMessage msg) {
        boolean result = true;

        if (StringUtils.isBlank(msg.getOrganisationIdentification())) {
            LOGGER.error(LOG_MESSAGE_BLANK_FIELD, "OrganisationIdentification");
            result = false;
        }
        if (StringUtils.isBlank(msg.getDeviceIdentification())) {
            LOGGER.error(LOG_MESSAGE_BLANK_FIELD, "DeviceIdentification");
            result = false;
        }
        if (StringUtils.isBlank(msg.getCorrelationUid())) {
            LOGGER.error(LOG_MESSAGE_BLANK_FIELD, "CorrelationUid");
            result = false;
        }
        if (msg.getResult() == null) {
            LOGGER.error(LOG_MESSAGE_NULL_FIELD, "Result");
            result = false;
        }
        if (StringUtils.isBlank(msg.getDomain())) {
            LOGGER.error(LOG_MESSAGE_BLANK_FIELD, "Domain");
            result = false;
        }
        if (StringUtils.isBlank(msg.getMessageType())) {
            LOGGER.error(LOG_MESSAGE_BLANK_FIELD, "MessageType");
            result = false;
        }

        return result;
    }

    private void sendMessage(final ProtocolResponseMessage responseMessage) {

        LOGGER.info(
                "Sending protocol response message [correlationUid={}, device={}, messageType={}, messagePriority={}]",
                responseMessage.getCorrelationUid(), responseMessage.getDeviceIdentification(),
                responseMessage.getMessageType(), responseMessage.getMessagePriority());

        final MessageCreator responseMessageCreator = new ResponseMessageCreator(responseMessage);
        this.iec60870ResponsesJmsTemplate.send(responseMessageCreator);
    }

    private class ResponseMessageCreator implements MessageCreator {

        private final ProtocolResponseMessage responseMessage;

        public ResponseMessageCreator(final ProtocolResponseMessage responseMessage) {
            this.responseMessage = responseMessage;
        }

        @Override
        public Message createMessage(final Session session) throws JMSException {
            final ObjectMessage objectMessage = session.createObjectMessage(this.responseMessage);
            objectMessage.setJMSCorrelationID(this.responseMessage.getCorrelationUid());
            objectMessage.setStringProperty(Constants.DOMAIN, this.responseMessage.getDomain());
            objectMessage.setStringProperty(Constants.DOMAIN_VERSION, this.responseMessage.getDomainVersion());
            objectMessage.setJMSType(this.responseMessage.getMessageType());
            objectMessage.setJMSPriority(this.responseMessage.getMessagePriority());
            objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                    this.responseMessage.getOrganisationIdentification());
            objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION,
                    this.responseMessage.getDeviceIdentification());
            objectMessage.setStringProperty(Constants.RESULT, this.responseMessage.getResult().toString());
            if (this.responseMessage.getOsgpException() != null) {
                objectMessage.setStringProperty(Constants.DESCRIPTION,
                        this.responseMessage.getOsgpException().getMessage());
            }
            objectMessage.setBooleanProperty(Constants.IS_SCHEDULED, this.responseMessage.isScheduled());
            objectMessage.setIntProperty(Constants.RETRY_COUNT, this.responseMessage.getRetryCount());
            return objectMessage;
        }
    }

}
