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

import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.ProtocolResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component(value = "protocolOslpOutboundOsgpCoreResponsesMessageSender")
public class DeviceResponseMessageSender implements ResponseMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceResponseMessageSender.class);

    @Autowired
    @Qualifier("protocolOslpOutboundOsgpCoreResponsesJmsTemplate")
    private JmsTemplate jmsTemplate;

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

        this.jmsTemplate.send(new DeviceResponseMessageCreator(responseMessage));
    }

    private static class DeviceResponseMessageCreator implements MessageCreator {

        private final ProtocolResponseMessage message;

        public DeviceResponseMessageCreator(final ProtocolResponseMessage message) {
            this.message = message;
        }

        @Override
        public Message createMessage(final Session session) throws JMSException {
            final ObjectMessage objMsg = session.createObjectMessage(this.message);
            objMsg.setJMSCorrelationID(this.message.getCorrelationUid());
            objMsg.setStringProperty(Constants.DOMAIN, this.message.getDomain());
            objMsg.setStringProperty(Constants.DOMAIN_VERSION, this.message.getDomainVersion());
            objMsg.setJMSType(this.message.getMessageType());
            objMsg.setJMSPriority(this.message.getMessagePriority());
            objMsg.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                    this.message.getOrganisationIdentification());
            objMsg.setStringProperty(Constants.DEVICE_IDENTIFICATION, this.message.getDeviceIdentification());
            objMsg.setStringProperty(Constants.RESULT, this.message.getResult().toString());
            if (this.message.getOsgpException() != null) {
                objMsg.setStringProperty(Constants.DESCRIPTION, this.message.getOsgpException().getMessage());
            }
            objMsg.setBooleanProperty(Constants.IS_SCHEDULED, this.message.isScheduled());
            objMsg.setIntProperty(Constants.RETRY_COUNT, this.message.getRetryCount());
            return objMsg;
        }
    }
}
