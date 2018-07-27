/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.protocol.in;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.opensmartgridplatform.dto.valueobjects.DeviceFunctionDto;
import org.opensmartgridplatform.shared.infra.jms.BaseMessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessor;

@Component("osgpCoreIncomingProtocolRequestMessageProcessorMap")
public class ProtocolRequestMessageProcessorMap extends BaseMessageProcessorMap {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolRequestMessageProcessorMap.class);

    public ProtocolRequestMessageProcessorMap() {
        super("ProtocolRequestMessageProcessorMap");
    }

    @Override
    public MessageProcessor getMessageProcessor(final ObjectMessage message) throws JMSException {

        if (message.getJMSType() == null) {
            LOGGER.error("Unknown message type: {}", message.getJMSType());
            throw new JMSException("Unknown message type");
        }

        final DeviceFunctionDto messageType = DeviceFunctionDto.valueOf(message.getJMSType());

        if (messageType.name() == null) {
            LOGGER.error("No message processor found for message type: {}", message.getJMSType());
            throw new JMSException("Unknown message processor");
        }

        return this.messageProcessors.get(messageType.ordinal());
    }
}
