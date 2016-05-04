/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.infra.jms.protocol;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.alliander.osgp.core.domain.model.protocol.ProtocolRequestService;
import com.alliander.osgp.domain.core.entities.ProtocolInfo;
import com.alliander.osgp.dto.valueobjects.DeviceFunctionDto;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.ProtocolRequestMessage;

/**
 * This class sends protocol request messages to the requests queue for the
 * specific version of the protocol
 */
public class ProtocolRequestMessageSender implements ProtocolRequestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolRequestMessageSender.class);

    @Autowired
    private ProtocolRequestMessageJmsTemplateFactory protocolRequestMessageJmsTemplateFactory;

    @Autowired
    private Long getPowerUsageHistoryRequestTimeToLive;

    @Override
    public boolean isSupported(final ProtocolInfo protocolInfo) {
        return this.protocolRequestMessageJmsTemplateFactory.getJmsTemplate(protocolInfo) != null;
    }

    @Override
    public void send(final ProtocolRequestMessage message, final ProtocolInfo protocolInfo) {
        LOGGER.info("Sending protocol request message for device [{}] using protocol [{}] with version [{}]",
                message.getDeviceIdentification(), protocolInfo.getProtocol(), protocolInfo.getProtocolVersion());

        final JmsTemplate jmsTemplate = this.protocolRequestMessageJmsTemplateFactory.getJmsTemplate(protocolInfo);

        LOGGER.info("Message sender destination queue: [{}] for protocol [{}] with version [{}]",
                protocolInfo.getOutgoingProtocolRequestsQueue(), protocolInfo.getProtocol(),
                protocolInfo.getProtocolVersion());

        this.sendMessage(message, jmsTemplate);
    }

    private void sendMessage(final ProtocolRequestMessage requestMessage, final JmsTemplate jmsTemplate) {
        LOGGER.info("Sending request message to protocol requests queue");

        final Long originalTimeToLive = jmsTemplate.getTimeToLive();
        boolean isCustomTimeToLiveSet = false;
        if (requestMessage.getMessageType().equals(DeviceFunctionDto.GET_POWER_USAGE_HISTORY.toString())) {
            jmsTemplate.setTimeToLive(this.getPowerUsageHistoryRequestTimeToLive);
            isCustomTimeToLiveSet = true;
        }

        jmsTemplate.setPriority(requestMessage.getMessagePriority());
        jmsTemplate.send(new MessageCreator() {

            @Override
            public Message createMessage(final Session session) throws JMSException {
                final ObjectMessage objectMessage = session.createObjectMessage(requestMessage.getRequest());
                objectMessage.setJMSCorrelationID(requestMessage.getCorrelationUid());
                objectMessage.setJMSType(requestMessage.getMessageType());
                objectMessage.setStringProperty(Constants.DOMAIN, requestMessage.getDomain());
                objectMessage.setStringProperty(Constants.DOMAIN_VERSION, requestMessage.getDomainVersion());
                objectMessage.setStringProperty(Constants.ORGANISATION_IDENTIFICATION,
                        requestMessage.getOrganisationIdentification());
                objectMessage.setStringProperty(Constants.DEVICE_IDENTIFICATION,
                        requestMessage.getDeviceIdentification());
                objectMessage.setStringProperty(Constants.IP_ADDRESS, requestMessage.getIpAddress());
                objectMessage.setBooleanProperty(Constants.IS_SCHEDULED, requestMessage.isScheduled());
                objectMessage.setIntProperty(Constants.RETRY_COUNT, requestMessage.getRetryCount());
                return objectMessage;
            }

        });

        if (isCustomTimeToLiveSet) {
            jmsTemplate.setTimeToLive(originalTimeToLive);
        }
    }
}
