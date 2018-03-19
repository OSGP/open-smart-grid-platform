/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.infra.jms.domain;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.alliander.osgp.core.domain.model.domain.DomainResponseService;
import com.alliander.osgp.domain.core.entities.DomainInfo;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.ProtocolRequestMessage;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

public class DomainResponseMessageSender implements DomainResponseService {

    @Autowired
    private DomainResponseMessageJmsTemplateFactory factory;

    @Override
    public void send(final ProtocolResponseMessage protocolResponseMessage) {

        final String key = DomainInfo.getKey(protocolResponseMessage.getDomain(),
                protocolResponseMessage.getDomainVersion());
        final JmsTemplate jmsTemplate = this.factory.getJmsTemplate(key);

        final ResponseMessage message = this.createResponseMessage(protocolResponseMessage);

        this.send(message, protocolResponseMessage.getMessageType(), jmsTemplate);
    }

    @Override
    public void send(final ProtocolRequestMessage protocolRequestMessage, final Exception e) {

        final String key = DomainInfo.getKey(protocolRequestMessage.getDomain(),
                protocolRequestMessage.getDomainVersion());
        final JmsTemplate jmsTemplate = this.factory.getJmsTemplate(key);

        final ResponseMessage message = this.createResponseMessage(protocolRequestMessage, e);

        this.send(message, protocolRequestMessage.getMessageType(), jmsTemplate);
    }

    private void send(final ResponseMessage message, final String messageType, final JmsTemplate jmsTemplate) {

        jmsTemplate.setPriority(message.getMessagePriority());
        jmsTemplate.send(new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final ObjectMessage objectMessage = session.createObjectMessage(message);
                objectMessage.setJMSType(messageType);
                objectMessage.setJMSCorrelationID(message.getCorrelationUid());
                objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                        message.getOrganisationIdentification());
                objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION, message.getDeviceIdentification());
                objectMessage.setStringProperty(Constants.RESULT, message.getResult().toString());
                if (message.getOsgpException() != null) {
                    objectMessage.setStringProperty(Constants.DESCRIPTION, message.getOsgpException().getMessage());
                }
                return objectMessage;
            }
        });
    }

    private ResponseMessage createResponseMessage(final ProtocolResponseMessage protocolResponseMessage) {

        return ResponseMessage.newResponseMessageBuilder()
                .withCorrelationUid(protocolResponseMessage.getCorrelationUid())
                .withOrganisationIdentification(protocolResponseMessage.getOrganisationIdentification())
                .withDeviceIdentification(protocolResponseMessage.getDeviceIdentification())
                .withResult(protocolResponseMessage.getResult())
                .withOsgpException(protocolResponseMessage.getOsgpException())
                .withDataObject(protocolResponseMessage.getDataObject())
                .withMessagePriority(protocolResponseMessage.getMessagePriority()).build();
    }

    private ResponseMessage createResponseMessage(final ProtocolRequestMessage protocolRequestMessage,
            final Exception e) {
        final OsgpException ex = this.ensureOsgpException(e);

        return ResponseMessage.newResponseMessageBuilder()
                .withCorrelationUid(protocolRequestMessage.getCorrelationUid())
                .withOrganisationIdentification(protocolRequestMessage.getOrganisationIdentification())
                .withDeviceIdentification(protocolRequestMessage.getDeviceIdentification())
                .withResult(ResponseMessageResultType.NOT_OK).withOsgpException(ex)
                .withMessagePriority(protocolRequestMessage.getMessagePriority()).build();
    }

    private OsgpException ensureOsgpException(final Exception e) {

        if (e instanceof OsgpException) {
            return (OsgpException) e;
        }

        return new TechnicalException(ComponentType.OSGP_CORE, "An unknown error occurred", e);
    }
}
