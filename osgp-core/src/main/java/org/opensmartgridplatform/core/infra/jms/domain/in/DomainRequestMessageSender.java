/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.domain.in;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import org.opensmartgridplatform.core.domain.model.domain.DomainRequestService;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;

// This class should send request messages to the domain incoming queue.
public class DomainRequestMessageSender implements DomainRequestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainRequestMessageSender.class);

    @Autowired
    private DomainRequestMessageJmsTemplateFactory domainRequestMessageJmsTemplateFactory;

    @Override
    public void send(final RequestMessage message, final String messageType, final DomainInfo domainInfo) {
        LOGGER.info("Sending domain incoming request message for device [{}] using domain [{}] with version [{}]",
                message.getDeviceIdentification(), domainInfo.getDomain(), domainInfo.getDomainVersion());

        final JmsTemplate jmsTemplate = this.domainRequestMessageJmsTemplateFactory.getJmsTemplate(domainInfo.getKey());

        this.sendMessage(message, messageType, jmsTemplate);
    }

    private void sendMessage(final RequestMessage requestMessage, final String messageType,
            final JmsTemplate jmsTemplate) {
        LOGGER.info(
                "Sending request message to incoming domain requests queue, messageType: {} organisationIdentification: {} deviceIdentification: {}",
                messageType, requestMessage.getOrganisationIdentification(), requestMessage.getDeviceIdentification());

        jmsTemplate.send(new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final ObjectMessage objectMessage = session.createObjectMessage(requestMessage);
                objectMessage.setJMSCorrelationID(requestMessage.getCorrelationUid());
                objectMessage.setJMSType(messageType);
                objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                        requestMessage.getOrganisationIdentification());
                objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION,
                        requestMessage.getDeviceIdentification());
                return objectMessage;
            }

        });
    }
}
