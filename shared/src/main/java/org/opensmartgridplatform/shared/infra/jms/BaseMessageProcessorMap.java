/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.infra.jms;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseMessageProcessorMap implements MessageProcessorMap {

    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseMessageProcessorMap.class);

    protected Map<Integer, MessageProcessor> messageProcessors = new HashMap<>();

    protected final String messageProcessorMapName;

    public BaseMessageProcessorMap(final String messageProcessorMapName) {
        this.messageProcessorMapName = messageProcessorMapName;
    }

    @Override
    public void setMessageProcessors(final Map<Integer, MessageProcessor> messageProcessors) {
        this.messageProcessors = messageProcessors;
    }

    @Override
    public void addMessageProcessor(final Integer key, final String messageType, final MessageProcessor messageProcessor) {

        LOGGER.info("Putting MessageProcessor in {} with key: {} for MessageType: {}", this.messageProcessorMapName,
                key, messageType);

        this.messageProcessors.put(key, messageProcessor);
    }

    @Override
    public abstract MessageProcessor getMessageProcessor(ObjectMessage message) throws JMSException;
}