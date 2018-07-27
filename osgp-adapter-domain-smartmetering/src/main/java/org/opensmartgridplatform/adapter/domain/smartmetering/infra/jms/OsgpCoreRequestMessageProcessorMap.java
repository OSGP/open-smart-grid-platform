/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.opensmartgridplatform.dto.valueobjects.DeviceFunctionDto;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;

@Component("domainSmartMeteringOsgpCoreRequestMessageProcessorMap")
public class OsgpCoreRequestMessageProcessorMap extends BaseMessageProcessorMap {

    private static final Logger LOGGER = LoggerFactory.getLogger(OsgpCoreRequestMessageProcessorMap.class);

    protected OsgpCoreRequestMessageProcessorMap() {
        super("OsgpCoreRequestMessageProcessorMap");
    }

    @Override
    public MessageProcessor getMessageProcessor(final ObjectMessage message) throws JMSException {

        if (message.getJMSType() == null) {
            LOGGER.error("No JMS type in ObjectMessage");
            throw new JMSException("No message type");
        }

        try {

            final DeviceFunctionDto messageType = DeviceFunctionDto.valueOf(message.getJMSType());
            final MessageProcessor messageProcessor = this.messageProcessors.get(messageType.ordinal());
            if (messageProcessor == null) {
                throw new IllegalArgumentException("No message processor registered for type " + messageType.name()
                        + ", ordinal: " + messageType.ordinal());
            }
            return messageProcessor;

        } catch (final Exception e) {

            LOGGER.error("No message processor found for message type: " + message.getJMSType(), e);
            throw new JMSException("No message processor found for message type");
        }
    }
}
